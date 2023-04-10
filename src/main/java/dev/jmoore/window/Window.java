package dev.jmoore.window;

import dev.jmoore.Grogu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.val;

public class Window extends Application {

    final static int WIDTH = 800;
    final static int HEIGHT = 600;

    @Override
    public void start(Stage stage) {

        //#region           UI setup

        // Mouse position label
        val mousePositionLabel = new Label("Mouse position: 0x0");
        var gridPane = new GridPane();
        // Row 0
        gridPane.add(mousePositionLabel, 0, 0);

        StackPane ROOT = new StackPane(gridPane);

        // Primary scene
        Scene scene = new Scene(ROOT, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        //#endregion
    }
}
