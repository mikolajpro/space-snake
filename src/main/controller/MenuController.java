package main.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    StackPane stackPane;

    @FXML
    Button newGame;
    @FXML
    Button highScores;
    @FXML
    Button exit;

    @FXML
    public void initialize() {

    }

    public void newGame(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/game.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene currentScene = stackPane.getScene();
        Stage currentWindow = (Stage) currentScene.getWindow();

        Scene newScene = new Scene(root);
        currentWindow.setScene(newScene);
        currentWindow.setResizable(false);
    }

    public void highScores(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/highScores.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene currentScene = stackPane.getScene();
        Stage currentWindow = (Stage) currentScene.getWindow();

        Scene newScene = new Scene(root);
        currentWindow.setScene(newScene);
    }

    public void exit(MouseEvent mouseEvent) {
        Platform.exit();
    }
}
