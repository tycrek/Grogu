package dev.jmoore.window;

import dev.jmoore.Fractal;
import dev.jmoore.GenConfig;
import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class Window extends Application {

    public final static int WIDTH = 800;
    public final static int HEIGHT = 600;
    public final static AtomicReference<StackPane> ROOT_PANE = new AtomicReference<>();
    @Getter
    private ConfigureBox configureBox;
    @Getter
    private BottomBar bottomBar;

    @Override
    public void start(Stage stage) {
        // * JavaFX hierarchy: Stage -> Scene -> Pane -> Node(s)

        //#region           UI setup

        // Bottom bar & Configure box
        bottomBar = new BottomBar(this);
        configureBox = new ConfigureBox(this, stage);

        // Primary pane
        StackPane rootPane = new StackPane(bottomBar.getBar());
        StackPane.setAlignment(bottomBar.getBar(), Pos.BOTTOM_CENTER);
        ROOT_PANE.set(rootPane);

        // Primary scene
        Scene scene = new Scene(rootPane, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse MOVE listener
        scene.setOnMouseMoved(event -> {
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());

            // Update mouse position labels
            bottomBar.getMousePositionLabel().setText(String.format("Mouse position:%nX: (%s) %s%nY: (%s) %s",
                    event.getSceneX(), cartesian[0],
                    event.getSceneY(), cartesian[1]));

            // Update Mandelbrot labels
            var mandelResult = Fractal.isInMandelbrotSet(cartesian[0], cartesian[1]);
            bottomBar.getIsInSetLabel().setText(String.format("Is in set:%n%s", mandelResult.isInMandelbrotSet()));
            bottomBar.getIterationCountLabel().setText(String.format("Iteration count:%n%s", mandelResult.getIterations()));
        });

        // Mouse CLICK listener
        scene.setOnMouseClicked(event -> {
            if (Grogu.isGenerating.get()) return;

            // Convert to cartesian coordinates
            double[] cartesian = Window2Cartesian.convert(event.getSceneX(), event.getSceneY());
            W2CCoords.centerX = cartesian[0];
            W2CCoords.centerY = cartesian[1];
            configureBox.getCenterXInput().getTextField().setText(Double.toString(W2CCoords.centerX));
            configureBox.getCenterYInput().getTextField().setText(Double.toString(W2CCoords.centerY));
            updateRootPaneBackground(stage);
        });

        // "Zoom" on scroll
        scene.setOnScroll(event -> {
            if (Grogu.isGenerating.get()) return;

            W2CCoords.xScale = rescaleOnScroll(event, Grogu.Axis.X);
            W2CCoords.yScale = rescaleOnScroll(event, Grogu.Axis.Y);
            configureBox.getScaleXInput().getTextField().setText(Double.toString(W2CCoords.xScale));
            configureBox.getScaleYInput().getTextField().setText(Double.toString(W2CCoords.yScale));
            updateRootPaneBackground(stage);
        });

        //#endregion
        //#region       Window resize operations

        // Long story here, todo: update this comment
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
            if (Grogu.isGenerating.get()) return;

            updateRootPaneBackground(stage);
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
        updateRootPaneBackground(stage);

        //#endregion
    }

    public void updateRootPaneBackground(Stage stage) {
        Grogu.isGenerating.set(true);
        bottomBar.getGeneratingLabel().setVisible(true);

        // Generate the fractal asynchronously
        ImageGen.generateAsync((int) W2CCoords.width, (int) W2CCoords.height)

                // * Platform.runLater() is required to update the UI from a different thread
                .thenAccept(fractal -> Platform.runLater(() -> {
                    Grogu.isGenerating.set(false);

                    // Update the labels
                    bottomBar.getTimeTakenLabel().setText((String.format("Time taken:%n%sms", fractal.getDuration())));
                    bottomBar.getGeneratingLabel().setVisible(false);

                    // Set the background image
                    Window.ROOT_PANE.get().setBackground(new Background(new BackgroundImage(
                            new ImageView(new Image(ImageGen.toInputStream(fractal.getImage()))).getImage(),
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
                }));
    }

    private double rescaleOnScroll(ScrollEvent event, Grogu.Axis axis) {
        boolean isNegative = Double.toString(event.getDeltaY()).contains("-");
        return axis == Grogu.Axis.X
                ? isNegative ? W2CCoords.xScale * GenConfig.Image.ScaleFactor : W2CCoords.xScale / GenConfig.Image.ScaleFactor
                : isNegative ? W2CCoords.yScale * GenConfig.Image.ScaleFactor : W2CCoords.yScale / GenConfig.Image.ScaleFactor;
    }
}
