package dev.jmoore.window.events;

import dev.jmoore.window.SimpleAlert;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TextFieldKeyTypedValidationHandler implements EventHandler<KeyEvent> {

    private final TextField textField;
    private final boolean allowNegative;
    private final boolean allowDecimal;

    @Override
    public void handle(KeyEvent event) {
        var input = event.getCharacter();

        // Check how many decimal points are in the text field
        int decimalCount = 0;
        for (char c : textField.getText().toCharArray())
            if (c == '.')
                decimalCount++;

        // Input validation
        if ((allowDecimal && input.equals(".") && decimalCount < 1) // ? Only one decimal point allowed;
                || (input.equals("\b") // ? Allow backspace
                || input.equals("\u007F") // ? Allow delete
                || input.equals("\t") // ? Allow tab
                || input.equals("\r") // ? Allow enter
                || input.matches("[0-9]"))) // ? Allow numbers
            return;

        // Last-minute override checks
        if ((input.equals("-") && allowNegative) // ? Allow negative numbers
                || (input.equals(".") && allowDecimal)) // ? Allow decimals
            return;

        // Otherwise, block the event
        event.consume();
        System.out.printf("Key input blocked: %s%n", input);

        // Show an alert
        SimpleAlert.show("Invalid input", String.format("Invalid input: %s%n%nOnly numbers (including decimals) are allowed.", input));
    }
}
