package com.chshru.music.ui.view.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class LineRenderer extends Renderer {
    private Paint mPaint;
    private Paint mFlashPaint;
    private boolean mCycleColor;
    private float amplitude = 0;

    public LineRenderer(Paint paint,
                        Paint flashPaint,
                        boolean cycleColor) {
        super();
        mPaint = paint;
        mFlashPaint = flashPaint;
        mCycleColor = cycleColor;
    }

    public LineRenderer() {
        super();
        mPaint = new Paint();
        mPaint.setStrokeWidth(1f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(88, 0, 128, 255));

        mFlashPaint = new Paint();
        mFlashPaint.setStrokeWidth(5f);
        mFlashPaint.setAntiAlias(true);
        mFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        mCycleColor = true;
    }

    @Override
    public void onRenderAudio(Canvas canvas, byte[] bytes, Rect rect) {
        if (mCycleColor) {
            cycleColor();
        }

        for (int i = 0; i < bytes.length - 1; i++) {
            mPoints[i * 4] = rect.width() * i / (bytes.length - 1);
            mPoints[i * 4 + 1] = rect.height() / 2
                    + ((byte) (bytes[i] + 128)) * (rect.height() / 3) / 128;
            mPoints[i * 4 + 2] = rect.width() * (i + 1) / (bytes.length - 1);
            mPoints[i * 4 + 3] = rect.height() / 2
                    + ((byte) (bytes[i + 1] + 128)) * (rect.height() / 3) / 128;
        }

        float accumulator = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            accumulator += Math.abs(bytes[i]);
        }

        float amp = accumulator / (128 * bytes.length);
        if (amp > amplitude) {
            amplitude = amp;
            canvas.drawLines(mPoints, mFlashPaint);
        } else {
            amplitude *= 0.99;
            canvas.drawLines(mPoints, mPaint);
        }
    }

    @Override
    public void onRenderFFT(Canvas canvas, byte[] bytes, Rect rect) {

    }

    private float colorCounter = 0;

    private void cycleColor() {
        int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 3));
        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 1) + 1));
        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 7) + 1));
        mPaint.setColor(Color.argb(128, r, g, b));
        colorCounter += 0.03;
    }
}
