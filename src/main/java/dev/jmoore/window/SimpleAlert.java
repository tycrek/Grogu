package dev.jmoore.window;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class SimpleAlert {

    public static void show(String title, String message) {
        show(title, message, ButtonType.OK);
    }

    public static void show(String title, String message, ButtonType buttonType) {
        var dialogPane = new DialogPane();
        var dialog = new Dialog<>();

        dialogPane.setContentText(message);
        dialogPane.getButtonTypes().add(buttonType);
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(title);
        dialog.show();
    }
}
