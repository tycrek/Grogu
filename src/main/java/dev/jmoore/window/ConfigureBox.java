package dev.jmoore.window;

import dev.jmoore.Cartesian;
import dev.jmoore.Configuration;
import dev.jmoore.Grogu;
import dev.jmoore.window.events.TextFieldKeyTypedValidationHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class ConfigureBox {

    private final Stage childWindow;

    // Coordinates
    private final MandelInputPane scaleXInput = new MandelInputPane(Grogu.Axis.X, "Scale");
    private final MandelInputPane scaleYInput = new MandelInputPane(Grogu.Axis.Y, "Scale");
    private final MandelInputPane centerXInput = new MandelInputPane(Grogu.Axis.X, "Center");
    private final MandelInputPane centerYInput = new MandelInputPane(Grogu.Axis.Y, "Center");
    // Fractal config text fields
    private final TextField fractalIterationsTextField = new TextField(Integer.toString(Configuration.Fractal.Iterations));
    private final TextField fractalEscapeRadiusTextField = new TextField(Double.toString(Configuration.Fractal.EscapeRadius));
    private final TextField fractalZScaleTextField = new TextField(Double.toString(Configuration.Fractal.ZScale));
    private final TextField fractalRealPartZTextField = new TextField(Double.toString(Configuration.Fractal.RealPartZ));
    private final TextField fractalImaginaryPartZTextField = new TextField(Double.toString(Configuration.Fractal.ImaginaryPartZ));
    // Image config text fields
    private final TextField resolutionXTextField = new TextField(Integer.toString(Configuration.Image.ResolutionX));
    private final TextField resolutionYTextField = new TextField(Integer.toString(Configuration.Image.ResolutionY));

    // Fractal config labels
    private final Label fractalIterationsLabel = new Label("Iterations:");
    private final Label fractalEscapeRadiusLabel = new Label("Escape radius:");
    private final Label fractalZScaleLabel = new Label("Z scale:");
    private final Label fractalRealPartZLabel = new Label("Real part Z:");
    private final Label fractalImaginaryPartZLabel = new Label("Imaginary part Z:");
    // Image config labels
    private final Label resolutionXLabel = new Label("Resolution X:");
    private final Label resolutionYLabel = new Label("Resolution Y:");

    public ConfigureBox(Window window, Stage parentStage) {

        // Input handler
        final Consumer<KeyEvent> inputHandler = (event) -> {
            try {
                parseDoubles(this);
                if (event.getCode().equals(KeyCode.ENTER))
                    window.updateImageView();
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
                SimpleAlert.show("Invalid input", "Check your input and try again");
            }
        };

        // * Attach input handlers
        // Coordinates
        scaleXInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        scaleYInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        centerXInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        centerYInput.getTextField().addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        // Fractal config
        fractalIterationsTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        fractalIterationsTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(fractalIterationsTextField, false, false));
        fractalEscapeRadiusTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        fractalEscapeRadiusTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(fractalEscapeRadiusTextField, true, true));
        fractalZScaleTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        fractalZScaleTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(fractalZScaleTextField, true, true));
        fractalRealPartZTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        fractalRealPartZTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(fractalRealPartZTextField, true, true));
        fractalImaginaryPartZTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        fractalImaginaryPartZTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(fractalImaginaryPartZTextField, true, true));
        // Image config
        resolutionXTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        resolutionXTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(resolutionXTextField, false, false));
        resolutionYTextField.addEventHandler(KeyEvent.KEY_RELEASED, inputHandler::accept);
        resolutionYTextField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(resolutionYTextField, false, false));

        // * Set up the GridPane
        GridPane gridPane = new GridPane();
        //gridPane.setHgap(10);
        gridPane.setVgap(10);

        List<Node> childNodes = new ArrayList<>(Arrays.asList(
                scaleXInput, scaleYInput,
                centerXInput, centerYInput,
                fractalIterationsLabel, fractalIterationsTextField,
                fractalEscapeRadiusLabel, fractalEscapeRadiusTextField,
                fractalZScaleLabel, fractalZScaleTextField,
                fractalRealPartZLabel, fractalRealPartZTextField,
                fractalImaginaryPartZLabel, fractalImaginaryPartZTextField,
                resolutionXLabel, resolutionXTextField,
                resolutionYLabel, resolutionYTextField
        ));

        // Iterate over the child nodes and add them to the grid
        int col = 0, row = 0;
        for (Node childNode : childNodes) {
            // Add the node to the grid
            gridPane.add(childNode, col, row);

            // Use modulo operator to switch columns every other node
            col = (col + 1) % 2;

            // Move to the next row after every second column
            if (col == 0) row++;
        }

        val scene = new Scene(gridPane, 480, 400);
        childWindow = new Stage();
        childWindow.setTitle("Configuration");
        childWindow.setScene(scene);

        // Close on ESC
        childWindow.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.ESCAPE))
                childWindow.close();
        });
    }

    public static void parseDoubles(ConfigureBox cb) {
        // Coordinates
        var xText = cb.getScaleXInput().getTextField().getText();
        var yText = cb.getScaleYInput().getTextField().getText();
        var xCenterText = cb.getCenterXInput().getTextField().getText();
        var yCenterText = cb.getCenterYInput().getTextField().getText();
        // Fractal config
        var fractalIterationsText = cb.getFractalIterationsTextField().getText();
        var fractalEscapeRadiusText = cb.getFractalEscapeRadiusTextField().getText();
        var fractalZScaleText = cb.getFractalZScaleTextField().getText();
        var fractalRealPartZText = cb.getFractalRealPartZTextField().getText();
        var fractalImaginaryPartZText = cb.getFractalImaginaryPartZTextField().getText();
        // Image config
        var resolutionXText = cb.getResolutionXTextField().getText();
        var resolutionYText = cb.getResolutionYTextField().getText();

        // Coordinates
        Cartesian.Coords.xScale = Double.parseDouble(xText);
        Cartesian.Coords.yScale = Double.parseDouble(yText);
        Cartesian.Coords.centerX = Double.parseDouble(xCenterText);
        Cartesian.Coords.centerY = Double.parseDouble(yCenterText);
        // Fractal config
        Configuration.Fractal.Iterations = Integer.parseInt(fractalIterationsText);
        Configuration.Fractal.EscapeRadius = Double.parseDouble(fractalEscapeRadiusText);
        Configuration.Fractal.ZScale = Double.parseDouble(fractalZScaleText);
        Configuration.Fractal.RealPartZ = Double.parseDouble(fractalRealPartZText);
        Configuration.Fractal.ImaginaryPartZ = Double.parseDouble(fractalImaginaryPartZText);
        // Image config
        Configuration.Image.ResolutionX = Integer.parseInt(resolutionXText);
        Configuration.Image.ResolutionY = Integer.parseInt(resolutionYText);
    }
}
