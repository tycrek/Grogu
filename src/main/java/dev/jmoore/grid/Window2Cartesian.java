package dev.jmoore.grid;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Window2Cartesian {

    /**
     * Thanks to ChatGPT for pixel-to-Cartesian conversion code!
     */
    public static double[] convert(double x, double y) {
        double scaleFactorX = W2CCoords.xScale / W2CCoords.width; // pixel-to-Cartesian scale factor for x-axis
        double scaleFactorY = W2CCoords.yScale / W2CCoords.height; // pixel-to-Cartesian scale factor for y-axis

        // Calculate Cartesian coordinates
        double cartesianX = W2CCoords.centerX + (x - W2CCoords.width / 2) * scaleFactorX;
        double cartesianY = W2CCoords.centerY - (y - W2CCoords.height / 2) * scaleFactorY;

        return new double[]{cartesianX, cartesianY};
    }
}
