package dev.jmoore.window;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import lombok.Getter;
import lombok.val;

@Getter
public class CartesianRangeGridPane {

    // Non-empty labels
    private static final Label topLabel = new Label("T");
    private static final Label leftLabel = new Label("L");
    private static final Label rightLabel = new Label("R");
    private static final Label bottomLabel = new Label("B");

    public static GridPane build() {

        // Create the grid
        val cartesianRangeGrid = new GridPane();
        cartesianRangeGrid.setAlignment(Pos.CENTER);

        // Add the labels
        cartesianRangeGrid.add(topLabel, 1, 0);
        cartesianRangeGrid.add(leftLabel, 0, 1);
        cartesianRangeGrid.add(rightLabel, 2, 1);
        cartesianRangeGrid.add(bottomLabel, 1, 2);

        // Add universal child properties
        cartesianRangeGrid.getChildren().forEach((child) -> {

            // Set label font weight bold, increase font size, and set colour
            if (child instanceof Label)
                child.setStyle("-fx-font-weight: bold; -fx-font-size: 24; -fx-text-fill: #FFF; -fx-fill: blue; -fx-stroke: black; -fx-stroke-width: 2px;");

            // Center the labels
            GridPane.setHalignment(child, HPos.CENTER);
            GridPane.setValignment(child, VPos.CENTER);

            // Add padding
            GridPane.setMargin(child, new Insets(20));
        });

        // Fix the positioning for the labels
        GridPane.setValignment(topLabel, VPos.TOP);
        GridPane.setHalignment(leftLabel, HPos.LEFT);
        GridPane.setHalignment(rightLabel, HPos.RIGHT);
        GridPane.setValignment(bottomLabel, VPos.BOTTOM);

        // Set the constraints for a 3x3 grid
        updateConstraints(Window.WIDTH, Window.HEIGHT, cartesianRangeGrid);

        return cartesianRangeGrid;
    }

    public static void updateConstraints(double width, double height, GridPane cartesianRangeGrid) {
        // 33% width and height constraints
        val colConstraints = new ColumnConstraints(width / 3);
        val rowConstraints = new RowConstraints(height / 3);

        // Remove the old constraints
        cartesianRangeGrid.getColumnConstraints().clear();
        cartesianRangeGrid.getRowConstraints().clear();

        // Add the new constraints
        cartesianRangeGrid.getColumnConstraints().addAll(colConstraints, colConstraints, colConstraints);
        cartesianRangeGrid.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints);
    }
}
