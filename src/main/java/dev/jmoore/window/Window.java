package dev.jmoore.window;

import dev.jmoore.Grogu;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.val;

public class Window extends Application {

    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;

    @Override
    public void start(Stage stage) {

        //#region           UI setup

        // Mouse position label
        val mousePositionLabel = new Label("Mouse position:");
        val mousePositionCoordsLabel = new Label("0x0");

        // W2CCoords labels
        val w2cSizeLabel = new Label("Window size:");
        val w2cSizeValueLabel = new Label("0x0");

        val w2cCoordsLabel = new Label("Cartesian coordinates:");
        val w2cCoordsValueLabel = new Label("0x0");

        val w2cScaleLabel = new Label("Cartesian scale:");
        val w2cScaleValueLabel = new Label("0x0");

        // Create the input panes
        MandelInputPane inputX = new MandelInputPane(Grogu.Axis.X);
        MandelInputPane inputY = new MandelInputPane(Grogu.Axis.Y);

        Runnable inputHandler = () -> {
            var xText = inputX.getTextField().getText();
            var yText = inputY.getTextField().getText();
            try {
                var xD = Double.parseDouble(xText);
                var yD = Double.parseDouble(yText);
                SimpleAlert.show("Valid input", String.format("Valid inputs: [%s, %s]", xText, yText));
            } catch (NumberFormatException e) {
                if (!yText.equals("") && !xText.equals(""))
                    SimpleAlert.show("Invalid input", String.format("Invalid inputs: [%s, %s]%n%nOnly numbers (including decimals) are allowed.", xText, yText));
                else SimpleAlert.show("Invalid input", "Invalid input: <empty>");
            }
        };

        inputX.getTextField().setOnAction(event -> inputHandler.run());
        inputY.getTextField().setOnAction(event -> inputHandler.run());

        // Create the grid pane and add the input panes
        var inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(16, 0, 0, 16));
        inputGrid.getColumnConstraints().addAll(new ColumnConstraints(180), new ColumnConstraints(180));
        inputGrid.setVgap(4);
        // Row 0
        inputGrid.add(inputX, 0, 0);
        // Row 1
        inputGrid.add(inputY, 0, 1);
        // Row 2
        inputGrid.add(mousePositionLabel, 0, 2);
        inputGrid.add(mousePositionCoordsLabel, 1, 2);
        // Row 3
        inputGrid.add(w2cSizeLabel, 0, 3);
        inputGrid.add(w2cSizeValueLabel, 1, 3);
        // Row 4
        inputGrid.add(w2cCoordsLabel, 0, 4);
        inputGrid.add(w2cCoordsValueLabel, 1, 4);
        // Row 5
        inputGrid.add(w2cScaleLabel, 0, 5);
        inputGrid.add(w2cScaleValueLabel, 1, 5);

        StackPane ROOT = new StackPane(inputGrid);

        // Primary scene
        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse click listener
        scene.setOnMouseClicked(event -> System.out.println("Mouse clicked: " + event.getSceneX() + ", " + event.getSceneY()));

        // Mouse move listener
        scene.setOnMouseMoved(event -> mousePositionCoordsLabel.setText(String.format("%sx%s", event.getSceneX(), event.getSceneY())));

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
        stage.setMinWidth(400);
        stage.show();

        // For some reason this is required to close the application, it did it automatically at first so idk what happened
        stage.setOnCloseRequest(event -> System.exit(0));

        //#endregion
    }
}
