package dev.jmoore.color;

import dev.jmoore.Configuration;
import dev.jmoore.ImageGen;

/**
 * Thanks to ChatGPT for the HSL to RGB conversion code.
 * Comments and explanations have been added by me.
 */
public class HSLGen {

    /**
     * Generates a colour for the given x, y, and iterations.
     */
    public static int generateColor(int x, int y, int iterations) {
        float hue = calculateHue(x, y);
        float lightness = calculateLightness(iterations);
        return convertHslToRgb(hue, Configuration.Image.Saturation, lightness);
    }

    /**
     * Calculates the hue of the given x and y coordinates.
     */
    private static float calculateHue(int x, int y) {
        double angle = Math.atan2(y, x);
        float hue = (float) Math.toDegrees(angle);
        return hue < 0 ? hue + 360 : hue;
    }

    /**
     * Calculates the lightness of the given iterations.
     * <p>
     * By default, image will appear light-to-dark.
     * If inverted, image will appear dark-to-light.
     */
    private static float calculateLightness(int iterations) {
        return (Configuration.Image.Mode == ImageGen.Mode.HSL_INVERTED || Configuration.Image.Mode == ImageGen.Mode.HSL_INVERTED_2
                ? 255.0f - (float) iterations
                : (float) iterations)
                / 255.0f;
    }

    /**
     * Converts the given HSL values to an RGB integer.
     *
     * @param hue        The hue of the color, from 0.0f to 360.0f.
     * @param saturation The saturation of the color, from 0.0f to 1.0f.
     * @param lightness  The lightness of the color, from 0.0f to 1.0f.
     */
    public static int convertHslToRgb(float hue, float saturation, float lightness) {

        // Represents the chroma, which is the difference between the maximum and minimum values of a color channel.
        // In this case, it represents the amount of saturation in the color.
        float c = (1 - Math.abs(2 * lightness - 1)) * saturation;

        // Represents the intermediate value used in the conversion calculation.
        float x = c * (1 - Math.abs((hue / 60) % 2 - 1));

        // Represents the amount of lightness or darkness added to the color.
        float m = lightness - c / 2;

        // Final RGB is calculated using the values of c, x, and m and then combined to form the final RGB color.
        float r, g, b;

        // The hue value is used to determine which of the six possible cases the color falls into.
        if (hue < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (hue < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (hue < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (hue < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (hue < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        // The RGB values are then multiplied by 255 to convert them to the range 0-255.
        int red = (int) ((r + m) * 255);
        int green = (int) ((g + m) * 255);
        int blue = (int) ((b + m) * 255);

        return Convert.rgb2hex(red, green, blue);
    }
}
