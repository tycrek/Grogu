package dev.jmoore;

import dev.jmoore.grid.Window2Cartesian;
import lombok.SneakyThrows;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageGen {
    public static BufferedImage generate(int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var properCoords = Window2Cartesian.convert(x, y);
                var mandelResult = Fractal.isInMandelbrotSet(properCoords[0], properCoords[1]);

                int scaledValue = scaleIterationsToRgb(mandelResult.getIterations(), true); // convert to a scale of 0-255
                int colour = mandelResult.isInMandelbrotSet()
                        ? rgb2hex(0, 0, 0)
                        : rgb2hex(255 - scaledValue * 5, 255 - scaledValue * 6, scaledValue * 7);
                image.setRGB(x, y, colour);
            }
        }

        return image;
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
}