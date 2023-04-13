package dev.jmoore;

import dev.jmoore.window.Window;
import javafx.application.Application;

import java.util.concurrent.atomic.AtomicBoolean;

public class Grogu {
    public static final AtomicBoolean isGenerating = new AtomicBoolean(false);

    public static void main(String[] args) {
        Application.launch(Window.class, args);
    }

    public enum Axis {
        X, Y
    }
}
