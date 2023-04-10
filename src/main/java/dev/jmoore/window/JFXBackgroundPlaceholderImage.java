package dev.jmoore.window;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class JFXBackgroundPlaceholderImage {

    private static final String PLACEHOLDER = "https://via.placeholder.com/%dx%d";

    public static Background get(int width, int height) {
        return new Background(
                new BackgroundImage(
                        new Image(String.format(PLACEHOLDER, width, height)),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT));
    }
}
