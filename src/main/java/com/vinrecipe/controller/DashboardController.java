package com.vinrecipe.controller;

import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;

/**
 * Controller for DashboardView.fxml.
 * Displays all recipes as cards in a FlowPane with sort options.
 */
public class DashboardController implements ContextAware {

    @FXML private FlowPane recipeGrid;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Label userLabel;
    @FXML private Label statusLabel;

    private User currentUser;
    private RecipeService recipeService;
    private SearchService searchService;
    private MainController mainController;

    private List<Recipe> recipes;

    @Override
    public void setContext(User currentUser, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = currentUser;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;

        userLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        sortCombo.setItems(FXCollections.observableArrayList("Newest First", "Top Rated", "Quickest", "Cheapest"));
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applySort());
        loadRecipes();
    }

    @FXML
    private void initialize() {
        // intentionally blank — context injected by setContext()
    }

    private void loadRecipes() {
        recipes = recipeService.getAllRecipes();
        if (recipes.isEmpty()) {
            statusLabel.setText("No recipes yet. Click 'Add Recipe' to create one!");
        } else {
            statusLabel.setText("");
        }
        renderCards(recipes);
    }

    private void applySort() {
        if (recipes == null) return;
        String selected = sortCombo.getValue();
        List<Recipe> sorted;
        if ("Top Rated".equals(selected)) {
            sorted = searchService.sortByRating(recipes);
        } else if ("Quickest".equals(selected)) {
            sorted = searchService.sortByPrepTime(recipes);
        } else if ("Cheapest".equals(selected)) {
            sorted = searchService.sortByPrice(recipes);
        } else {
            // Newest first — default order from DB (already DESC by created_at)
            sorted = recipes;
        }
        renderCards(sorted);
    }

    private void renderCards(List<Recipe> list) {
        recipeGrid.getChildren().clear();
        for (Recipe recipe : list) {
            recipeGrid.getChildren().add(buildCard(recipe));
        }
    }

    /** Build a recipe card VBox programmatically. */
    private VBox buildCard(Recipe recipe) {
        VBox card = new VBox(6);
        card.getStyleClass().add("recipe-card");
        card.setPrefWidth(200);

        // Title
        Label title = new Label(recipe.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        // Rating stars
        Label rating = new Label("★ " + String.format("%.1f", recipe.getRating()));
        rating.getStyleClass().add("card-rating");

        // Time
        Label time = new Label("⏱ " + recipe.getTotalTime() + " min");
        time.getStyleClass().add("card-meta");

        // Price
        Label price = new Label("💰 " + String.format("%,.0f", recipe.getTotalPrice()) + " VND");
        price.getStyleClass().add("card-meta");

        // Author
        String authorName = recipe.getAuthor() != null ? recipe.getAuthor().getUsername() : "Unknown";
        Label author = new Label("👤 " + authorName);
        author.getStyleClass().add("card-meta");

        // Tags
        Label tags = new Label();
        if (!recipe.getTags().isEmpty()) {
            String tagText = recipe.getTags().stream()
                    .map(t -> "#" + t.getName())
                    .reduce((a, b) -> a + " " + b).orElse("");
            tags.setText(tagText);
            tags.getStyleClass().add("card-tags");
            tags.setWrapText(true);
        }

        card.getChildren().addAll(title, rating, time, price, author, tags);

        // Click → show detail
        card.setOnMouseClicked(event -> showDetail(recipe));
        return card;
    }

    private void showDetail(Recipe recipe) {
        // Load RecipeDetailView and pass recipe
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/views/RecipeDetailView.fxml"));
            javafx.scene.Node view = loader.load();
            RecipeDetailController ctrl = loader.getController();
            ctrl.setContext(currentUser, recipeService, searchService, mainController);
            ctrl.setRecipe(recipe);
            mainController.loadViewNode(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddRecipe() {
        mainController.loadView("/fxml/views/RecipeFormView.fxml");
    }

    @FXML
    private void handleRefresh() {
        searchService.buildIndex(recipeService.getAllRecipes());
        loadRecipes();
    }
}
