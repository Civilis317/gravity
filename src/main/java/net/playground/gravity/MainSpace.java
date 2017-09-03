package net.playground.gravity;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainSpace extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        int dx = 50;
        int dy = 50;
        int radius = 125;

        Circle circle = new Circle(radius, dx, dy);
        Text text = new Text(20, 20, "HI");
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);

        pane.getChildren().addAll(circle, text);
        Scene scene = new Scene(pane, 700, 700);

        scene.setOnKeyPressed(e -> {
            double x;
            double y;
            Circle blast = new Circle(30, Color.RED);

            switch (e.getCode()) {
            case DOWN:
                circle.setCenterY(circle.getCenterY() + 10);
                break;
            case UP:
                circle.setCenterY(circle.getCenterY() - 10);
                break;
            case LEFT:
                circle.setCenterX(circle.getCenterX() - 10);
                break;
            case RIGHT:
                circle.setCenterX(circle.getCenterX() + 10);
                break;
            default:
                break;
            }
        });

        primaryStage.setTitle("Arrow Keys");
        primaryStage.setScene(scene);
        primaryStage.show();

        text.requestFocus();
    }

}
