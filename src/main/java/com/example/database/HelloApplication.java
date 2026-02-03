package com.example.database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);

        URL loginUrl = ResourceHelper.fxml("login.fxml");
        FXMLLoader loader = new FXMLLoader(loginUrl);
        Scene scene = new Scene(loader.load());
        stage.setTitle("The Book Store");
        try {
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/database/Images/Icon.png"))));
        }catch (Exception e){
            throw new Exception("failed to load Icon Image: " + e.getMessage(), e);
        }
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
