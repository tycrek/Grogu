package dev.jmoore.window;

import dev.jmoore.GenConfig;
import dev.jmoore.Grogu;
import dev.jmoore.ImageGen;
import dev.jmoore.grid.W2CCoords;
import dev.jmoore.window.events.TextFieldKeyTypedValidationHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    // Fractal config labels
    private final Label fractalIterationsLabel = new Label("Iterations:");
    private final Label fractalEscapeRadiusLabel = new Label("Escape radius:");
    private final Label fractalZScaleLabel = new Label("Z scale:");
    private final Label fractalRealPartZLabel = new Label("Real part Z:");
    private final Label fractalImaginaryPartZLabel = new Label("Imaginary part Z:");

    // Fractal config text fields
    private final TextField fractalIterationsTextField = new TextField(Integer.toString(GenConfig.Fractal.Iterations));
    private final TextField fractalEscapeRadiusTextField = new TextField(Double.toString(GenConfig.Fractal.EscapeRadius));
    private final TextField fractalZScaleTextField = new TextField(Double.toString(GenConfig.Fractal.ZScale));
    private final TextField fractalRealPartZTextField = new TextField(Double.toString(GenConfig.Fractal.RealPartZ));
    private final TextField fractalImaginaryPartZTextField = new TextField(Double.toString(GenConfig.Fractal.ImaginaryPartZ));

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

            // Fractal config
            var fractalIterationsText = ug.getFractalIterationsTextField().getText();
            var fractalEscapeRadiusText = ug.getFractalEscapeRadiusTextField().getText();
            var fractalZScaleText = ug.getFractalZScaleTextField().getText();
            var fractalRealPartZText = ug.getFractalRealPartZTextField().getText();
            var fractalImaginaryPartZText = ug.getFractalImaginaryPartZTextField().getText();

            try {
                W2CCoords.xScale = Double.parseDouble(xText);
                W2CCoords.yScale = Double.parseDouble(yText);
                W2CCoords.centerX = Double.parseDouble(xCenterText);
                W2CCoords.centerY = Double.parseDouble(yCenterText);

                // Fractal config
                GenConfig.Fractal.Iterations = Integer.parseInt(fractalIterationsText);
                GenConfig.Fractal.EscapeRadius = Double.parseDouble(fractalEscapeRadiusText);
                GenConfig.Fractal.ZScale = Double.parseDouble(fractalZScaleText);
                GenConfig.Fractal.RealPartZ = Double.parseDouble(fractalRealPartZText);
                GenConfig.Fractal.ImaginaryPartZ = Double.parseDouble(fractalImaginaryPartZText);

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
        // Fractal config
        ug.getFractalIterationsTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getFractalIterationsTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalIterationsTextField(), false, false));
        ug.getFractalEscapeRadiusTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getFractalEscapeRadiusTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalEscapeRadiusTextField(), true, true));
        ug.getFractalZScaleTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getFractalZScaleTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalZScaleTextField(), true, true));
        ug.getFractalRealPartZTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getFractalRealPartZTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalRealPartZTextField(), true, true));
        ug.getFractalImaginaryPartZTextField().addEventHandler(KeyEvent.KEY_RELEASED, event -> inputHandler.run());
        ug.getFractalImaginaryPartZTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalImaginaryPartZTextField(), true, true));

        // Create the grid pane and add the nodes
        ug.getGridPane().setPadding(new Insets(16, 0, 0, 16));
        ug.getGridPane().getColumnConstraints().addAll(new ColumnConstraints(220), new ColumnConstraints(220));
        ug.getGridPane().setVgap(4);
        // Row 0
        ug.getGridPane().add(ug.getScaleXInput(), 0, 0);
        ug.getGridPane().add(ug.getScaleYInput(), 1, 0);
        // Fractal config
        ug.getGridPane().add(ug.getFractalIterationsLabel(), 2, 0);
        ug.getGridPane().add(ug.getFractalIterationsTextField(), 2, 1);
        ug.getGridPane().add(ug.getFractalEscapeRadiusLabel(), 3, 0);
        ug.getGridPane().add(ug.getFractalEscapeRadiusTextField(), 3, 1);
        ug.getGridPane().add(ug.getFractalZScaleLabel(), 4, 0);
        ug.getGridPane().add(ug.getFractalZScaleTextField(), 4, 1);
        ug.getGridPane().add(ug.getFractalRealPartZLabel(), 5, 0);
        ug.getGridPane().add(ug.getFractalRealPartZTextField(), 5, 1);
        ug.getGridPane().add(ug.getFractalImaginaryPartZLabel(), 6, 0);
        ug.getGridPane().add(ug.getFractalImaginaryPartZTextField(), 6, 1);

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
