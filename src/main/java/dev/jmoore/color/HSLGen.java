package dev.jmoore.color;

import dev.jmoore.GenConfig;
import dev.jmoore.ImageGen;

/**
 * Thanks to ChatGPT for the HSL to RGB conversion code.
 */
public class HSLGen {

    public static int generateColor(int x, int y, int scaledValue) {
        float hue = calculateHue(x, y);
        float lightness = calculateLightness(scaledValue);
        return convertHslToRgb(hue, GenConfig.Image.Saturation, lightness);
    }

    private static float calculateHue(int x, int y) {
        double angle = Math.atan2(y, x);
        float hue = (float) Math.toDegrees(angle);
        if (hue < 0) {
            hue += 360;
        }
        return hue;
    }

    private static float calculateLightness(int scaledValue) {
        // ! The 255 - part inverts the value
        // ---------------       ------------------------------
        float lightness = (255 - (float) scaledValue) / 255.0f;
        return lightness;
    }

    private static int convertHslToRgb(float hue, float saturation, float lightness) {

        // Represents the chroma, which is the difference between the maximum and minimum values of a color channel.
        // In this case, it represents the amount of saturation in the color.
        float c = (1 - Math.abs(2 * lightness - 1)) * saturation;

        // Represents the intermediate value used in the conversion calculation.
        float x = c * (1 - Math.abs((hue / 60) % 2 - 1));

        // Represents the amount of lightness or darkness added to the color.
        float m = lightness - c / 2;

        // Calculated using the values of c, x, and m and then combined to form the final RGB color.
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

        int red = (int) ((r + m) * 255);
        int green = (int) ((g + m) * 255);
        int blue = (int) ((b + m) * 255);

        return ImageGen.rgb2hex(red, green, blue);
    }
}
