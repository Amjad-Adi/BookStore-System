package com.example.database;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SceneRouter {
    private static Stage stage;

    private SceneRouter() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setResizable(true);
    }

    public static void showLogin() {
        setScene("/com/example/database/login.fxml", "Login");
    }

    public static void showRegister() {
        setScene("/com/example/database/register.fxml", "Register");
    }

    public static void showMainApp() {
        setScene("/com/example/database/main_layout.fxml", "Book Store - Admin");
    }

    private static void setScene(String fxmlPath, String title) {
        try {
            URL url = SceneRouter.class.getResource(fxmlPath);
            if (url == null) {
                throw new RuntimeException("FXML not found: " + fxmlPath);
            }
            Parent root = FXMLLoader.load(url);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load scene: " + fxmlPath + "\n" + e.getMessage(), e);
        }
    }
}
