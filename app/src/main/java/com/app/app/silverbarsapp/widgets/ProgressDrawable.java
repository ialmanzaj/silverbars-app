package com.app.app.silverbarsapp.widgets;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.widget.SeekBar;

/**
 * Created by isaacalmanza on 05/18/17.
 */

public class ProgressDrawable extends Drawable {

    private static final String TAG = "CustomDrawable";
    private final SeekBar mySlider;
    private final Drawable myBase;
    private final int myDots;
    private Paint unSelectLinePaint;

    public ProgressDrawable(Drawable base, SeekBar slider, int dots, int color) {
        mySlider = slider;
        myBase = base;
        myDots = dots;

        unSelectLinePaint = new Paint();
        unSelectLinePaint.setColor(color);
        unSelectLinePaint.setStrokeWidth(toPix(10));
    }

    @Override
    public final void draw(@Nullable Canvas canvas) {
        float height = toPix(30) / 2;
        float width = getBounds().width();
        float radius = toPix((int) height / myDots);
        assert canvas != null;
        canvas.drawLine(0, height, width, height, unSelectLinePaint);
        float mX = 0;
        canvas.drawCircle(mX, height, radius, unSelectLinePaint);
        for (int i = 0; i < myDots; i++) {
            mX = mX + width / 3;
            canvas.drawCircle(mX, height, radius, unSelectLinePaint);
        }
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mySlider.getContext().getResources().getDisplayMetrics());
    }

    @Override
    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }
}
