package com.vinrecipe.controller;

import com.vinrecipe.dao.UserDAO;
import com.vinrecipe.model.NormalStudent;
import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.RoomLeader;
import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for MyRoomView.fxml.
 * Only shown to RoomLeader users (enforced by MainController via instanceof check).
 */
public class MyRoomController implements ContextAware {

    @FXML private ListView<String> memberListView;
    @FXML private ListView<Recipe> recipeListView;
    @FXML private Label roomIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label memberCountLabel;
    @FXML private Label recipeCountLabel;

    private User currentUser;
    private MainController mainController;
    private RecipeService recipeService;
    private SearchService searchService;
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void setContext(User user, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = user;
        this.recipeService  = recipeService;
        this.searchService  = searchService;
        this.mainController = mainController;

        setupRecipeList();

        if (user instanceof RoomLeader leader) {
            loadRoomData(leader);
        } else {
            statusLabel.setText("⚠ Access restricted to Room Leaders only.");
        }
    }

    private void setupRecipeList() {
        // Custom cell to display recipe title and author cleanly
        recipeListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String author = item.getAuthor() != null ? item.getAuthor().getUsername() : "Unknown";
                    setText("🍲 " + item.getTitle() + "  (By: " + author + ")");
                }
            }
        });

        // Double-click to open RecipeDetail
        recipeListView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                Recipe selected = recipeListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openRecipeDetail(selected);
                }
            }
        });
    }

    private void loadRoomData(RoomLeader leader) {
        int roomId = leader.getRoomId();
        roomIdLabel.setText("Room ID: " + roomId);

        try {
            // 1. Load Members
            List<User> members = userDAO.findByRoomId(roomId);
            memberListView.getItems().clear();

            if (members.isEmpty()) {
                memberListView.getItems().add("No members found in this room.");
                memberCountLabel.setText("0 members");
            } else {
                for (User m : members) {
                    String roleIcon = m instanceof RoomLeader ? "👑" : "🎓";
                    String entry = roleIcon + "  " + m.getUsername()
                            + "  (Permission Level: " + m.getPermissionLevel() + ")";
                    memberListView.getItems().add(entry);
                }
                memberCountLabel.setText(members.size() + " member(s)");
            }

            // 2. Load Recipes created by these members
            List<Integer> roomMemberIds = members.stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());

            List<Recipe> allRecipes = recipeService.getAllRecipes();
            List<Recipe> roomRecipes = allRecipes.stream()
                    .filter(r -> r.getAuthor() != null && roomMemberIds.contains(r.getAuthor().getUserId()))
                    .collect(Collectors.toList());

            recipeListView.getItems().setAll(roomRecipes);
            recipeCountLabel.setText(roomRecipes.size() + " recipe(s)");

            statusLabel.setText("You are the leader of Room " + roomId
                    + ".\nYou can double-click a recipe below to edit or delete it.");

        } catch (Exception e) {
            statusLabel.setText("Could not load room data: " + e.getMessage());
        }
    }

    private void openRecipeDetail(Recipe recipe) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/views/RecipeDetailView.fxml"));
            javafx.scene.Node view = loader.load();
            RecipeDetailController ctrl = loader.getController();
            ctrl.setContext(currentUser, recipeService, searchService, mainController);
            ctrl.setRecipe(recipe);
            mainController.loadViewNode(view);
        } catch (Exception e) {
            System.err.println("Failed to load RecipeDetailView: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        mainController.loadView("/fxml/views/DashboardView.fxml");
    }
}

