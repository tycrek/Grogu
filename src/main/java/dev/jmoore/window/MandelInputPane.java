package dev.jmoore.window;

import dev.jmoore.Grogu;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MandelInputPane extends FlowPane {
    public MandelInputPane(Grogu.Axis axis) {
        super(Orientation.HORIZONTAL, 4.0, 4.0);

        Label label = new Label(axis.name() + " Axis:");

        TextField textField = new TextField();
        this.getChildren().addAll(label, textField);
    }
}
