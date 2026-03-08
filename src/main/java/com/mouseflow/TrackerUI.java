package com.mouseflow;

import java.util.ArrayDeque;

import javafx.stage.Screen;
import com.sun.javafx.scene.paint.GradientUtils.Point;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TrackerUI extends Application{

    private Label xLabel = new Label("0");
    private Label yLabel = new Label("0");
    private Label focusLabel = new Label("None");
    private Label hoverLabel = new Label("None");

    private App engine;

    private Canvas previewCanvas = new Canvas(200,200);
    private ArrayDeque<Point> pathQueue = new ArrayDeque<>();
    double minX,minY,totalW,totalH;

    @Override
    public void start(Stage stage){
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(200);

        Button dashBtn = new Button("Dashboard");
        dashBtn.getStyleClass().add("nav-button");

        Button liveFeed = new Button("Live Feed");
        liveFeed.getStyleClass().add("nav-button");

        Button stats = new Button("Stats");
        stats.getStyleClass().add("nav-button");

        Button settings = new Button("Settings");
        settings.getStyleClass().add("nav-button");

        sidebar.getChildren().addAll(
            dashBtn,
            liveFeed,
            stats,
            settings
        );

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(40));

        VBox statusCard = new VBox(20);
        statusCard.getStyleClass().add("card");

        Label title = new Label("Live Monitor");
        title.getStyleClass().add("title-label");

        HBox coords = new HBox(20);
        coords.getChildren().addAll(
            createStatGroup("X Position", xLabel),
            createStatGroup("Y Position", yLabel)
        );

        statusCard.getChildren().addAll(title, coords,
            createStatGroup("Current Focus", focusLabel),
            createStatGroup("Under Mouse", hoverLabel)
        );
        
        mainContent.getChildren().add(statusCard);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 900,600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("MouseFlow Tracker");
        stage.setScene(scene);
        stage.show();

        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Screen s : Screen.getScreens()) {
            Rectangle2D bounds = s.getBounds();
            minX = Math.min(minX, bounds.getMinX());
            minY = Math.min(minY, bounds.getMinY());
            maxX = Math.max(maxX, bounds.getMaxX());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        
        this.totalW = maxX - minX;
        this.totalH = maxY - minY;

        new Thread(()-> {
            App engine = new App(this);
            engine.startTracking();
            this.engine = engine;
        }).start();
    }

    private VBox createStatGroup(String title, Label valueLabel){
        Label t = new Label(title);
        t.getStyleClass().add("stat-label");
        valueLabel.getStyleClass().add("stat-label");
        return new VBox(5,t,valueLabel);
    }

    public void updateLiveStats(int x, int y, String focus, String hover){
        Platform.runLater(() -> {
            xLabel.setText(String.valueOf(x));
            yLabel.setText(String.valueOf(y));
            focusLabel.setText(focus);
            hoverLabel.setText(hover);
        });
    }

    @Override
    public void stop(){
        System.out.println("Window closing...clean up threads.");
        if(engine != null) {
            engine.shutdown();
        }
        System.exit(0);
    }
    public static void main(String[] args){
        launch(args);
    }
    
}
