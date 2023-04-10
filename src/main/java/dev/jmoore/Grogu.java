package dev.jmoore;

import dev.jmoore.window.Window;
import javafx.application.Application;

public class Grogu {
    public static void main(String[] args) {
        Application.launch(Window.class, args);
    }

    public enum Axis {
        X, Y
    }
}
