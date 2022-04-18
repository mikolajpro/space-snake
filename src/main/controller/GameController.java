package main.controller;

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Direction;
import main.Player;
import main.Position;


import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class GameController {


    @FXML
    StackPane stackPane;
    @FXML
    Pane pane;
    @FXML
    Label timer;
    @FXML
    Rectangle rectangle;

    @FXML
    Button segmentAdder;

    HBox hBox;
    AnchorPane anchorPane;

    int widthSquaresNumber;
    int heightSquaresNumber;

    private double gamePaneWidth;
    private double gamePaneHeight;

    int snakeSize = 50;

    int columns;
    int rows;

    double score = 0;


    Player player = new Player();


    Direction direction = Direction.UP;

    Position currentPosition;
    Position lastPosition;

    Rectangle snakeHead;
    ArrayList<Rectangle> body = new ArrayList<>();

    Rectangle food = new Rectangle(snakeSize, snakeSize);
    boolean isSegmentAdder;

    long secondsCounter;

    Timeline time;

    Timeline movement = new Timeline(
            new KeyFrame(Duration.seconds(0),
                    event -> {
                        if (food.getX() == snakeHead.getX() && food.getY() == snakeHead.getY()) {
                            food.setVisible(false);
                            score++;
                        }
                        if (snakeHead.getX() == -snakeSize || snakeHead.getX() == gamePaneWidth
                                || snakeHead.getY() == -snakeSize || snakeHead.getY() == gamePaneHeight) {
                            gameOver();
                        }
                        for (Rectangle snakeSegment : body) {
                            if (snakeSegment.getX() == snakeHead.getX() && snakeSegment.getY() == snakeHead.getY()) {
                                gameOver();
                            }
                        }
                    }
            ),
            new KeyFrame(Duration.seconds(0.3),
                    event -> {
                        if (food.getX() == snakeHead.getX() && food.getY() == snakeHead.getY() && isSegmentAdder) {
                            addBody();
                        }

                        lastPosition = new Position(snakeHead.getX(), snakeHead.getY());
                        moveHead();
                        snakeHead.toFront();
                        moveBody();
                    }
            )
    );

    Timeline foodSpawn = new Timeline(
            new KeyFrame(Duration.seconds(0),
                    event -> {
                        if ((int) (Math.random() * 4) == 0) {
                            food.setFill(new ImagePattern(new Image("/main/image/superCoin.png")));
                            isSegmentAdder = false;
                        } else {
                            food.setFill(new ImagePattern(new Image("/main/image/coin.png")));
                            isSegmentAdder = true;
                        }

                        food.setX((int) (Math.random() * widthSquaresNumber) * snakeSize);
                        food.setY((int) (Math.random() * heightSquaresNumber) * snakeSize);
                        food.setVisible(true);
                        anchorPane.getChildren().add(food);
                        snakeHead.toFront();
                    }
            ),
            new KeyFrame(Duration.seconds(10),
                    event -> {
                        anchorPane.getChildren().remove(food);
                    }
            )
    );


    @FXML
    public void initialize() {
        prepareGame();
    }

    public void prepareGame() {
        segmentAdder.setVisible(false);
        timer.setVisible(false);

        widthSquaresNumber = (int) stackPane.getPrefWidth() / snakeSize;
        heightSquaresNumber = (int) stackPane.getPrefHeight() / snakeSize;

        if (widthSquaresNumber % 2 == 0) widthSquaresNumber -= 1;
        if (heightSquaresNumber % 2 == 0) heightSquaresNumber -= 1;

        gamePaneWidth = widthSquaresNumber * snakeSize;
        gamePaneHeight = heightSquaresNumber * snakeSize;

        rectangle.setWidth(stackPane.getPrefWidth());
        rectangle.setHeight(stackPane.getPrefHeight());

        TextField textFieldColumns = new TextField();
        textFieldColumns.setPromptText("COLUMNS");
        textFieldColumns.setPrefWidth(475);

        TextField textFieldRows = new TextField();
        textFieldRows.setPromptText("ROWS");
        textFieldRows.setPrefWidth(475);

        textFieldColumns.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        this.columns = (Integer.parseInt(newValue));
                    } catch (NumberFormatException e) {

                    }

                }
        );
        textFieldRows.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        this.rows = (Integer.parseInt(newValue));
                    } catch (NumberFormatException e) {

                    }
                }
        );


        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(snakeSize);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(textFieldColumns, textFieldRows);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(snakeSize);

        Button startGameButton = new Button("START");
        startGameButton.setPrefWidth(1000);

        vBox.getChildren().addAll(hBox, startGameButton);
        stackPane.getChildren().addAll(vBox);


        startGameButton.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (columns > 0 && rows > 0) {
                            stackPane.getChildren().remove(vBox);
                            startGame();
                        } else {
                            if (columns <= 0)
                                textFieldColumns.setStyle("-fx-background-color: #ff0000");
                            else {
                                textFieldColumns.setStyle("-fx-background-color: #72FF00");
                            }
                            if (rows <= 0)
                                textFieldRows.setStyle("-fx-background-color: #ff0000");
                            else {
                                textFieldRows.setStyle("-fx-background-color: #72FF00");
                            }
                        }
                    }
                }
        );
    }

    public void startGame() {
        segmentAdder.setVisible(true);

        // Change '-' to '+' to make V it appear in game
        segmentAdder.setTranslateY(-(gamePaneHeight / 2) - segmentAdder.getHeight());
        segmentAdder.setTranslateX(-(gamePaneWidth / 2) - segmentAdder.getWidth() / 2 - snakeSize);

        timer.setVisible(true);

        hBox = new HBox();
        anchorPane = new AnchorPane();

        anchorPane.setPrefWidth(gamePaneWidth);
        anchorPane.setPrefHeight(gamePaneHeight);

        timer.setTranslateY(gamePaneHeight / 2 - snakeSize * 1.5);

        pane.getChildren().add(hBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(anchorPane);

        snakeHead = new Rectangle(anchorPane.getPrefWidth() / 2 - snakeSize / 2, anchorPane.getPrefHeight() / 2 - snakeSize / 2, snakeSize, snakeSize);
        snakeHead.setFill(Color.RED);
        snakeHead.setStroke(Color.web("0xd20000", 1.0));
        snakeHead.setStrokeType(StrokeType.INSIDE);
        snakeHead.setStrokeWidth(5);


        anchorPane.getChildren().addAll(snakeHead, segmentAdder);

        anchorPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                setDirection(event.getCode());
            }
        });


        //TIMELINES
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss");
        LocalTime localTime = LocalTime.now();
        time = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        event -> {
                            secondsCounter = localTime.until(LocalTime.now(), ChronoUnit.SECONDS);
                            timer.setText(LocalTime.ofSecondOfDay(secondsCounter).format(dateTimeFormatter));
                        }
                ));

        time.setCycleCount(Animation.INDEFINITE);
        time.play();

        movement.setCycleCount(Animation.INDEFINITE);
        movement.play();

        foodSpawn.setCycleCount(Animation.INDEFINITE);
        foodSpawn.play();
    }

    private void gameOver() {
        movement.pause();
        foodSpawn.pause();
        time.pause();

        Rectangle red = new Rectangle(anchorPane.getPrefWidth(), anchorPane.getPrefHeight(), Color.RED);
        Rectangle black = new Rectangle(anchorPane.getPrefWidth(), anchorPane.getPrefHeight(), Color.BLACK);
        black.setOpacity(0.5);
        red.setOpacity(0.0);
        FadeTransition ft = new FadeTransition(Duration.seconds(3), red);
        ft.setFromValue(0.0);
        ft.setToValue(0.1);
        ft.setCycleCount(1);
        ft.play();

        Text textGameOver = new Text("GAME OVER!");
        textGameOver.setFill(Color.WHITE);
        textGameOver.setOpacity(0.0);
        ft = new FadeTransition(Duration.seconds(1), textGameOver);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.4), textGameOver);
        tt.setByY(-200);
        tt.play();


        textGameOver.setFont(Font.loadFont("file:src/main/font/VCR_OSD_MONO_1.ttf", 100));
        textGameOver.toFront();
        textGameOver.setX(anchorPane.getPrefWidth() / 2 - textGameOver.getLayoutBounds().getWidth() / 2);
        textGameOver.setY(anchorPane.getPrefHeight() / 2 + textGameOver.getLayoutBounds().getHeight() / 4);

        anchorPane.getChildren().addAll(black, red, textGameOver);


//====================================================================================================================//
        score += secondsCounter;
        player.setScore(score);

        VBox vBox = new VBox();
        vBox.setOpacity(0.0);

        ft = new FadeTransition(Duration.seconds(3), vBox);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();

        stackPane.getChildren().add(vBox);


        TextField textFieldName = new TextField();
        textFieldName.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    player.setName(newValue);
                });
        textFieldName.setPromptText("NAME");

        Button okButton = new Button("OK");
        ArrayList<Player> players = readPlayersArrayListFromFile("players.obj");
        okButton.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (player.getName() != null) {
                            players.add(player);
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(
                                        new FileOutputStream("players.obj")
                                );
                                oos.writeObject(players);
                                oos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/menu.fxml"));
                            Parent root = null;
                            try {
                                root = loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Scene scene = new Scene(root);

                            Stage window = (Stage) stackPane.getScene().getWindow();
                            window.setScene(scene);
                        } else {
                            textFieldName.setStyle("-fx-background-color: #ff0000");
                        }
                    }
                }

        );

        textFieldName.setMaxWidth(600);
        okButton.setPrefWidth(600);


        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(snakeSize);
        vBox.getChildren().addAll(textFieldName, okButton);

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

    public void addBody() {
        Rectangle bodySegment = new Rectangle(snakeSize, snakeSize);
        bodySegment.setFill(Color.web("0x72ff00", 1.0));
        body.add(bodySegment);

        anchorPane.getChildren().add(bodySegment);
    }

    public void setDirection(KeyCode keyCode) {
        switch (keyCode) {
            case W -> direction = Direction.UP;
            case A -> direction = Direction.LEFT;
            case S -> direction = Direction.DOWN;
            case D -> direction = Direction.RIGHT;
        }
    }

    public void moveHead() {
        switch (direction) {
            case UP -> snakeHead.setY(snakeHead.getY() - snakeSize);
            case LEFT -> snakeHead.setX(snakeHead.getX() - snakeSize);
            case DOWN -> snakeHead.setY(snakeHead.getY() + snakeSize);
            case RIGHT -> snakeHead.setX(snakeHead.getX() + snakeSize);
        }
    }

    public void moveBody() {
        for (Rectangle bodySegment : body) {
            currentPosition = new Position(bodySegment.getX(), bodySegment.getY());
            bodySegment.setX(lastPosition.getX());
            bodySegment.setY(lastPosition.getY());
            lastPosition = currentPosition;
        }
    }

    public void addFive(MouseEvent mouseEvent) {
        for (int i = 0; i < 5; i++) {
            addBody();
        }
    }
}
