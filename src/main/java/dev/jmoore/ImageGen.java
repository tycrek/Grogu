package dev.jmoore;

import dev.jmoore.color.Convert;
import dev.jmoore.color.HSLGen;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

public class ImageGen {
    public static FractalImage generate(int width, int height) {
        var image = new WritableImage(width, height);

        var start = System.currentTimeMillis();

        // Optimized array storage
        int[] pixels = new int[width * height];

        // Iterate over every pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Subtract from X and Y to center the image
                // todo: make this a config option and play with it more
                double realPartC = (x) - (Configuration.Image.ResolutionX / 2.0) * Cartesian.Coords.xScale;
                double imaginaryPartC = (y) - (Configuration.Image.ResolutionY / 2.0) * Cartesian.Coords.yScale;
                var properCoords = Cartesian.convert(realPartC, imaginaryPartC);
                var mandelResult = Fractal.isInMandelbrotSet(properCoords[0], properCoords[1]);

                // Convert to a normalized scale of 0-255
                int iterations = Convert.scaleIterationsToRgb(mandelResult.getIterations(), true);

                // Set the pixel
                pixels[x + y * width] = getColourByMode(
                        Configuration.Image.Mode,
                        mandelResult.isInMandelbrotSet(),
                        width, height, x, y, iterations);
            }
        }

        // Set the pixels all at once
        image.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbPreInstance(), pixels, 0, width);

        var end = System.currentTimeMillis();
        System.out.printf("Took %sms to generate image%n", end - start);

        return new FractalImage(image, end - start);
    }

    public static CompletableFuture<FractalImage> generateAsync(int width, int height) {
        return CompletableFuture.supplyAsync(() -> generate(width, height));
    }

    public static int getColourByMode(ImageGen.Mode mode, boolean isInSet, int width, int height, int x, int y, int iterations) {
        // * Future tycrek: this CANNOT be ternary operators, it's too messy
        if (isInSet)
            return switch (mode) {
                case HSL_REGULAR_2, HSL_INVERTED -> 0xFFFFFFFF;
                default -> 0xFF000000;
            };
        return switch (mode) {
            case HSL_REGULAR, HSL_INVERTED, HSL_INVERTED_2, HSL_REGULAR_2 ->
                    Convert.hex2argb(HSLGen.generateColor(x, y, iterations));
            case RGB_Tycrek_1 -> Convert.rgb2argb(255 - iterations * 5, 255 - iterations * 6, iterations * 7);
        };
    }

    /**
     * Image generation modes
     */
    public enum Mode {
        /**
         * HSL mode with WHITE outer layers, and a BLACK Mandelbrot set
         */
        HSL_REGULAR,

        /**
         * HSL mode with BLACK outer layers, and a WHITE Mandelbrot set
         */
        HSL_INVERTED,

        /**
         * HSL mode with BLACK outer layers, and a BLACK Mandelbrot set
         */
        HSL_INVERTED_2,

        /**
         * HSL mode with WHITE outer layers, and a WHITE Mandelbrot set
         */
        HSL_REGULAR_2,

        /**
         * RGB mode, my first attempt at a color scheme
         */
        RGB_Tycrek_1,
    }

    @Getter
    @RequiredArgsConstructor
    public static class FractalImage {
        private final Image image;
        private final long duration;
    }
}
