package com.chshru.music.ui.view.visualizer.multiple.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class BarGraphRenderer extends Renderer {
    private int mLines = 32;
    private Paint mPaint;


    public BarGraphRenderer() {
        super();
        mPaint = new Paint();
        mPaint.setStrokeWidth(12f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 181, 111, 233));
    }

    @Override
    public void onRenderAudio(Canvas canvas, byte[] bytes, Rect rect) {

    }

    @Override
    public void onRenderFFT(Canvas canvas, byte[] bytes, Rect rect) {

        byte[] model = new byte[bytes.length / 2 + 1];

        model[0] = (byte) Math.abs(bytes[0]);
        for (int i = 2, j = 1; j < mLines; i += 2, j++) {
            model[j] = (byte) Math.hypot(bytes[i], bytes[i + 1]);
        }

        mPaint.setStrokeWidth((rect.width() - 70) / mLines);
        final int baseX = rect.width() / mLines;
        final int height = rect.height() * 2 / 3;

        for (int i = 0; i < mLines; i++) {
            if (model[i] < 0) {
                model[i] = 127;
            }

            final int xi = baseX * i + baseX / 2;

            mFFTPoints[i * 4] = xi;
            mFFTPoints[i * 4 + 1] = height;

            mFFTPoints[i * 4 + 2] = xi;
            mFFTPoints[i * 4 + 3] = height - model[i];
        }
        canvas.drawLines(mFFTPoints, mPaint);
    }
}
