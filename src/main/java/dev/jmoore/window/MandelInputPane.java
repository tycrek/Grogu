package dev.jmoore.window;

import dev.jmoore.Grogu;
import dev.jmoore.grid.W2CCoords;
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
        textField = new TextField(Double.toString(use.equals("Scale") ? W2CCoords.xScale : W2CCoords.centerX));

        // * Any key handler: handles input validation per character
        textField.addEventHandler(KeyEvent.KEY_TYPED, (event) -> {
            var input = event.getCharacter();

            // Check how many decimal points are in the text field
            int decimalCount = 0;
            for (char c : textField.getText().toCharArray())
                if (c == '.')
                    decimalCount++;
            System.out.printf("Decimal count: %d%n", decimalCount);

            // Input validation
            if ((input.equals(".") && decimalCount < 1) // ? Only one decimal point allowed
                    || (input.equals("\b") // ? Allow backspace
                    || input.equals("\u007F") // ? Allow delete
                    || input.equals("\t") // ? Allow tab
                    || input.equals("\r") // ? Allow enter
                    || input.matches("[0-9]"))) // ? Allow numbers
                return;

            // Otherwise, block the event
            event.consume();
            System.out.printf("Key input blocked: %s%n", input);

            // Show an alert
            SimpleAlert.show("Invalid input", String.format("Invalid input: %s%n%nOnly numbers (including decimals) are allowed.", input));
        });

        // * Axis label
        var label = new Label(String.format("%s Axis %s:", axis.name(), use));
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        this.getChildren().addAll(label, textField);
    }
}
