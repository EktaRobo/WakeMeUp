package com.example.ekta.tryout5;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by ekta on 23/12/16.
 */

public class DeviceUtils {
    /**
     * Returns device width
     *
     * @param context : context
     * @return : device width in int
     */
    public static int getDeviceWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * Returns device height
     *
     * @param context : context
     * @return : device height in int
     */
    public static int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int convertPixelsToDp(Context ctx, int value) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (value * scale + 0.5f);
        return dpAsPixels;
    }

    public static int converDpToPixel(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

}
