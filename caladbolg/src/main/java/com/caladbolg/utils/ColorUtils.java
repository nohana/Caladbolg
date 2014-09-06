package com.caladbolg.utils;

import android.graphics.Color;

public class ColorUtils {
    public static String colorCode(int rgb, int alpha) {
        return Integer.toHexString(argb(rgb, alpha));
    }

    public static String colorCode(int argb) {
        return Integer.toHexString(argb);
    }

    public static int rgb(int argb) {
        return argb & 0x00ffffff;
    }

    public static int argb(String hexColorCode) {
        if (hexColorCode.startsWith("#")) hexColorCode = hexColorCode.substring(1);
        return (int) Long.parseLong(hexColorCode, 16);
    }

    public static int argb(int rgb, int alpha) {
        return Color.argb(alpha, Color.red(rgb), Color.green(rgb), Color.blue(rgb));
    }
}
