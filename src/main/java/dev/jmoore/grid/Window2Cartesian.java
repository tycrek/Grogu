package dev.jmoore.grid;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Window2Cartesian {

    /**
     * Thanks ChatGPT
     */
    public static double[] convert(double x, double y) {
        double sx = W2CCoords.xScale / W2CCoords.width; // pixel-to-Cartesian scale factor for x-axis
        double sy = W2CCoords.yScale / W2CCoords.height; // pixel-to-Cartesian scale factor for y-axis

        // Calculate Cartesian coordinates of mouse cursor
        double cartesianX = W2CCoords.cartesianCenterX + (x - W2CCoords.width / 2) * sx;
        double cartesianY = W2CCoords.cartesianCenterY - (y - W2CCoords.height / 2) * sy;

        return new double[]{cartesianX, cartesianY};
    }
}
