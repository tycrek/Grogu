package dev.jmoore.window;

import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Data;
import lombok.val;

@Data
public class UtilityGrid {

    private final MandelInputPane scaleXInput = new MandelInputPane(Grogu.Axis.X, "Scale");
    private final MandelInputPane scaleYInput = new MandelInputPane(Grogu.Axis.Y, "Scale");
    private final MandelInputPane centerXInput = new MandelInputPane(Grogu.Axis.X, "Center (not in use yet)");
    private final MandelInputPane centerYInput = new MandelInputPane(Grogu.Axis.Y, "Center (not in use yet)");

    // Mouse position label
    private final Label mousePositionLabel = new Label("Mouse position:");
    private final Label mousePositionCoordsLabel = new Label("0x0");

    // Mandelbrot labels
    private final Label isInSetLabel = new Label("Is in set: false");
    private final Label iterationCountLabel = new Label("Iteration count: 0");

    // GridPane & Button
    private final GridPane gridPane = new GridPane();
    private final Button button = new Button("Generate");

    public static UtilityGrid build(Stage stage) {
        val ug = new UtilityGrid();

        // Input handler
        final Runnable inputHandler = () -> {
            var xText = ug.getScaleXInput().getTextField().getText();
            var yText = ug.getScaleYInput().getTextField().getText();
            var xCenterText = ug.getCenterXInput().getTextField().getText();
            var yCenterText = ug.getCenterYInput().getTextField().getText();

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
        ug.getScaleXInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getScaleYInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getCenterXInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getCenterYInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());

        // Create the grid pane and add the nodes
        ug.getGridPane().setPadding(new Insets(16, 0, 0, 16));
        ug.getGridPane().getColumnConstraints().addAll(new ColumnConstraints(220), new ColumnConstraints(220));
        ug.getGridPane().setVgap(4);
        // Row 0
        ug.getGridPane().add(ug.getScaleXInput(), 0, 0);
        ug.getGridPane().add(ug.getScaleYInput(), 1, 0);
        // Row 1
        ug.getGridPane().add(ug.getCenterXInput(), 0, 1);
        ug.getGridPane().add(ug.getCenterYInput(), 1, 1);
        // Row 2
        ug.getGridPane().add(ug.getMousePositionLabel(), 0, 2);
        // Row 3
        ug.getGridPane().add(ug.getMousePositionCoordsLabel(), 0, 3);
        // Row 4
        ug.getGridPane().add(ug.getIsInSetLabel(), 0, 4);
        // Row 5
        ug.getGridPane().add(ug.getIterationCountLabel(), 0, 5);

        // Style the labels
        ug.getGridPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;"));

        // Button on last row
        ug.getGridPane().add(ug.getButton(), 0, 20);
        ug.getButton().addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                updateRootPaneBackground(new ImageView(new Image(ImageGen.toInputStream(ImageGen.generate((int) W2CCoords.width, (int) W2CCoords.height)))), stage));

        return ug;
    }

    static void updateRootPaneBackground(ImageView imageView, Stage stage) {
        Window.rootPane.get().setBackground(new Background(new BackgroundImage(
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
