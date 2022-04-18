package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Player;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class HighScoresController {

    @FXML
    VBox vBox;

    @FXML
    TableView<Player> tableView;
    @FXML
    TableColumn<Player, Double> tableColumnScore;
    @FXML
    TableColumn<Player, String> tableColumnName;
    @FXML
    Button menu;

    public void initialize() {
        ArrayList<Player> players = readPlayersArrayListFromFile("players.obj");

        Comparator<Player> compareByScore = Comparator.comparingDouble(Player::getScore);
        players.sort(compareByScore.reversed());

        ObservableList<Player> list = FXCollections.observableArrayList(players);

        tableColumnScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableView.setItems(list);
    }

    public ArrayList<Player> readPlayersArrayListFromFile(String path) {
        ArrayList<Player> players;
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(path)
            );
            players = (ArrayList<Player>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            players = new ArrayList<>();
        }
        return players;
    }

    public void backToMenu(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/menu.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene currentScene = menu.getScene();
        Stage currentWindow = (Stage) currentScene.getWindow();

        Scene newScene = new Scene(root);
        currentWindow.setScene(newScene);
        currentWindow.setResizable(false);
    }
}
