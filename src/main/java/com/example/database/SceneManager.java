package com.example.database;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage stage;

    private SceneManager() {}

    public static void setStage(Stage s) {
        stage = s;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceHelper.fxml(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scene: " + fxmlFile + " -> " + e.getMessage(), e);
        }
    }
}
