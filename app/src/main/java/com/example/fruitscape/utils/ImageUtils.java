package com.example.fruitscape.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.example.fruitscape.ml.ModelConfig;

public class ImageUtils {

    /**
     * Make bitmap appropriate of appropriate dimensions.
     */
    public static Bitmap prepareImageForClassification(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        //colorMatrix.setSaturation(0);
        //colorMatrix.postConcat(BLACK_WHITE);
        //colorMatrix.postConcat(INVERT);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(f);

        Bitmap bmp = Bitmap.createScaledBitmap(
                bitmap,
                ModelConfig.INPUT_IMG_SIZE_WIDTH,
                ModelConfig.INPUT_IMG_SIZE_HEIGHT,
                false);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return bmp;
    }
}
