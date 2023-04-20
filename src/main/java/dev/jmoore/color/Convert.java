package dev.jmoore.color;

import dev.jmoore.Configuration;

public class Convert {
    public static int rgb2hex(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static int rgb2argb(int r, int g, int b) {
        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static int hex2argb(int hex) {
        return (255 << 24) | hex;
    }

    public static int scaleIterationsToRgb(int iterations, boolean invert) {
        iterations = invert ? Configuration.Fractal.Iterations - iterations : iterations;
        return (int) (((double) iterations / Configuration.Fractal.Iterations) * 255.0);
    }
}
