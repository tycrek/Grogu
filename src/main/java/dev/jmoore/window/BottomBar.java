package dev.jmoore.window;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import lombok.Getter;

@Getter
public class BottomBar {

    private final HBox bar;

    // Mouse position label
    private final Label mousePositionLabel = new Label("Mouse position:\nX:\nY:");

    // Mandelbrot labels
    private final Label isInSetLabel = new Label("Is in set:\nfalse");
    private final Label iterationCountLabel = new Label("Iteration count:\n0");
    private final Label timeTakenLabel = new Label("Time taken:\n0ms");
    private final Label generatingLabel = new Label("Generating...");

    private final Button configureButton = new Button("Configure");

    {
        // Set the preferred width of the mouse position label
        mousePositionLabel.setPrefWidth(200);
    }

    public BottomBar(Window window) {
        // Bottom bar spacer to push the button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Configure button will open the configure box
        configureButton.setOnAction(event -> {
            window.getConfigureBox().getChildWindow().show();
            window.getConfigureBox().getChildWindow().requestFocus();
        });

        // Bottom bar
        bar = new HBox(mousePositionLabel, isInSetLabel, iterationCountLabel, timeTakenLabel, generatingLabel, spacer, configureButton);
        bar.setStyle("-fx-background-color: #222; -fx-padding: 4px;");
        bar.setMaxHeight(Region.USE_PREF_SIZE);
        bar.setAlignment(Pos.CENTER);
        bar.setSpacing(10);

        // Style the Labels
        bar.getChildren().filtered((node) -> node instanceof Label)
                .forEach(node -> node.setStyle("-fx-font-weight: bold; -fx-text-fill: white;"));

        // Override the style of the generating label
        generatingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 16;");
    }
}
