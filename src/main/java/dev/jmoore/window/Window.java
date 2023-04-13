package dev.jmoore.window;

import dev.jmoore.Fractal;
import dev.jmoore.GenConfig;
import dev.jmoore.Grogu;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
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

        // Utility grid
        val utilityGrid = UtilityGrid.build(stage);

        StackPane ROOT = new StackPane(utilityGrid.getGridPane());
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
            // Convert to cartesian coordinates
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());
            W2CCoords.centerX = cartesian[0];
            W2CCoords.centerY = cartesian[1];
            utilityGrid.getCenterXInput().getTextField().setText(Double.toString(W2CCoords.centerX));
            utilityGrid.getCenterYInput().getTextField().setText(Double.toString(W2CCoords.centerY));
            UtilityGrid.updateRootPaneBackground(utilityGrid, stage);
        });

        // "Zoom" on scroll
        scene.setOnScroll(event -> {
            W2CCoords.xScale = rescaleOnScroll(event, Grogu.Axis.X);
            W2CCoords.yScale = rescaleOnScroll(event, Grogu.Axis.Y);
            utilityGrid.getScaleXInput().getTextField().setText(Double.toString(W2CCoords.xScale));
            utilityGrid.getScaleYInput().getTextField().setText(Double.toString(W2CCoords.yScale));
            UtilityGrid.updateRootPaneBackground(utilityGrid, stage);
        });

        //#endregion
        //#region       Window resize operations

        // Long story here, todo: update this comment
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
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

    private double rescaleOnScroll(ScrollEvent event, Grogu.Axis axis) {
        boolean isNegative = Double.toString(event.getDeltaY()).contains("-");
        return axis == Grogu.Axis.X
                ? isNegative ? W2CCoords.xScale * GenConfig.Image.ScaleFactor : W2CCoords.xScale / GenConfig.Image.ScaleFactor
                : isNegative ? W2CCoords.yScale * GenConfig.Image.ScaleFactor : W2CCoords.yScale / GenConfig.Image.ScaleFactor;
    }
}
