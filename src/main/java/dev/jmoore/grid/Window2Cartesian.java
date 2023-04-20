package dev.jmoore.grid;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Window2Cartesian {

    /**
     * Thanks to ChatGPT for pixel-to-Cartesian conversion code!
     * Comments and general cleanup done by tycrek.
     */
    public static double[] convert(double x, double y) {
        // Pixel-to-Cartesian scale factor for each axis
        double scaleFactorX = W2CCoords.xScale / W2CCoords.width;
        double scaleFactorY = W2CCoords.yScale / W2CCoords.height;

        // Calculate Cartesian coordinates
        double cartesianX = W2CCoords.centerX + (x - W2CCoords.width / 2) * scaleFactorX;
        double cartesianY = W2CCoords.centerY - (y - W2CCoords.height / 2) * scaleFactorY;

        return new double[]{cartesianX, cartesianY};
    }
}
