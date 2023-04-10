package dev.jmoore.grid;

import dev.jmoore.window.Window;

public record W2CCoords() {
    public static double
            width = Window.WIDTH, height = Window.HEIGHT,
            x = 0, y = 0,
            xScale = 2, yScale = 2;
}
