package dev.jmoore.grid;

import dev.jmoore.window.Window;

public record W2CCoords() {
    public static double
            width = Window.WIDTH, height = Window.HEIGHT,
            centerX = 0, centerY = 0,
            cartesianCenterX = 0, cartesianCenterY = 0,
            xScale = 2.0, yScale = 2.0;
}
