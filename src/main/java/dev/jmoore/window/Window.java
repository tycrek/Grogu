package dev.jmoore.window;

import dev.jmoore.Fractal;
import dev.jmoore.GenConfig;
import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.val;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class Window extends Application {

    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;
    public final static AtomicReference<StackPane> rootPane = new AtomicReference<>();

    @Override
    public void start(Stage stage) {

        //#region           UI setup

        // Create the input panes
        val scaleXInput = new MandelInputPane(Grogu.Axis.X, "Scale");
        val scaleYInput = new MandelInputPane(Grogu.Axis.Y, "Scale");
        val centerXInput = new MandelInputPane(Grogu.Axis.X, "Center (not in use yet)");
        val centerYInput = new MandelInputPane(Grogu.Axis.Y, "Center (not in use yet)");

        // Input handler
        final Runnable inputHandler = () -> {
            var xText = scaleXInput.getTextField().getText();
            var yText = scaleYInput.getTextField().getText();
            var xCenterText = centerXInput.getTextField().getText();
            var yCenterText = centerYInput.getTextField().getText();

            try {
                W2CCoords.xScale = Double.parseDouble(xText);
                W2CCoords.yScale = Double.parseDouble(yText);
                W2CCoords.centerX = Double.parseDouble(xCenterText);
                W2CCoords.centerY = Double.parseDouble(yCenterText);
            } catch (NumberFormatException e) {
                if (!yText.equals("") && !xText.equals("")) SimpleAlert.show("Invalid input", String.format(
                        "Invalid inputs: [%s, %s, %s, %s]%n%nOnly numbers (including decimals) are allowed.",
                        xText, yText, xCenterText, yCenterText));
                else SimpleAlert.show("Invalid input", "Invalid input: <empty>");
            }
        };

        // Add event handlers to the input panes
        scaleXInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        scaleYInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        centerXInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        centerYInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());

        // Mouse position label
        val mousePositionLabel = new Label("Mouse position:");
        val mousePositionCoordsLabel = new Label("0x0");

        // Mandelbrot labels
        val isInSetLabel = new Label("Is in set: false");
        val iterationCountLabel = new Label("Iteration count: 0");

        // Create the grid pane and add the input panes
        val inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(16, 0, 0, 16));
        inputGrid.getColumnConstraints().addAll(new ColumnConstraints(220), new ColumnConstraints(220));
        inputGrid.setVgap(4);
        // Row 0
        inputGrid.add(scaleXInput, 0, 0);
        inputGrid.add(centerXInput, 1, 0);
        // Row 1
        inputGrid.add(scaleYInput, 0, 1);
        inputGrid.add(centerYInput, 1, 1);
        // Row 2
        inputGrid.add(mousePositionLabel, 0, 2);
        // Row 3
        inputGrid.add(mousePositionCoordsLabel, 0, 3);
        // Row 4
        inputGrid.add(isInSetLabel, 0, 4);
        // Row 5
        inputGrid.add(iterationCountLabel, 0, 5);

        // Colour all the inputGrid labels white
        inputGrid.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;"));

        // Button on last row
        val button = new Button("Generate");
        inputGrid.add(button, 0, 20);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
            updateRootPaneBackground(new ImageView(new Image(ImageGen.toInputStream(ImageGen.generate((int) W2CCoords.width, (int) W2CCoords.height)))), stage));

        // Overlaid cartesian range grid
        val cartesianRangeGrid = CartesianRangeGridPane.build();

        StackPane ROOT = new StackPane(cartesianRangeGrid, inputGrid);
        ROOT.setBackground(Background.fill(Color.BLACK));
        rootPane.set(ROOT);

        // Primary scene
        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse MOVE listener
        scene.setOnMouseMoved(event -> {
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());

            // Update mouse position labels
            mousePositionCoordsLabel.setText(String.format("X: (%s) %s%nY: (%s) %s",
                    event.getSceneX(), cartesian[0],
                    event.getSceneY(), cartesian[1]));

            // Update Mandelbrot labels
            var mandelResult = Fractal.isInMandelbrotSet(cartesian[0], cartesian[1]);
            isInSetLabel.setText(String.format("Is in set: %s", mandelResult.isInMandelbrotSet()));
            iterationCountLabel.setText(String.format("Iteration count: %s", mandelResult.getIterations()));
        });

        //#endregion
        //#region       Window resize operations

        // Updates the cartesian range grid constraints so the grid is always the same size as the window
        Runnable updateCartesianConstraints = () -> CartesianRangeGridPane.updateConstraints(stage.getWidth(), stage.getHeight(), cartesianRangeGrid);

        // Function to run both runnables and update the placeholder image, depending on the axis
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
            updateCartesianConstraints.run();
            if (axis == Grogu.Axis.X) W2CCoords.width = newSize.doubleValue() * GenConfig.Image.Resolution;
            else if (axis == Grogu.Axis.Y) W2CCoords.height = newSize.doubleValue() * GenConfig.Image.Resolution;
        };

        // Add the listener
        stage.widthProperty().addListener(makeListener.apply(Grogu.Axis.X));
        stage.heightProperty().addListener(makeListener.apply(Grogu.Axis.Y));

        //#endregion
        //#region       Stage setup

        // Set the stage
        stage.setScene(scene);
        stage.setTitle("Grogu - Fractal experiments");
        stage.setMinWidth(460);
        stage.show();

        // For some reason this is required to close the application, it did it automatically at first so idk what happened
        stage.setOnCloseRequest(event -> System.exit(0));

        //#endregion
    }

    void updateRootPaneBackground(ImageView imageView, Stage stage) {
        rootPane.get().setBackground(new Background(new BackgroundImage(
                imageView.getImage(),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        stage.getWidth(),
                        stage.getHeight(),
                        false,
                        false,
                        false,
                        false))));
    }
}
