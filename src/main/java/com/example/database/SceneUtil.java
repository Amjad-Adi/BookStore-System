package com.example.database;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneUtil {
    private SceneUtil() {}

    private static double normalW = 1500;
    private static double normalH = 900;

    private static boolean listenersInstalled = false;

    public static Parent load(String fxmlName) {
        String path = "/com/example/database/" + fxmlName;
        URL url = SceneUtil.class.getResource(path);

        if (url == null) {
            String errorMsg = "FXML not found: " + path +
                    "\n\nMake sure the file exists at:\n" +
                    "src/main/resources/com/example/database/" + fxmlName;
            showError("FXML Loading Error", errorMsg);
            throw new RuntimeException(errorMsg);
        }

        try {
            FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        } catch (IOException e) {
            String errorMsg = "Failed to load FXML: " + fxmlName +
                    "\n\nError: " + e.getMessage() +
                    "\n\nCheck:\n" +
                    "1. FXML syntax is correct\n" +
                    "2. Controller class exists and is properly referenced\n" +
                    "3. All fx:id elements match controller fields";
            showError("FXML Loading Error", errorMsg);
            e.printStackTrace();
            throw new RuntimeException(errorMsg, e);
        }
    }


    public static void switchTo(ActionEvent event, String fxmlName, String title) {
        try {
            Stage stage = stageFrom(event);
            installSizeMemory(stage);

            Parent root = load(fxmlName);
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            applyNormalSizeIfNotFullscreen(stage);
            stage.show();
        } catch (Exception e) {
            showError("Navigation Error", "Failed to switch to: " + fxmlName + "\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void switchTo(ActionEvent event, String fxmlName) {
        try {
            Stage stage = stageFrom(event);
            installSizeMemory(stage);

            Parent root = load(fxmlName);
            stage.setScene(new Scene(root));

            applyNormalSizeIfNotFullscreen(stage);
            stage.show();
        } catch (Exception e) {
            showError("Navigation Error", "Failed to switch to: " + fxmlName + "\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    public static Parent loadInto(StackPane container, String fxmlName) {
        if (container == null) {
            throw new IllegalArgumentException("loadInto: container is null.");
        }

        try {
            Parent root = load(fxmlName);
            container.getChildren().setAll(root);
            return root;
        } catch (Exception e) {
            showError("Loading Error", "Failed to load into container: " + fxmlName + "\n\n" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static Stage stageFrom(ActionEvent event) {
        if (event == null || event.getSource() == null) {
            throw new IllegalArgumentException("switchTo(ActionEvent): event or source is null.");
        }

        Object source = event.getSource();
        if (!(source instanceof Node)) {
            throw new IllegalArgumentException("Event source is not a Node: " + source.getClass().getName());
        }

        Node node = (Node) source;
        if (node.getScene() == null) {
            throw new IllegalStateException("Node does not have a Scene attached.");
        }

        if (node.getScene().getWindow() == null) {
            throw new IllegalStateException("Scene does not have a Window attached.");
        }

        return (Stage) node.getScene().getWindow();
    }

    private static void installSizeMemory(Stage stage) {
        if (listenersInstalled) return;
        listenersInstalled = true;

        stage.setResizable(true);

        stage.widthProperty().addListener((obs, oldV, newV) -> {
            if (!stage.isMaximized() && !stage.isFullScreen() && newV.doubleValue() > 200) {
                normalW = newV.doubleValue();
            }
        });

        stage.heightProperty().addListener((obs, oldV, newV) -> {
            if (!stage.isMaximized() && !stage.isFullScreen() && newV.doubleValue() > 200) {
                normalH = newV.doubleValue();
            }
        });
    }


    private static void applyNormalSizeIfNotFullscreen(Stage stage) {
        if (stage.isFullScreen() || stage.isMaximized()) return;

        stage.setWidth(normalW);
        stage.setHeight(normalH);

        stage.setMinWidth(1100);
        stage.setMinHeight(700);
    }


    private static void showError(String title, String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("An error occurred");
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("=== " + title + " ===");
            System.err.println(message);
            e.printStackTrace();
        }
    }


    public static double getNormalWidth() {
        return normalW;
    }


    public static double getNormalHeight() {
        return normalH;
    }


    public static void setDefaultSize(double width, double height) {
        if (width > 200 && height > 200) {
            normalW = width;
            normalH = height;
        }
    }
}