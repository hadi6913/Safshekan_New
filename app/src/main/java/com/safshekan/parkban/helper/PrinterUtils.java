package com.safshekan.parkban.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class PrinterUtils {

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);
        return bitmap;
    }
}
