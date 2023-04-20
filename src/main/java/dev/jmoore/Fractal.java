package dev.jmoore;

import lombok.Data;

public class Fractal {
    public static FractalResult isInMandelbrotSet(double realPartC, double imaginaryPartC) {

        // Set Z_0 to 0 initially
        double realPartZ = Configuration.Fractal.RealPartZ;
        double imaginaryPartZ = Configuration.Fractal.ImaginaryPartZ;

        // Iterate the Mandelbrot equation
        int iteration = 0;
        while ((iteration < Configuration.Fractal.Iterations)
                && ((realPartZ * realPartZ) + (imaginaryPartZ * imaginaryPartZ) < Configuration.Fractal.EscapeRadius)) {
            double nextRealPartZ = (realPartZ * realPartZ) - (imaginaryPartZ * imaginaryPartZ) + realPartC;
            double nextImaginaryPartZ = Configuration.Fractal.ZScale * (realPartZ * imaginaryPartZ) + imaginaryPartC;
            realPartZ = nextRealPartZ;
            imaginaryPartZ = nextImaginaryPartZ;
            iteration++;
        }

        return new FractalResult(iteration == Configuration.Fractal.Iterations, iteration);
    }

    @Data
    public static class FractalResult {
        private final boolean isInMandelbrotSet;
        private final int iterations;
    }
}
