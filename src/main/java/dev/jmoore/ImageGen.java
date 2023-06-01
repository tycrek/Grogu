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
            case Red_Scale_50_stops -> {
                // 50 shades of red, evenly distributed through the iterations
                int red = (int) (iterations * (255.0 / Configuration.Fractal.Iterations));
                yield Convert.rgb2argb(red, 0, 0);
            }
            case Rainbow -> {
                // 7 colours of the rainbow
                int colours = 7;
                int rb_red = Convert.rgb2argb(255, 0, 0);
                int rb_orange = Convert.rgb2argb(255, 127, 0);
                int rb_yellow = Convert.rgb2argb(255, 255, 0);
                int rb_green = Convert.rgb2argb(0, 255, 0);
                int rb_blue = Convert.rgb2argb(0, 0, 255);
                int rb_indigo = Convert.rgb2argb(75, 0, 130);
                int rb_violet = Convert.rgb2argb(143, 0, 255);

                int remainder = iterations % colours;

                yield switch (remainder) {
                    case 0 -> rb_red;
                    case 1 -> rb_orange;
                    case 2 -> rb_yellow;
                    case 3 -> rb_green;
                    case 4 -> rb_blue;
                    case 5 -> rb_indigo;
                    case 6 -> rb_violet;
                    default -> rb_red;
                };
            }
            case CottonCandy -> {
                int remainder = iterations % 10;
                yield switch (remainder) {
                    case 1 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 0.6f));
                    case 2 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 0.7f));
                    case 3 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 0.8f));
                    case 4 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 0.9f));
                    case 5 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 1.0f));
                    case 6 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 1.1f));
                    case 7 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 1.2f));
                    case 8 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 1.3f));
                    case 9 -> Convert.hex2argb(HSLGen.convertHslToRgb(0.5f, 0.5f, 1.4f));
                    default -> HSLGen.convertHslToRgb(0.5f, 0.5f, 0.5f);
                };
            }
            case CheshireCat -> {
                int remainder = iterations % 10;
                yield switch (remainder) {
                    case 1 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 10));
                    case 2 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 20));
                    case 3 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 30));
                    case 4 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 40));
                    case 5 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 50));
                    case 6 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 60));
                    case 7 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 70));
                    case 8 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 80));
                    case 9 -> Convert.hex2argb(HSLGen.convertHslToRgb(200, 1, 90));
                    default -> HSLGen.convertHslToRgb(200, 1, 100);
                };
            }
            case EarthTones -> {
                int remainder = iterations % 6;
                int et_grass = Convert.hex2argb(HSLGen.convertHslToRgb(131, 0.68f, 0.36f));
                int et_dirt = Convert.hex2argb(HSLGen.convertHslToRgb(30, 0.55f, 0.26f));
                int et_rock = Convert.hex2argb(HSLGen.convertHslToRgb(0, 0, 0.5f));
                int et_snow = Convert.hex2argb(HSLGen.convertHslToRgb(0, 0, 1));
                int et_sand = Convert.hex2argb(HSLGen.convertHslToRgb(60, 0.5f, 0.9f));
                int et_water = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.6f));
                yield switch (remainder) {
                    case 1 -> et_dirt;
                    case 2 -> et_rock;
                    case 3 -> et_snow;
                    case 4 -> et_sand;
                    case 5 -> et_water;
                    default -> et_grass;
                };
            }
            case SkyBlue -> {
                int remainder = iterations % 6;
                int sb_1 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.3f));
                int sb_2 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.4f));
                int sb_3 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.5f));
                int sb_4 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.6f));
                int sb_5 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.7f));
                int sb_6 = Convert.hex2argb(HSLGen.convertHslToRgb(195, 0.75f, 0.8f));
                yield switch (remainder) {
                    case 1 -> sb_1;
                    case 2 -> sb_2;
                    case 3 -> sb_3;
                    case 4 -> sb_4;
                    case 5 -> sb_5;
                    default -> sb_6;
                };
            }
            case Green3 -> {
                int remainder = iterations % 3;
                int g3_1 = Convert.hex2argb(HSLGen.convertHslToRgb(120, 0.75f, 0.3f));
                int g3_2 = Convert.hex2argb(HSLGen.convertHslToRgb(120, 0.75f, 0.5f));
                int g3_3 = Convert.hex2argb(HSLGen.convertHslToRgb(120, 0.75f, 0.7f));
                yield switch (remainder) {
                    case 1 -> g3_1;
                    case 2 -> g3_2;
                    default -> g3_3;
                };
            }
            case PinkGradient100Stops -> Convert.hex2argb(HSLGen.convertHslToRgb(300, 1, iterations / 100f));
            case PinkGradient500Stops -> Convert.hex2argb(HSLGen.convertHslToRgb(300, 1, iterations / 500f));
            case Hue360 -> Convert.hex2argb(HSLGen.convertHslToRgb(iterations % 360, 1, 0.5f));
            case OnlyWhite -> Convert.hex2argb(0xFFFFFF);
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

        Red_Scale_50_stops,

        Rainbow,

        CottonCandy,

        CheshireCat,

        EarthTones,

        SkyBlue,

        Green3,

        PinkGradient100Stops,
        PinkGradient500Stops,

        Hue360,

        OnlyWhite,
    }

    @Getter
    @RequiredArgsConstructor
    public static class FractalImage {
        private final Image image;
        private final long duration;
    }
}
