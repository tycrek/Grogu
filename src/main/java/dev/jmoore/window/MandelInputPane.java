package dev.jmoore.window;

import dev.jmoore.Grogu;
import dev.jmoore.Cartesian;
import dev.jmoore.window.events.TextFieldKeyTypedValidationHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import lombok.Getter;

public class MandelInputPane extends FlowPane {

    @Getter
    private final TextField textField;

    public MandelInputPane(Grogu.Axis axis, String use) {
        super(Orientation.HORIZONTAL, 4.0, 4.0);

        // Create the text field
        textField = new TextField(Double.toString(use.equals("Scale") ? (axis == Grogu.Axis.X ? Cartesian.Coords.xScale : Cartesian.Coords.yScale) : Cartesian.Coords.centerX));

        // * Any key handler: handles input validation per character
        textField.addEventHandler(KeyEvent.KEY_TYPED, new TextFieldKeyTypedValidationHandler(textField, true, true));

        // * Axis label
        var label = new Label(String.format("%s Axis %s:", axis.name(), use));
        label.setStyle("-fx-font-weight: bold;");

        this.getChildren().addAll(label, textField);
    }
}
