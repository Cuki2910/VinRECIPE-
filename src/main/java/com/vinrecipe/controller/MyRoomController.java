package com.vinrecipe.controller;

import com.vinrecipe.dao.UserDAO;
import com.vinrecipe.model.RoomLeader;
import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for MyRoomView.fxml.
 * Only shown to RoomLeader users (enforced by MainController via instanceof check).
 *
 * Demonstrates Role-Based access: RoomLeader has permissionLevel 2, can see
 * and interact with room members, unlike NormalStudent (permissionLevel 1).
 */
public class MyRoomController implements ContextAware {

    @FXML private ListView<String> memberListView;
    @FXML private Label roomIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label memberCountLabel;

    private User currentUser;
    private MainController mainController;
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void setContext(User user, RecipeService recipeService,
                           SearchService searchService, MainController mainController) {
        this.currentUser    = user;
        this.mainController = mainController;

        if (user instanceof RoomLeader leader) {
            loadRoomMembers(leader);
        } else {
            // Safety guard — should never reach here since MainController only
            // shows "My Room" button to RoomLeaders
            statusLabel.setText("⚠ Access restricted to Room Leaders only.");
        }
    }

    /**
     * Load all members of this leader's room from the database.
     * Uses RoomLeader.assignMembers() concept — demonstrating Polymorphism.
     */
    private void loadRoomMembers(RoomLeader leader) {
        int roomId = leader.getRoomId();
        roomIdLabel.setText("Room ID: " + roomId);

        try {
            List<User> members = userDAO.findByRoomId(roomId);
            memberListView.getItems().clear();

            if (members.isEmpty()) {
                memberListView.getItems().add("No members found in this room.");
                memberCountLabel.setText("0 members");
            } else {
                for (User m : members) {
                    String roleIcon = "🎓";
                    String entry = roleIcon + "  " + m.getUsername()
                            + "  (Permission Level: " + m.getPermissionLevel() + ")";
                    memberListView.getItems().add(entry);
                }
                memberCountLabel.setText(members.size() + " member(s)");
            }
            statusLabel.setText("You are the leader of Room " + roomId
                    + ". You can edit or delete recipes from your members on the Dashboard.");

        } catch (Exception e) {
            statusLabel.setText("Could not load room members: " + e.getMessage());
        }
    }

    @FXML
    private void goBack() {
        mainController.loadView("/fxml/views/DashboardView.fxml");
    }
}
