package com.vinrecipe.controller;

import com.vinrecipe.model.User;
import com.vinrecipe.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for LoginView.fxml.
 * Handles login and registration from the same form.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;  // plain text copy for show-password
    @FXML private Button togglePasswordBtn;
    @FXML private TextField emailField;   // only visible when registering
    @FXML private Label emailLabel;
    @FXML private Label statusLabel;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Label toggleLabel;

    private final UserService userService = new UserService();
    private boolean isRegisterMode = false;
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Start in login mode — email field hidden
        setRegisterMode(false);
    }

    /** Handle Login button. */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordVisible.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter username and password.", true);
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            showStatus("Invalid username or password.", true);
            return;
        }

        // Navigate to main app
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/layout/MainLayout.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/components.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setTitle("VinRECIPE — " + user.getUsername());
        } catch (IOException e) {
            showStatus("Failed to load main window: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /** Handle Register button. */
    @FXML
    private void handleRegister() {
        if (!isRegisterMode) {
            setRegisterMode(true);
            return;
        }
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordVisible.getText() : passwordField.getText();
        String email    = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showStatus("All fields are required.", true);
            return;
        }

        int newId = userService.register(username, password, email);
        if (newId == -1) {
            showStatus("Registration failed. Username may already exist.", true);
        } else {
            showStatus("Account created! You can now log in.", false);
            setRegisterMode(false);
        }
    }


    /** Toggle password visibility. */
    @FXML
    private void togglePassword() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            // Copy text from PasswordField → TextField and show plain text
            passwordVisible.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisible.setVisible(true);
            passwordVisible.setManaged(true);
            togglePasswordBtn.setText("🙈");
        } else {
            // Copy text back from TextField → PasswordField
            passwordField.setText(passwordVisible.getText());
            passwordVisible.setVisible(false);
            passwordVisible.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordBtn.setText("👁");
        }
    }

    /** Toggle between login and register mode. */
    @FXML
    private void toggleMode() {
        setRegisterMode(!isRegisterMode);
        statusLabel.setText("");
    }

    private void setRegisterMode(boolean register) {
        isRegisterMode = register;
        boolean show = register;
        emailField.setVisible(show);
        emailField.setManaged(show);
        emailLabel.setVisible(show);
        emailLabel.setManaged(show);
        loginBtn.setVisible(!show);
        loginBtn.setManaged(!show);
        registerBtn.setText(show ? "Create Account" : "Register");
        toggleLabel.setText(show ? "Already have an account? Log In" : "New here? Register");
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-error", "status-success");
        statusLabel.getStyleClass().add(isError ? "status-error" : "status-success");
    }
}
