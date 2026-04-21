package com.vinrecipe.controller;

import com.vinrecipe.model.Admin;
import com.vinrecipe.model.RoomLeader;
import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for MainLayout.fxml.
 * Manages the BorderPane shell: sidebar navigation + center content area.
 * Implements the Single Window pattern: swap content in Center pane, never open new Stage.
 *
 * Role-Based UI:
 *  - Admin (permissionLevel 3): Full control over all recipes
 *  - RoomLeader (permissionLevel 2): Sees "👥 My Room" nav button; can edit/delete roommates' recipes
 *  - NormalStudent (permissionLevel 1): Can only manage own recipes
 */
public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentArea;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleBadge;
    @FXML private Button myRoomBtn;

    private User currentUser;
    private final RecipeService recipeService = new RecipeService();
    private final SearchService searchService = new SearchService();

    /** Called by LoginController after successful login. */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        setupUserBadge(user);
        // Load all recipes into search index
        searchService.buildIndex(recipeService.getAllRecipes());
        // Show dashboard by default
        loadView("/fxml/views/DashboardView.fxml");
    }

    /**
     * Update the sidebar user info card based on the logged-in user's role.
     * Demonstrates Polymorphism: getPermissionLevel() returns different values per subclass.
     */
    private void setupUserBadge(User user) {
        userNameLabel.setText(user.getUsername());

        if (user instanceof Admin) {
            userRoleBadge.setText("🛡 Admin");
            userRoleBadge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                    + "-fx-padding: 2 10; -fx-background-radius: 12; -fx-font-size: 11px;");
        } else if (user instanceof RoomLeader) {
            userRoleBadge.setText("👑 Room Leader");
            userRoleBadge.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; "
                    + "-fx-padding: 2 10; -fx-background-radius: 12; -fx-font-size: 11px;");
            // Show "My Room" nav button only for Room Leaders
            myRoomBtn.setVisible(true);
            myRoomBtn.setManaged(true);
        } else {
            userRoleBadge.setText("🎓 Student");
            userRoleBadge.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                    + "-fx-padding: 2 10; -fx-background-radius: 12; -fx-font-size: 11px;");
            // Students do NOT see the "My Room" management button
            myRoomBtn.setVisible(false);
            myRoomBtn.setManaged(false);
        }
    }

    /** Navigation: go to Dashboard. */
    @FXML private void showDashboard()    { loadView("/fxml/views/DashboardView.fxml"); }

    /** Navigation: go to Search. */
    @FXML private void showSearch()       { loadView("/fxml/views/SearchView.fxml"); }

    /** Navigation: go to Shopping List. */
    @FXML private void showShoppingList() { loadView("/fxml/views/ShoppingListView.fxml"); }

    /** Navigation: go to Add New Recipe form. */
    @FXML private void showAddRecipe()    { loadView("/fxml/views/RecipeFormView.fxml"); }

    /**
     * My Room — only accessible to RoomLeader.
     * Shows room members and allows the leader to manage the room.
     */
    @FXML
    private void showMyRoom() {
        loadView("/fxml/views/MyRoomView.fxml");
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

    public User getCurrentUser()          { return currentUser; }
    public RecipeService getRecipeService(){ return recipeService; }
    public SearchService getSearchService(){ return searchService; }
}
