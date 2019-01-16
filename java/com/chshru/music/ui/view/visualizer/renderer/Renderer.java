package com.chshru.music.ui.view.visualizer.renderer;

import android.graphics.Canvas;
import android.graphics.Rect;


abstract public class Renderer {

    protected float[] mPoints;
    protected float[] mFFTPoints;

    public Renderer() {
    }

    abstract public void onRenderAudio(Canvas canvas, byte[] bytes, Rect rect);
    abstract public void onRenderFFT(Canvas canvas, byte[] bytes, Rect rect);


    final public void renderAudio(Canvas canvas, byte[] bytes, Rect rect) {
        if (mPoints == null || mPoints.length < bytes.length * 4) {
            mPoints = new float[bytes.length * 4];
        }

        onRenderAudio(canvas, bytes, rect);
    }

    final public void renderFFT(Canvas canvas, byte[] bytes, Rect rect) {
        if (mFFTPoints == null || mFFTPoints.length < bytes.length * 4) {
            mFFTPoints = new float[bytes.length * 4];
        }

        onRenderFFT(canvas, bytes, rect);
    }
}
