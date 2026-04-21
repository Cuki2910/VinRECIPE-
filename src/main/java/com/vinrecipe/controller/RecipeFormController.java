package com.vinrecipe.controller;

import com.vinrecipe.model.*;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for RecipeFormView.fxml.
 * Handles both Create and Edit modes for recipes.
 */
public class RecipeFormController implements ContextAware {

    @FXML private Label formTitleLabel;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea instructionsArea;
    @FXML private TextField prepTimeField;
    @FXML private TextField cookTimeField;
    @FXML private TextField servingsField;
    @FXML private TextField ratingField;

    // Ingredient input row
    @FXML private TextField ingNameField;
    @FXML private TextField ingQtyField;
    @FXML private TextField ingUnitField;
    @FXML private TextField ingPriceField;

    @FXML private TableView<Ingredient> ingredientTable;
    @FXML private TableColumn<Ingredient, String> ingNameCol;
    @FXML private TableColumn<Ingredient, Double> ingQtyCol;
    @FXML private TableColumn<Ingredient, String> ingUnitCol;
    @FXML private TableColumn<Ingredient, Double> ingPriceCol;

    @FXML private ComboBox<Tag> tagCombo;
    @FXML private ListView<Tag> selectedTagsList;
    @FXML private Label statusLabel;

    private User currentUser;
    private RecipeService recipeService;
    private SearchService searchService;
    private MainController mainController;

    private Recipe editingRecipe = null; // null = create mode
    private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
    private final ObservableList<Tag> selectedTags      = FXCollections.observableArrayList();

    @Override
    public void setContext(User currentUser, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = currentUser;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;

        setupTable();
        loadTags();
        formTitleLabel.setText("Add New Recipe");
    }

    /** Call this to switch to Edit mode for an existing recipe. */
    public void setEditMode(Recipe recipe) {
        this.editingRecipe = recipe;
        formTitleLabel.setText("Edit Recipe");
        titleField.setText(recipe.getTitle());
        descriptionArea.setText(recipe.getDescription() != null ? recipe.getDescription() : "");
        instructionsArea.setText(recipe.getInstructions() != null ? recipe.getInstructions() : "");
        prepTimeField.setText(String.valueOf(recipe.getPrepTime()));
        cookTimeField.setText(String.valueOf(recipe.getCookTime()));
        servingsField.setText(String.valueOf(recipe.getServings()));
        ratingField.setText(String.format("%.1f", recipe.getRating()));
        ingredients.setAll(recipe.getIngredients());
        selectedTags.setAll(recipe.getTags());
    }

    private void setupTable() {
        ingNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ingUnitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        ingPriceCol.setCellValueFactory(new PropertyValueFactory<>("priceEstimate"));
        ingredientTable.setItems(ingredients);
        selectedTagsList.setItems(selectedTags);
    }

    private void loadTags() {
        List<Tag> tags = recipeService.getAllTags();
        tagCombo.setItems(FXCollections.observableArrayList(tags));
    }

    @FXML
    private void handleAddIngredient() {
        try {
            String name  = ingNameField.getText().trim();
            double qty   = Double.parseDouble(ingQtyField.getText().trim());
            String unit  = ingUnitField.getText().trim();
            double price = ingPriceField.getText().isBlank() ? 0
                           : Double.parseDouble(ingPriceField.getText().trim());
            if (name.isEmpty()) { showStatus("Ingredient name required", true); return; }
            Ingredient ing = new Ingredient(0, name, qty, unit, price);
            ingredients.add(ing);
            ingNameField.clear(); ingQtyField.clear(); ingUnitField.clear(); ingPriceField.clear();
        } catch (NumberFormatException e) {
            showStatus("Quantity / price must be a number", true);
        }
    }

    @FXML
    private void handleRemoveIngredient() {
        Ingredient selected = ingredientTable.getSelectionModel().getSelectedItem();
        if (selected != null) ingredients.remove(selected);
    }

    @FXML
    private void handleAddTag() {
        Tag selected = tagCombo.getValue();
        if (selected != null && !selectedTags.contains(selected)) {
            selectedTags.add(selected);
        }
    }

    @FXML
    private void handleRemoveTag() {
        Tag selected = selectedTagsList.getSelectionModel().getSelectedItem();
        if (selected != null) selectedTags.remove(selected);
    }

    @FXML
    private void handleSave() {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) { showStatus("Title is required", true); return; }
            int prepTime  = Integer.parseInt(prepTimeField.getText().trim());
            int cookTime  = Integer.parseInt(cookTimeField.getText().trim());
            int servings  = Integer.parseInt(servingsField.getText().trim());
            double rating = ratingField.getText().isBlank() ? 0
                            : Double.parseDouble(ratingField.getText().trim());

            Recipe recipe = editingRecipe != null ? editingRecipe : new Recipe();
            recipe.setTitle(title);
            recipe.setDescription(descriptionArea.getText());
            recipe.setInstructions(instructionsArea.getText());
            recipe.setPrepTime(prepTime);
            recipe.setCookTime(cookTime);
            recipe.setServings(servings);
            recipe.setRating(rating);
            recipe.setAuthor(currentUser);
            recipe.setIngredients(new ArrayList<>(ingredients));
            recipe.setTags(new ArrayList<>(selectedTags));

            boolean success;
            if (editingRecipe != null) {
                success = recipeService.updateRecipe(recipe);
            } else {
                success = recipeService.createRecipe(recipe) != -1;
            }

            if (success) {
                searchService.buildIndex(recipeService.getAllRecipes());
                mainController.loadView("/fxml/views/DashboardView.fxml");
            } else {
                showStatus("Failed to save recipe. Check input and try again.", true);
            }
        } catch (NumberFormatException e) {
            showStatus("Prep time, cook time, and servings must be whole numbers.", true);
        }
    }

    @FXML
    private void handleCancel() {
        mainController.loadView("/fxml/views/DashboardView.fxml");
    }

    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().removeAll("status-error", "status-success");
        statusLabel.getStyleClass().add(isError ? "status-error" : "status-success");
    }
}
