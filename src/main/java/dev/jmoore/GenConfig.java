package dev.jmoore;

public class GenConfig {
    public record Fractal() {
        static int Iterations = 1000;
        static double RealPartZ = 0;
        static double ImaginaryPartZ = 0;
        static double EscapeRadius = 4;
    }

    public record Image() {
        public static int Resolution = 2;
    }
}
