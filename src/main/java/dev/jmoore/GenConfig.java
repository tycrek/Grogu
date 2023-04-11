package dev.jmoore;

public class GenConfig {
    public record Fractal() {
        static int Iterations = 50;
        static double RealPartZ = 0.0; // 0.0
        static double ImaginaryPartZ = 0.0; // 0.0
        static double EscapeRadius = 4.0; // 4.0
        static double ZScale = 2.0; // 2.0
    }

    public record Image() {
        public static int Resolution = 2;
    }
}
