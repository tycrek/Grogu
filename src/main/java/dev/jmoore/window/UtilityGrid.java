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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Data;
import lombok.val;

import java.util.function.Consumer;

@Data
public class UtilityGrid {

    private final MandelInputPane scaleXInput = new MandelInputPane(Grogu.Axis.X, "Scale");
    private final MandelInputPane scaleYInput = new MandelInputPane(Grogu.Axis.Y, "Scale");
    private final MandelInputPane centerXInput = new MandelInputPane(Grogu.Axis.X, "Center");
    private final MandelInputPane centerYInput = new MandelInputPane(Grogu.Axis.Y, "Center");

    // Fractal config labels
    private final Label fractalIterationsLabel = new Label("Iterations:");
    private final Label fractalEscapeRadiusLabel = new Label("Escape radius:");
    private final Label fractalZScaleLabel = new Label("Z scale:");
    private final Label fractalRealPartZLabel = new Label("Real part Z:");
    private final Label fractalImaginaryPartZLabel = new Label("Imaginary part Z:");
    // Image config labels
    private final Label resolutionXLabel = new Label("Resolution X:");
    private final Label resolutionYLabel = new Label("Resolution Y:");

    // Fractal config text fields
    private final TextField fractalIterationsTextField = new TextField(Integer.toString(GenConfig.Fractal.Iterations));
    private final TextField fractalEscapeRadiusTextField = new TextField(Double.toString(GenConfig.Fractal.EscapeRadius));
    private final TextField fractalZScaleTextField = new TextField(Double.toString(GenConfig.Fractal.ZScale));
    private final TextField fractalRealPartZTextField = new TextField(Double.toString(GenConfig.Fractal.RealPartZ));
    private final TextField fractalImaginaryPartZTextField = new TextField(Double.toString(GenConfig.Fractal.ImaginaryPartZ));
    // Image config text fields
    private final TextField resolutionXTextField = new TextField(Integer.toString(GenConfig.Image.ResolutionX));
    private final TextField resolutionYTextField = new TextField(Integer.toString(GenConfig.Image.ResolutionY));

    // Mouse position label
    private final Label mousePositionLabel = new Label("Mouse position:");
    private final Label mousePositionCoordsLabel = new Label("0x0");

    // Mandelbrot labels
    private final Label isInSetLabel = new Label("Is in set: false");
    private final Label iterationCountLabel = new Label("Iteration count: 0");
    private final Label timeTakenLabel = new Label("Time taken: 0ms");

    // GridPane & Button
    private final GridPane gridPane = new GridPane();
    private final Button button = new Button("Generate");

    public static UtilityGrid build(Stage stage) {
        val ug = new UtilityGrid();

        // Input handler
        final Consumer<KeyEvent> inputHandler = (event) -> {
            try {
                parseDoubles(ug);

                // Only update image if key was TAB or ENTER
                if (event.getCode().equals(KeyCode.TAB) || event.getCode().equals(KeyCode.ENTER))
                    updateRootPaneBackground(new ImageView(new Image(ImageGen.toInputStream(ImageGen.generate((int) W2CCoords.width, (int) W2CCoords.height, ug)))), stage);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
        };

        // Add event handlers to the input panes
        ug.getScaleXInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getScaleYInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getCenterXInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getCenterYInput().getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);

        // Fractal config
        ug.getFractalIterationsTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getFractalIterationsTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalIterationsTextField(), false, false));
        ug.getFractalEscapeRadiusTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getFractalEscapeRadiusTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalEscapeRadiusTextField(), true, true));
        ug.getFractalZScaleTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getFractalZScaleTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalZScaleTextField(), true, true));
        ug.getFractalRealPartZTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getFractalRealPartZTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalRealPartZTextField(), true, true));
        ug.getFractalImaginaryPartZTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getFractalImaginaryPartZTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getFractalImaginaryPartZTextField(), true, true));
        // Image config
        ug.getResolutionXTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getResolutionXTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getResolutionXTextField(), false, false));
        ug.getResolutionYTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        ug.getResolutionYTextField().addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(ug.getResolutionYTextField(), false, false));

        // Create the grid pane and add the nodes
        ug.getGridPane().setPadding(new Insets(16, 0, 0, 16));
        ug.getGridPane().getColumnConstraints().addAll(
                new ColumnConstraints(100),
                new ColumnConstraints(100));
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
        // Image config
        ug.getGridPane().add(ug.getResolutionXLabel(), 7, 0);
        ug.getGridPane().add(ug.getResolutionXTextField(), 7, 1);
        ug.getGridPane().add(ug.getResolutionYLabel(), 8, 0);
        ug.getGridPane().add(ug.getResolutionYTextField(), 8, 1);

        // Row 1
        ug.getGridPane().add(ug.getCenterXInput(), 0, 1);
        ug.getGridPane().add(ug.getCenterYInput(), 1, 1);
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

        // Style the labels
        ug.getGridPane().getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;"));

        return ug;
    }

    public static void parseDoubles(UtilityGrid ug) {
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
        // Image config
        var resolutionXText = ug.getResolutionXTextField().getText();
        var resolutionYText = ug.getResolutionYTextField().getText();

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
        // Image config
        GenConfig.Image.ResolutionX = Integer.parseInt(resolutionXText);
        GenConfig.Image.ResolutionY = Integer.parseInt(resolutionYText);
    }

    public static void updateRootPaneBackground(ImageView imageView, Stage stage) {
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
