package com.vinrecipe.controller;

import com.vinrecipe.model.*;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for RecipeDetailView.fxml.
 * Shows full recipe info: ingredients list, instructions, tags, and action buttons.
 */
public class RecipeDetailController implements ContextAware {

    @FXML private Label titleLabel;
    @FXML private Label ratingLabel;
    @FXML private Label timeLabel;
    @FXML private Label servingsLabel;
    @FXML private Label tagsLabel;
    @FXML private TextArea instructionsArea;
    @FXML private ListView<String> ingredientList;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Label authorLabel;

    private User currentUser;
    private RecipeService recipeService;
    private SearchService searchService;
    private MainController mainController;
    private Recipe recipe;

    @Override
    public void setContext(User currentUser, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = currentUser;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;
    }

    /** Populate all fields with the given recipe. */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;

        titleLabel.setText(recipe.getTitle());
        ratingLabel.setText("★ " + String.format("%.1f", recipe.getRating()));
        timeLabel.setText("Prep: " + recipe.getPrepTime() + " min  |  Cook: " + recipe.getCookTime()
                          + " min  |  Total: " + recipe.getTotalTime() + " min");
        servingsLabel.setText("Servings: " + recipe.getServings());

        String tagsText = recipe.getTags().stream()
                .map(t -> "#" + t.getName())
                .reduce((a, b) -> a + "  " + b).orElse("No tags");
        tagsLabel.setText(tagsText);

        String authorName = recipe.getAuthor() != null ? recipe.getAuthor().getUsername() : "Unknown";
        authorLabel.setText("By: " + authorName);

        instructionsArea.setText(recipe.getInstructions() != null ? recipe.getInstructions() : "");
        instructionsArea.setEditable(false);

        // Populate ingredients
        ingredientList.getItems().clear();
        for (Ingredient ing : recipe.getIngredients()) {
            ingredientList.getItems().add(
                    String.format("• %.1f %s %s  (~%.0f VND)",
                            ing.getQuantity(), ing.getUnit(), capitalize(ing.getName()), ing.getPriceEstimate()));
        }

        // Show edit/delete only if user is author or admin
        boolean canModify = currentUser instanceof Admin
                || (recipe.getAuthor() != null
                    && recipe.getAuthor().getUserId() == currentUser.getUserId());
        editBtn.setVisible(canModify);
        deleteBtn.setVisible(canModify);
    }

    @FXML
    private void handleEdit() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/views/RecipeFormView.fxml"));
            javafx.scene.Node view = loader.load();
            RecipeFormController ctrl = loader.getController();
            ctrl.setContext(currentUser, recipeService, searchService, mainController);
            ctrl.setEditMode(recipe);
            mainController.loadViewNode(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + recipe.getTitle() + "\"? This cannot be undone.",
                ButtonType.YES, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm Deletion");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                recipeService.deleteRecipe(recipe.getRecipeId());
                searchService.buildIndex(recipeService.getAllRecipes());
                mainController.loadView("/fxml/views/DashboardView.fxml");
            }
        });
    }

    @FXML
    private void handleBack() {
        mainController.loadView("/fxml/views/DashboardView.fxml");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
