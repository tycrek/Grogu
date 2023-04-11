package dev.jmoore;

public class GenConfig {
    public record Fractal() {
        public static int Iterations = 50;
        public static double RealPartZ = 0.0; // 0.0
        public static double ImaginaryPartZ = 0.0; // 0.0
        public static double EscapeRadius = 4.0; // 4.0
        public static double ZScale = 2.0; // 2.0
    }

    public record Image() {
        public static int ResolutionX = 1;
        public static int ResolutionY = 1;
        public static double ScaleFactor = 2.0;
    }
}
