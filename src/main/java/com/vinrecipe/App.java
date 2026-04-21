package com.vinrecipe;

import com.vinrecipe.dao.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX application entry point for VinRECIPE.
 * Loads the Login screen on startup.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/fxml/views/LoginView.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Cannot find LoginView.fxml");
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 420, 560);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("VinRECIPE — Smart Recipe Planner");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Close DB connection gracefully on exit
        DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
