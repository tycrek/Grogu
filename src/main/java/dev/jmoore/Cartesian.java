package dev.jmoore;

import dev.jmoore.window.Window;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cartesian {

    /**
     * Thanks to ChatGPT for pixel-to-Cartesian conversion code!
     * Comments and general cleanup done by tycrek.
     */
    public static double[] convert(double x, double y) {
        // Pixel-to-Cartesian scale factor for each axis
        double scaleFactorX = Coords.xScale / Coords.width;
        double scaleFactorY = Coords.yScale / Coords.height;

        // Calculate Cartesian coordinates
        double cartesianX = Coords.centerX + (x - Coords.width / 2) * scaleFactorX;
        double cartesianY = Coords.centerY - (y - Coords.height / 2) * scaleFactorY;

        return new double[]{cartesianX, cartesianY};
    }

    public record Coords() {
        public static double
                width = Window.WIDTH, height = Window.HEIGHT,
                centerX = 0.0, centerY = 0.0,
                xScale = 4.0, yScale = 3.0;
    }
}
