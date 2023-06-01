package dev.jmoore.window;

import dev.jmoore.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
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
    @Getter
    private ImageView imageView;

    @Override
    public void start(Stage stage) {
        // * JavaFX hierarchy: Stage -> Scene -> Pane -> Node(s)

        //#region           UI setup

        // Bottom bar & Configure box
        bottomBar = new BottomBar(this);
        configureBox = new ConfigureBox(this, stage);

        // Image view
        imageView = new ImageView();
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);

        // When right-clicked, open configure box
        imageView.setOnContextMenuRequested(event -> bottomBar.getConfigureButton().fire());

        // Primary pane
        StackPane rootPane = new StackPane(imageView, bottomBar.getBar());
        StackPane.setAlignment(bottomBar.getBar(), Pos.BOTTOM_CENTER);
        ROOT_PANE.set(rootPane);

        // Primary scene
        Scene scene = new Scene(rootPane, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Mouse MOVE listener
        scene.setOnMouseMoved(event -> {
            double[] cartesian = Cartesian.convert(event.getSceneX(), event.getSceneY());

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
            // Check when button it is
            if (Grogu.isGenerating.get() || event.getButton() == MouseButton.SECONDARY) return;

            // Convert to cartesian coordinates
            double[] cartesian = Cartesian.convert(event.getSceneX(), event.getSceneY());
            Cartesian.Coords.centerX = cartesian[0];
            Cartesian.Coords.centerY = cartesian[1];
            configureBox.getCenterXInput().getTextField().setText(Double.toString(Cartesian.Coords.centerX));
            configureBox.getCenterYInput().getTextField().setText(Double.toString(Cartesian.Coords.centerY));
            updateImageView();
        });

        // "Zoom" on scroll
        scene.setOnScroll(event -> {
            if (Grogu.isGenerating.get()) return;

            Cartesian.Coords.xScale = rescaleOnScroll(event, Grogu.Axis.X);
            Cartesian.Coords.yScale = rescaleOnScroll(event, Grogu.Axis.Y);
            configureBox.getScaleXInput().getTextField().setText(Double.toString(Cartesian.Coords.xScale));
            configureBox.getScaleYInput().getTextField().setText(Double.toString(Cartesian.Coords.yScale));
            updateImageView();
        });

        //#endregion
        //#region       Window resize operations

        // Constructs a window size listener for the given axis
        Function<Grogu.Axis, ChangeListener<Number>> makeListener = (axis) -> (obs, oldSize, newSize) -> {
            System.out.printf("Window %s resized from %s to %s%n", axis, oldSize, newSize);

            if (axis == Grogu.Axis.X)
                Cartesian.Coords.width = newSize.doubleValue() - 16;
            else if (axis == Grogu.Axis.Y)
                Cartesian.Coords.height = newSize.doubleValue() - bottomBar.getBar().getHeight() / 2;

            // Calculate aspect ratio to maintain the Cartesian Coords scales (thanks CoPilot)
            double aspectRatio = Cartesian.Coords.width / Cartesian.Coords.height;
            if (aspectRatio > 1) {
                Cartesian.Coords.xScale = Cartesian.Coords.yScale * aspectRatio;
                configureBox.getScaleXInput().getTextField().setText(Double.toString(Cartesian.Coords.xScale));
            } else {
                Cartesian.Coords.yScale = Cartesian.Coords.xScale / aspectRatio;
                configureBox.getScaleYInput().getTextField().setText(Double.toString(Cartesian.Coords.yScale));
            }

            if (Grogu.isGenerating.get()) return;
            updateImageView();
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
        updateImageView();

        //#endregion
    }

    public void updateImageView() {
        Grogu.isGenerating.set(true);
        bottomBar.getGeneratingLabel().setVisible(true);

        // Generate the fractal asynchronously
        ImageGen.generateAsync((int) Cartesian.Coords.width, (int) Cartesian.Coords.height)

                // * Platform.runLater() is required to update the UI from a different thread
                .thenAccept(fractal -> Platform.runLater(() -> {
                    Grogu.isGenerating.set(false);

                    // Update the labels
                    bottomBar.getTimeTakenLabel().setText((String.format("Time taken:%n%sms", fractal.getDuration())));
                    bottomBar.getGeneratingLabel().setVisible(false);

                    // Update the image
                    imageView.setImage(fractal.getImage());
                }));
    }

    private double rescaleOnScroll(ScrollEvent event, Grogu.Axis axis) {
        boolean isNegative = Double.toString(event.getDeltaY()).contains("-");
        return axis == Grogu.Axis.X
                ? isNegative ? Cartesian.Coords.xScale * Configuration.Image.ScaleFactor : Cartesian.Coords.xScale / Configuration.Image.ScaleFactor
                : isNegative ? Cartesian.Coords.yScale * Configuration.Image.ScaleFactor : Cartesian.Coords.yScale / Configuration.Image.ScaleFactor;
    }
}
