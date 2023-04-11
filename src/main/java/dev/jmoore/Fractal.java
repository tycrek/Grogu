package dev.jmoore;

import lombok.Data;

public class Fractal {
    public static FractalResult isInMandelbrotSet(double realPartC, double imaginaryPartC) {

        // Set Z_0 to 0 initially
        double realPartZ = GenConfig.Fractal.RealPartZ;
        double imaginaryPartZ = GenConfig.Fractal.ImaginaryPartZ;

        // Iterate the Mandelbrot equation
        int iteration = 0;
        while ((iteration < GenConfig.Fractal.Iterations)
                && ((realPartZ * realPartZ) + (imaginaryPartZ * imaginaryPartZ) < GenConfig.Fractal.EscapeRadius)) {
            double nextRealPartZ = (realPartZ * realPartZ) - (imaginaryPartZ * imaginaryPartZ) + realPartC;
            double nextImaginaryPartZ = GenConfig.Fractal.ZScale * (realPartZ * imaginaryPartZ) + imaginaryPartC;
            realPartZ = nextRealPartZ;
            imaginaryPartZ = nextImaginaryPartZ;
            iteration++;
        }

        if (iteration == GenConfig.Fractal.Iterations)
            // * The point is MOST LIKELY in the Mandelbrot set
            return new FractalResult(true, iteration);
        else
            // ! The point is NOT in the Mandelbrot set
            return new FractalResult(false, iteration);
    }

    @Data
    public static class FractalResult {
        private final boolean isInMandelbrotSet;
        private final int iterations;
    }
}
