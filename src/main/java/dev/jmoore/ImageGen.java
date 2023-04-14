package dev.jmoore;

import dev.jmoore.grid.W2CCoords;
import dev.jmoore.grid.Window2Cartesian;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public class ImageGen {
    public static FractalImage generate(int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        var start = System.currentTimeMillis();

        // Optimized array storage
        int[] pixels = new int[width * height];

        // Iterate over every pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // Subtract from X and Y to center the image
                // todo: make this a config option and play with it more
                double realPartC = (x) - (GenConfig.Image.ResolutionX / 2.0) * W2CCoords.xScale;
                double imaginaryPartC = (y) - (GenConfig.Image.ResolutionY / 2.0) * W2CCoords.yScale;
                var properCoords = Window2Cartesian.convert(realPartC, imaginaryPartC);
                var mandelResult = Fractal.isInMandelbrotSet(properCoords[0], properCoords[1]);

                // Convert to a normalized scale of 0-255
                int iterations = scaleIterationsToRgb(mandelResult.getIterations(), true);

                pixels[x + y * width] = mandelResult.isInMandelbrotSet()
                        //? rgb2hex(0, 0, 0)
                        ? 0xFFFFFF
                        //: HSLGen.generateColor(x, y, iterations);
                        : rgb2hex(255 - iterations * 5, 255 - iterations * 6, iterations * 7);
            }
        }

        // Set the pixels all at once (significantly faster than setRGB within the loop)
        image.setRGB(0, 0, width, height, pixels, 0, width);

        var end = System.currentTimeMillis();
        System.out.printf("Took %sms to generate image%n", end - start);

        return new FractalImage(image, end - start);
    }

    public static CompletableFuture<FractalImage> generateAsync(int width, int height) {
        return CompletableFuture.supplyAsync(() -> generate(width, height));
    }

    public static int rgb2hex(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static int scaleIterationsToRgb(int iterations) {
        return scaleIterationsToRgb(iterations, false);
    }

    public static int scaleIterationsToRgb(int iterations, boolean invert) {
        iterations = invert ? GenConfig.Fractal.Iterations - iterations : iterations;
        return (int) (((double) iterations / GenConfig.Fractal.Iterations) * 255.0);
    }

    @SneakyThrows
    public static InputStream toInputStream(BufferedImage image) {
        val os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
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
        private final BufferedImage image;
        private final long duration;
    }
}
