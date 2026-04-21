package com.vinrecipe.controller;

import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for MainLayout.fxml.
 * Manages the BorderPane shell: sidebar navigation + center content area.
 * Implements the Single Window pattern: swap content in Center pane, never open new Stage.
 */
public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentArea;

    private User currentUser;
    private final RecipeService recipeService = new RecipeService();
    private final SearchService searchService = new SearchService();

    /** Called by LoginController after successful login. */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Load all recipes into search index
        searchService.buildIndex(recipeService.getAllRecipes());
        // Show dashboard by default
        loadView("/fxml/views/DashboardView.fxml");
    }

    /** Navigation: go to Dashboard. */
    @FXML
    private void showDashboard() {
        loadView("/fxml/views/DashboardView.fxml");
    }

    /** Navigation: go to Search. */
    @FXML
    private void showSearch() {
        loadView("/fxml/views/SearchView.fxml");
    }

    /** Navigation: go to Shopping List. */
    @FXML
    private void showShoppingList() {
        loadView("/fxml/views/ShoppingListView.fxml");
    }

    /** Navigation: go to Add New Recipe form. */
    @FXML
    private void showAddRecipe() {
        loadView("/fxml/views/RecipeFormView.fxml");
    }

    /**
     * Load an FXML view into the center StackPane.
     * Passes context (currentUser, services) to child controllers if they implement Initializable.
     */
    public void loadView(String fxmlPath) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                System.err.println("[MainController] FXML not found: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();
            // Inject context into child controller if it needs it
            Object controller = loader.getController();
            if (controller instanceof ContextAware) {
                ((ContextAware) controller).setContext(currentUser, recipeService, searchService, this);
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("[MainController] Failed to load view " + fxmlPath + ": " + e.getMessage());
        }
    }

    /** Load a pre-built Node directly into the content area (used by DashboardController). */
    public void loadViewNode(Node view) {
        contentArea.getChildren().setAll(view);
    }

    public User getCurrentUser() { return currentUser; }
    public RecipeService getRecipeService() { return recipeService; }
    public SearchService getSearchService() { return searchService; }
}
