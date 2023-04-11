package dev.jmoore.window;

import dev.jmoore.Fractal;
import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

        // Overlaid cartesian range grid
        val cartesianRangeGrid = CartesianRangeGridPane.build();

        StackPane ROOT = new StackPane(cartesianRangeGrid, utilityGrid.getGridPane());
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
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());

            // Update center inputs
            utilityGrid.getCenterXInput().getTextField().setText(Double.toString(cartesian[0]));
            utilityGrid.getCenterYInput().getTextField().setText(Double.toString(cartesian[1]));

            // Update W2CCoords
            W2CCoords.centerX = cartesian[0];
            W2CCoords.centerY = cartesian[1];

            // Update the image
            System.out.println("Generating...");
            UtilityGrid.updateRootPaneBackground(new ImageView(new Image(
                    ImageGen.toInputStream(ImageGen.generate((int) W2CCoords.width, (int) W2CCoords.height)))), stage);
            utilityGrid.getCenterXInput().fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, "", "", null, false, false, false, false));
            System.out.println("Done!");
        });

        //#endregion
        //#region       Window resize operations

        // Updates the cartesian range grid constraints so the grid is always the same size as the window
        Runnable updateCartesianConstraints = () -> CartesianRangeGridPane.updateConstraints(stage.getWidth(), stage.getHeight(), cartesianRangeGrid);

        // Function to run both runnables and update the placeholder image, depending on the axis
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
            updateCartesianConstraints.run();
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
        UtilityGrid.updateRootPaneBackground(new ImageView(new Image(
                ImageGen.toInputStream(ImageGen.generate(Window.WIDTH, Window.HEIGHT)))), stage);

        //#endregion
    }
}
