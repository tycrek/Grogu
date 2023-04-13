package dev.jmoore.window;

import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Data;
import lombok.val;

@Data
public class UtilityGrid {

    // Mouse position label
    private final Label mousePositionLabel = new Label("Mouse position:");
    private final Label mousePositionCoordsLabel = new Label("0x0");

    // Mandelbrot labels
    private final Label isInSetLabel = new Label("Is in set: false");
    private final Label iterationCountLabel = new Label("Iteration count: 0");
    private final Label timeTakenLabel = new Label("Time taken: 0ms");
    private final Label generatingLabel = new Label("Generating...");

    // GridPane & Button
    private final GridPane gridPane = new GridPane();

    private ConfigureBox configureBox;

    public static UtilityGrid build(Stage stage) {
        val ug = new UtilityGrid();
        ug.configureBox = new ConfigureBox(ug, stage);

        // Create the grid pane and add the nodes
        ug.getGridPane().setPadding(new Insets(16, 0, 0, 16));
        ug.getGridPane().getColumnConstraints().addAll(
                new ColumnConstraints(100),
                new ColumnConstraints(100));
        ug.getGridPane().setVgap(4);
        // Row 0
        // Row 1
        // Row 2
        ug.getGridPane().add(ug.getMousePositionLabel(), 0, 2);
        // Row 3
        ug.getGridPane().add(ug.getMousePositionCoordsLabel(), 0, 3, 2, 1);
        // Row 4
        ug.getGridPane().add(ug.getIsInSetLabel(), 0, 4);
        // Row 5
        ug.getGridPane().add(ug.getIterationCountLabel(), 0, 5, 2, 1);
        // Row 6
        ug.getGridPane().add(ug.getTimeTakenLabel(), 0, 6, 2, 1);
        // Row 7
        ug.getGridPane().add(ug.getGeneratingLabel(), 0, 7);

        // Style the labels
        ug.getGridPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;"));

        // Override "Generating..." label style
        ug.getGeneratingLabel().setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 16;");

        return ug;
    }

    public static void updateRootPaneBackground(UtilityGrid ug, Stage stage) {
        Grogu.isGenerating.set(true);
        ug.getGeneratingLabel().setVisible(true);

        // Generate the fractal asynchronously
        ImageGen.generateAsync((int) W2CCoords.width, (int) W2CCoords.height)

                // * Platform.runLater() is required to update the UI from a different thread
                .thenAccept(fractal -> Platform.runLater(() -> {
                    Grogu.isGenerating.set(false);

                    // Update the labels
                    ug.getTimeTakenLabel().setText((String.format("Time taken: %sms", fractal.getDuration())));
                    ug.getGeneratingLabel().setVisible(false);

                    // Set the background image
                    Window.rootPane.get().setBackground(new Background(new BackgroundImage(
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
}
