package dev.jmoore.window;

import dev.jmoore.Grogu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.val;

public class Window extends Application {

    final static int WIDTH = 800;
    final static int HEIGHT = 600;

    @Override
    public void start(Stage stage) {

        //#region           UI setup

        // Mouse position label
        val mousePositionLabel = new Label("Mouse position: 0x0");

        // Create the input panes
        MandelInputPane inputX = new MandelInputPane(Grogu.Axis.X);
        MandelInputPane inputY = new MandelInputPane(Grogu.Axis.Y);

        // Create the grid pane and add the input panes
        var gridPane = new GridPane();
        // Row 0
        gridPane.add(mousePositionLabel, 0, 0);
        // Row 1
        gridPane.add(inputX, 0, 1);
        gridPane.add(inputY, 1, 1);

        StackPane ROOT = new StackPane(gridPane);

        // Primary scene
        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse click listener
        scene.setOnMouseClicked(event -> System.out.println("Mouse clicked: " + event.getSceneX() + ", " + event.getSceneY()));

        // Mouse move listener
        scene.setOnMouseMoved(event -> mousePositionLabel.setText("Mouse position: " + event.getSceneX() + "x" + event.getSceneY()));

        //#endregion
        //#region       Placeholder image updater

        // Window resize listener thread (updates the placeholder image)
        val placeholderUpdater = new PlaceholderUpdater(ROOT, WIDTH, HEIGHT);
        new Thread(placeholderUpdater, "PlaceholderUpdateThread").start();

        // Stage property listeners
        stage.widthProperty().addListener((obs, oldVal, newVal) -> placeholderUpdater.updateX(newVal.doubleValue()));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> placeholderUpdater.updateY(newVal.doubleValue()));

        //#endregion
        //#region       Stage setup

        // Set the stage
        stage.setScene(scene);
        stage.setTitle("Grogu - Fractal experiments");
        stage.show();

        // For some reason this is required to close the application, it did it automatically at first so idk what happened
        stage.setOnCloseRequest(event -> System.exit(0));

        //#endregion
    }
}
