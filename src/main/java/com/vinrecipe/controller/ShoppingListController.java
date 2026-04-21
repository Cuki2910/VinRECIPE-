package com.vinrecipe.controller;

import com.vinrecipe.model.Ingredient;
import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Shopping List Controller — Ingredient-based cart.
 *
 * Users search for ingredients, set quantity/unit, add to cart.
 * The cart shows a TableView with real-time price calculation.
 *
 * Algorithm: HashMap<String, CartItem> aggregates duplicate names.
 * Complexity: O(n) for search via cached ingredient list.
 */
public class ShoppingListController implements ContextAware {

    // ---- FXML fields ----
    @FXML private TextField searchField;
    @FXML private TextField quantityField;
    @FXML private TextField unitField;
    @FXML private ListView<Ingredient> ingredientSearchList;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, String> colQty;
    @FXML private TableColumn<CartItem, String> colUnit;
    @FXML private TableColumn<CartItem, String> colPrice;
    @FXML private TableColumn<CartItem, String> colRemove;

    @FXML private Label totalPriceLabel;
    @FXML private Label instructionLabel;

    // ---- State ----
    private User currentUser;
    private RecipeService recipeService;
    private SearchService searchService;
    private MainController mainController;

    /** Master list of all ingredients (cached in RAM to avoid repeated DB calls — reduces lag). */
    private List<Ingredient> allIngredients = new ArrayList<>();
    /** Observable cart items driving the TableView. */
    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // ============================================================
    // ContextAware
    // ============================================================
    @Override
    public void setContext(User user, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = user;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;

        loadIngredientCache();
        setupTable();
        showAllIngredients();
    }

    // ============================================================
    // Setup
    // ============================================================

    /**
     * Load ALL ingredients once from all recipes and cache in RAM.
     * This is the key optimisation: one DB round-trip, then all
     * subsequent search/filter operations run locally at O(n).
     */
    private void loadIngredientCache() {
        allIngredients.clear();
        List<Recipe> recipes = recipeService.getAllRecipes();
        for (Recipe r : recipes) {
            if (r.getIngredients() != null) {
                allIngredients.addAll(r.getIngredients());
            }
        }
        // Deduplicate by name so the search list is clean
        java.util.Map<String, Ingredient> deduped = new java.util.LinkedHashMap<>();
        for (Ingredient ing : allIngredients) {
            deduped.putIfAbsent(ing.getName(), ing);
        }
        allIngredients = new ArrayList<>(deduped.values());
    }

    private void setupTable() {
        colName.setCellValueFactory(d -> new SimpleStringProperty(capitalize(d.getValue().getName())));
        colQty.setCellValueFactory(d  -> new SimpleStringProperty(String.format("%.1f", d.getValue().getQuantity())));
        colUnit.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUnit()));
        colPrice.setCellValueFactory(d -> {
            double total = d.getValue().getQuantity() * d.getValue().getPricePerUnit();
            return new SimpleStringProperty(String.format("%,.0f", total));
        });
        colRemove.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            {
                btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 2 8;");
                btn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartItems.remove(item);
                    refreshTotal();
                });
            }
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                setGraphic(empty ? null : btn);
            }
        });

        cartTable.setItems(cartItems);

        // Custom cell for ingredient search list
        ingredientSearchList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Ingredient ing, boolean empty) {
                super.updateItem(ing, empty);
                if (empty || ing == null) { setText(null); return; }
                setText(capitalize(ing.getName())
                        + "  (" + ing.getQuantity() + " " + ing.getUnit() + ")"
                        + "  ~" + String.format("%,.0f VND", ing.getPriceEstimate()));
            }
        });

        // When user clicks an ingredient in the search list, auto-fill fields
        ingredientSearchList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                unitField.setText(selected.getUnit());
                if (quantityField.getText().isBlank()) {
                    quantityField.setText(String.valueOf((int) selected.getQuantity()));
                }
            }
        });
    }

    private void showAllIngredients() {
        ingredientSearchList.getItems().setAll(allIngredients);
    }

    // ============================================================
    // FXML Actions
    // ============================================================

    /** Real-time filter as user types — purely in-memory, no DB hit. */
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            showAllIngredients();
            return;
        }
        List<Ingredient> filtered = allIngredients.stream()
                .filter(i -> i.getName().contains(query))
                .toList();
        ingredientSearchList.getItems().setAll(filtered);
    }

    /** Add the selected/typed ingredient to the cart. */
    @FXML
    private void handleAddIngredient() {
        Ingredient selected = ingredientSearchList.getSelectionModel().getSelectedItem();

        // Build the name from selection or search field
        String name = (selected != null) ? selected.getName()
                : searchField.getText().trim().toLowerCase();
        if (name.isEmpty()) {
            instructionLabel.setText("⚠ Please select or type an ingredient name.");
            return;
        }

        // Parse quantity
        double qty;
        try {
            qty = Double.parseDouble(quantityField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            instructionLabel.setText("⚠ Please enter a valid quantity (e.g. 200).");
            return;
        }

        String unit = unitField.getText().trim();
        if (unit.isEmpty() && selected != null) unit = selected.getUnit();

        double pricePerUnit = (selected != null && selected.getQuantity() > 0)
                ? selected.getPriceEstimate() / selected.getQuantity()
                : 0;

        // Merge with existing cart item of same name (HashMap-like merge using ObservableList)
        CartItem existing = cartItems.stream()
                .filter(ci -> ci.getName().equals(name))
                .findFirst().orElse(null);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            cartTable.refresh();
        } else {
            cartItems.add(new CartItem(name, qty, unit, pricePerUnit));
        }

        refreshTotal();
        instructionLabel.setText("✓ Added: " + capitalize(name) + " (" + qty + " " + unit + ")");

        // Reset fields
        quantityField.clear();
    }

    @FXML
    private void handleClear() {
        cartItems.clear();
        totalPriceLabel.setText("0 VND");
        instructionLabel.setText("Search and add ingredients to your cart");
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void refreshTotal() {
        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getQuantity() * ci.getPricePerUnit())
                .sum();
        totalPriceLabel.setText(String.format("%,.0f VND", total));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ============================================================
    // Inner CartItem model (holds one row in the cart TableView)
    // ============================================================

    /**
     * CartItem — a simple value class for the shopping cart.
     * Encapsulates name, quantity, unit, and price per unit.
     * Used by the TableView to display cart contents.
     */
    public static class CartItem {
        private String name;
        private double quantity;
        private final String unit;
        private final double pricePerUnit;

        public CartItem(String name, double quantity, String unit, double pricePerUnit) {
            this.name         = name;
            this.quantity     = quantity;
            this.unit         = unit;
            this.pricePerUnit = pricePerUnit;
        }

        public String getName()         { return name; }
        public double getQuantity()     { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public String getUnit()         { return unit; }
        public double getPricePerUnit() { return pricePerUnit; }
    }
}
