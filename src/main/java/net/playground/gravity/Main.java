package net.playground.gravity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private ArrayList<Planet> planetList = new ArrayList<>();

    private double SCALE;
    private int SPEEDUP_FACTOR = 1;
    private final double WIDTH = 1400;
    private final double HEIGHT = 900;

    private GraphicsContext g2d;

    private String getConfig(String configName) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/" + configName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString("UTF-8");
    }

    @SuppressWarnings("rawtypes")
    private void parseYML(String yml) throws IOException {
        YamlReader reader = new YamlReader(yml);
        while (true) {
            Map map = (Map) reader.read();
            if (map == null) {
                break;
            }
            if (map.get("scale") != null) {
                setScale(map);
            } else if (map.get("speedupFactor") != null) {
                setSpeedupFactor(map);
            } else if (map.get("planet") != null) {
                addPlanet(map);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void setScale(Map scaleMap) {
        this.SCALE = Double.valueOf((String) scaleMap.get("scale")).doubleValue();
    }

    @SuppressWarnings({ "rawtypes" })
    private void setSpeedupFactor(Map map) {
        this.SPEEDUP_FACTOR = Integer.valueOf((String) map.get("speedupFactor")).intValue();
    }

    @SuppressWarnings("rawtypes")
    private void addPlanet(Map map) {
        Planet planet = new Planet();
        Map planetMap = (Map) map.get("planet");
        planet.setId(Integer.valueOf((String) planetMap.get("id")).intValue());
        planet.setName((String) planetMap.get("name"));
        planet.setColor((String) planetMap.get("color"));
        planet.setRadius(Double.valueOf((String) planetMap.get("radius")).doubleValue());
        planet.setMass(Double.valueOf((String) planetMap.get("mass")).doubleValue());
        Map positionMap = (Map) planetMap.get("position");
        Vector position = new Vector(Double.valueOf((String) positionMap.get("value")).doubleValue(), Double.valueOf((String) positionMap.get("angle")).doubleValue());
        planet.setInitialPosition(position);
        Map velocityMap = (Map) planetMap.get("velocity");
        Vector velocity = new Vector(Double.valueOf((String) velocityMap.get("value")).doubleValue(), Double.valueOf((String) velocityMap.get("angle")).doubleValue());
        planet.setInitialVelocity(velocity);
        planet.setDebugEnabled(Boolean.valueOf((String) planetMap.get("debug")).booleanValue());
        planetList.add(planet);
    }

    private void setup(String ymlConfig) throws IOException {
        String config = getConfig(ymlConfig);
        parseYML(config);
    }

    private List<String> getConfigList() {
        List<String> returnList = new ArrayList<>();
        try {
            URI uri = this.getClass().getClassLoader().getResource("config").toURI();
            Files.newDirectoryStream(Paths.get(uri), path -> path.toString().endsWith(".yml")).forEach(p -> returnList.add(p.getName(p.getNameCount() - 1).toString()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    private Parent createStartupView(Stage stage, List<String> configList) {
        BorderPane root = new BorderPane();
        root.setPrefSize(400, 300);

        VBox vbCenter = new VBox();

        Label label = new Label("Choose a configuration:");
        vbCenter.getChildren().add(label);

        ComboBox<String> configBox = new ComboBox<>();
        configBox.getItems().addAll(configList);
        configBox.setValue(configBox.getItems().get(0));
        vbCenter.getChildren().add(configBox);

        HBox buttonContainer = new HBox();
        Button button = new Button("Ok");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                // change scene:
                stage.close();
                stage.getScene().setRoot(createContent(configBox.getValue()));
                stage.show();
            }
        });
        buttonContainer.getChildren().add(button);

        root.setPadding(new Insets(20)); // space between elements and window border
        root.setCenter(vbCenter);
        root.setBottom(buttonContainer);
        return root;
    }

    private Parent createContent(String configYml) {

        try {
            setup(configYml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pane root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        g2d = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int n = 0;
                int m = 0;
                while (n < SPEEDUP_FACTOR) {
                    planetList.forEach(p -> p.processGravity(planetList));

                    // plot only every minute
                    if (m == 60) {
                        m = 0;
                        clearCanvas();
                        planetList.forEach(p -> display(p));
                    }
                    n++;
                    m++;
                }
            }
        };

        timer.start();

        root.getChildren().add(canvas);
        return root;
    }

    private void clearCanvas() {
        g2d.setFill(Color.CORNSILK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setStroke(Color.LIGHTGREY);
        for (int x = 0; x < WIDTH; x += 10) {
            g2d.strokeLine(x, 0, x, HEIGHT);
            g2d.strokeLine(0, x, WIDTH, x);
        }
        g2d.setStroke(Color.BLACK);
        g2d.strokeLine(0, HEIGHT / 2, WIDTH, HEIGHT / 2);
        g2d.strokeLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
    }

    private void display(Planet planet) {
        // apply scale and calculate offset to put origin (0,0) at center screen...
        double x = (planet.getPosition().getX() / SCALE) + (WIDTH / 2);
        double y = (planet.getPosition().getY() / SCALE) + (HEIGHT / 2);
        double radius = planet.getRadius() / SCALE;
        if (radius < 1) {
            radius = 1;
        }
        planet.getPositionList().forEach(p -> displayTrail(p));
        g2d.setFill(Color.web(planet.getColor()));
        g2d.fillOval(x - radius / 2, y - radius / 2, radius, radius);
    }

    private void displayTrail(Point point) {
        double x = (point.getX() / SCALE) + (WIDTH / 2);
        double y = (point.getY() / SCALE) + (HEIGHT / 2);
        g2d.setStroke(Color.LIGHTGREY);
        g2d.strokeOval(x, y, 1, 1);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // setup scene:
        stage.setTitle("Gravity Simulator");
        stage.setScene(new Scene(createStartupView(stage, getConfigList())));
        stage.show();
    }

}
