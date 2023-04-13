package dev.jmoore.window;

import dev.jmoore.Fractal;
import dev.jmoore.GenConfig;
import dev.jmoore.Grogu;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

        // Utility grid & bar
        val utilityGrid = UtilityGrid.build(stage);
        val bar = buildBar(utilityGrid);

        StackPane ROOT = new StackPane(utilityGrid.getGridPane(), bar);
        StackPane.setAlignment(bar, Pos.BOTTOM_CENTER);
        rootPane.set(ROOT);

        // Primary scene
        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse MOVE listener
        scene.setOnMouseMoved(event -> {
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());

            // Update mouse position labels
            utilityGrid.getMousePositionCoordsLabel().setText(String.format("X: (%s) %s%nY: (%s) %s",
                    event.getSceneX(), cartesian[0],
                    event.getSceneY(), cartesian[1]));

            // Update Mandelbrot labels
            var mandelResult = Fractal.isInMandelbrotSet(cartesian[0], cartesian[1]);
            utilityGrid.getIsInSetLabel().setText(String.format("Is in set: %s", mandelResult.isInMandelbrotSet()));
            utilityGrid.getIterationCountLabel().setText(String.format("Iteration count: %s", mandelResult.getIterations()));
        });

        // Mouse CLICK listener
        scene.setOnMouseClicked(event -> {
            if (Grogu.isGenerating.get()) return;

            // Convert to cartesian coordinates
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());
            W2CCoords.centerX = cartesian[0];
            W2CCoords.centerY = cartesian[1];
            utilityGrid.getConfigureBox().getCenterXInput().getTextField().setText(Double.toString(W2CCoords.centerX));
            utilityGrid.getConfigureBox().getCenterYInput().getTextField().setText(Double.toString(W2CCoords.centerY));
            UtilityGrid.updateRootPaneBackground(utilityGrid, stage);
        });

        // "Zoom" on scroll
        scene.setOnScroll(event -> {
            if (Grogu.isGenerating.get()) return;

            W2CCoords.xScale = rescaleOnScroll(event, Grogu.Axis.X);
            W2CCoords.yScale = rescaleOnScroll(event, Grogu.Axis.Y);
            utilityGrid.getConfigureBox().getScaleXInput().getTextField().setText(Double.toString(W2CCoords.xScale));
            utilityGrid.getConfigureBox().getScaleYInput().getTextField().setText(Double.toString(W2CCoords.yScale));
            UtilityGrid.updateRootPaneBackground(utilityGrid, stage);
        });

        //#endregion
        //#region       Window resize operations

        // Long story here, todo: update this comment
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
            if (Grogu.isGenerating.get()) return;

            UtilityGrid.updateRootPaneBackground(utilityGrid, stage);
            if (axis == Grogu.Axis.X) W2CCoords.width = newSize.doubleValue();
            else if (axis == Grogu.Axis.Y) W2CCoords.height = newSize.doubleValue();
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

        // Set the initial background
        UtilityGrid.updateRootPaneBackground(utilityGrid, stage);

        //#endregion
    }

    private HBox buildBar(UtilityGrid utilityGrid) {

        // Add any nodes you want in your bar to the HBox
        Label label = new Label("This is a bar!\nWith two lines hopefully");
        label.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Set the priority of the spacer to ALWAYS

        // Buttons
        Button configureButton = new Button("Configure");
        configureButton.setOnAction(event -> utilityGrid.getConfigureBox().getChildWindow().show());

        // Create an HBox to hold your bar
        HBox bar = new HBox(label, spacer, configureButton);
        bar.setStyle("-fx-background-color: #222; -fx-padding: 10px;");
        bar.setMaxHeight(Region.USE_PREF_SIZE); // Set the maximum height to use preferred size

        return bar;
    }

    private double rescaleOnScroll(ScrollEvent event, Grogu.Axis axis) {
        boolean isNegative = Double.toString(event.getDeltaY()).contains("-");
        return axis == Grogu.Axis.X
                ? isNegative ? W2CCoords.xScale * GenConfig.Image.ScaleFactor : W2CCoords.xScale / GenConfig.Image.ScaleFactor
                : isNegative ? W2CCoords.yScale * GenConfig.Image.ScaleFactor : W2CCoords.yScale / GenConfig.Image.ScaleFactor;
    }
}
