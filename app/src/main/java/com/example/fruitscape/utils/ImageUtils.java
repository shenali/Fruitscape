package com.example.fruitscape.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.example.fruitscape.Classifiers.Configuration;

public class ImageUtils {


//      Convert bitmap to the input dimensions

    public static Bitmap prepareImageForClassification(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(f);

        Bitmap bmp = Bitmap.createScaledBitmap(
                bitmap,
                Configuration.INPUT_WIDTH,
                Configuration.INPUT_HEIGHT,
                false);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return bmp;
    }
}
