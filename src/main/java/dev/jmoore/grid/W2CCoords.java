package dev.jmoore.grid;

import dev.jmoore.window.Window;

public record W2CCoords() {
    public static double
            width = Window.WIDTH, height = Window.HEIGHT,
            centerX = 0.0, centerY = 0.0,
            xScale = 4.0, yScale = 3.0;
}
