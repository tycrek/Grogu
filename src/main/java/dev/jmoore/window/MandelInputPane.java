package dev.jmoore.window;

import dev.jmoore.Grogu;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MandelInputPane extends FlowPane {
    public MandelInputPane(Grogu.Axis axis) {
        super(Orientation.HORIZONTAL, 4.0, 4.0);

        // Create the text field
        TextField textField = new TextField();

        // * ENTER key handler: <currently does nothing>
        textField.setOnAction(event -> {
            System.out.println("Input provided: " + textField.getText());
            try {
                double value = Double.parseDouble(textField.getText());
            } catch (NumberFormatException e) {
                SimpleAlert.show("Invalid input", "Invalid input: " + textField.getText());
            }
        });

        this.getChildren().addAll(
                new Label(axis.name() + " Axis:"),
                textField);
    }
}
