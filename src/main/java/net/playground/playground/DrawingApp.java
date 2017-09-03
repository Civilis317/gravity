package net.playground.playground;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DrawingApp extends Application {

    private GraphicsContext g;
    private double t = 0.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800, 600);
        Canvas canvas = new Canvas(800, 600);
        g = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                t += 0.017;
                draw();
            }
        };

        timer.start();

        root.getChildren().add(canvas);
        return root;
    }

    private void draw() {
        Point2D p = curveFunction();
        g.setStroke(Color.BLACK);
        double newX = 400 + p.getX();
        double newY = 300 + p.getY();
        g.strokeOval(newX, newY, 1, 1);
    }

    private Point2D curveFunction() {
        double x = Math.cos(t);
        double y = Math.sin(t);
        return new Point2D(x, y).multiply(50);
    }

}
