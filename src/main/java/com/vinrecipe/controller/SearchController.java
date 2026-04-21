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

import java.util.*;

/**
 * Controller for SearchView.fxml.
 * Supports title search (LIKE query) and ingredient-based inverted index search.
 */
public class SearchController implements ContextAware {

    @FXML private TextField titleSearchField;
    @FXML private TextField ingredientSearchField;  // comma-separated ingredients
    @FXML private ComboBox<String> tagFilterCombo;
    @FXML private FlowPane resultsPane;
    @FXML private Label resultCountLabel;

    private User currentUser;
    private RecipeService recipeService;
    private SearchService searchService;
    private MainController mainController;

    @Override
    public void setContext(User currentUser, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = currentUser;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;

        // Load unique tags for filter dropdown, add "All" option
        List<String> tagOptions = new ArrayList<>();
        tagOptions.add("All Tags");
        tagOptions.addAll(searchService.getUniqueTagNames().stream().sorted().toList());
        tagFilterCombo.setItems(FXCollections.observableArrayList(tagOptions));
        tagFilterCombo.getSelectionModel().selectFirst();

        // Show all recipes initially
        renderResults(recipeService.getAllRecipes());
    }

    /** Search by title keyword. */
    @FXML
    private void handleTitleSearch() {
        String keyword = titleSearchField.getText().trim();
        if (keyword.isEmpty()) {
            renderResults(recipeService.getAllRecipes());
        } else {
            renderResults(recipeService.searchByTitle(keyword));
        }
    }

    /**
     * Search by available ingredients using Inverted Index algorithm.
     * Input: comma-separated ingredient names.
     */
    @FXML
    private void handleIngredientSearch() {
        String input = ingredientSearchField.getText().trim();
        if (input.isEmpty()) {
            renderResults(searchService.getAllRecipes());
            return;
        }
        List<String> userIngredients = Arrays.asList(input.split(","));
        userIngredients = userIngredients.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        List<Recipe> results = searchService.searchByIngredients(userIngredients);
        renderResults(results);
    }

    /** Filter results by tag. */
    @FXML
    private void handleTagFilter() {
        String selected = tagFilterCombo.getValue();
        if (selected == null || "All Tags".equals(selected)) {
            renderResults(recipeService.getAllRecipes());
        } else {
            renderResults(searchService.filterByTag(selected));
        }
    }

    @FXML
    private void handleClear() {
        titleSearchField.clear();
        ingredientSearchField.clear();
        tagFilterCombo.getSelectionModel().selectFirst();
        renderResults(recipeService.getAllRecipes());
    }

    private void renderResults(List<Recipe> recipes) {
        resultsPane.getChildren().clear();
        resultCountLabel.setText(recipes.size() + " recipe(s) found");
        for (Recipe recipe : recipes) {
            resultsPane.getChildren().add(buildCard(recipe));
        }
    }

    private VBox buildCard(Recipe recipe) {
        VBox card = new VBox(6);
        card.getStyleClass().add("recipe-card");
        card.setPrefWidth(190);

        Label title  = new Label(recipe.getTitle());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);

        Label rating = new Label("★ " + String.format("%.1f", recipe.getRating()));
        rating.getStyleClass().add("card-rating");

        Label time   = new Label("⏱ " + recipe.getTotalTime() + " min");
        time.getStyleClass().add("card-meta");

        card.getChildren().addAll(title, rating, time);
        card.setOnMouseClicked(e -> showDetail(recipe));
        return card;
    }

    private void showDetail(Recipe recipe) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/views/RecipeDetailView.fxml"));
            javafx.scene.Node view = loader.load();
            RecipeDetailController ctrl = loader.getController();
            ctrl.setContext(currentUser, recipeService, searchService, mainController);
            ctrl.setRecipe(recipe);
            mainController.loadViewNode(view);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
