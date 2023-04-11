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
        double fooX = W2CCoords.cartesianCenterX + (x - W2CCoords.width / 2) * sx;
        double fooY = W2CCoords.cartesianCenterY - (y - W2CCoords.height / 2) * sy;

        // Trim to 4 decimal places
        final double ROUND = 10000.0;
        fooX = Math.round(fooX * ROUND) / ROUND;
        fooY = Math.round(fooY * ROUND) / ROUND;

        return new double[]{fooX, fooY};
    }
}
