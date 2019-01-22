package com.chshru.music.ui.view.visualizer.multiple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;

import com.chshru.music.ui.view.visualizer.VisualizerView;
import com.chshru.music.ui.view.visualizer.multiple.renderer.Renderer;

import java.util.HashSet;
import java.util.Set;


public class MultipleVisualizerView extends VisualizerView {

    private byte[] mBytes;
    private byte[] mFFTBytes;
    private Rect mRect;
    private Visualizer mVisualizer;
    private Set<Renderer> mRenderers;
    private Paint mFlashPaint;
    private Paint mFadePaint;
    private boolean mHasRelease;

    public MultipleVisualizerView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        mBytes = null;
        mFFTBytes = null;
        mFlashPaint = new Paint();
        mFadePaint = new Paint();
        mRect = new Rect();
        mFlashPaint.setColor(Color.argb(127, 255, 255, 255));
        mFadePaint.setColor(Color.argb(150, 255, 255, 255));
        mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
        mRenderers = new HashSet<>();
        mMatrix = new Matrix();
        mHasRelease = true;
    }

    public boolean hasRenders() {
        return !mRenderers.isEmpty();
    }

    @Override
    public void create(int session) {
        mVisualizer = new Visualizer(session);
        mHasRelease = false;
        mVisualizer.setCaptureSize(256);
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                updateVisualizer(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
                updateVisualizerFFT(bytes);
            }
        };

        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
        mVisualizer.setEnabled(true);
    }

    public void addRenderer(Renderer renderer) {
        if (mVisualizer != null && !mHasRelease) {
            if (!mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(true);
            }
        }
        if (renderer != null) {
            mRenderers.add(renderer);
        }
    }

    public void clearRenderers() {
        if (mVisualizer != null && !mHasRelease) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }
        }
        mRenderers.clear();
    }

    @Override
    public void release() {
        mVisualizer.release();
        mHasRelease = true;
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    public void updateVisualizerFFT(byte[] bytes) {
        mFFTBytes = bytes;
        invalidate();
    }

    boolean mFlash = false;

    public void flash() {
        mFlash = true;
        invalidate();
    }

    Bitmap mCanvasBitmap;
    Canvas mCanvas;
    Matrix mMatrix;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRect.set(0, 0, getWidth(), getHeight());

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
        }

        if (mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if (mBytes != null) {
            for (Renderer r : mRenderers) {
                r.renderAudio(mCanvas, mBytes, mRect);
            }
        }

        if (mFFTBytes != null) {
            for (Renderer r : mRenderers) {
                r.renderFFT(mCanvas, mFFTBytes, mRect);
            }
        }
        mCanvas.drawPaint(mFadePaint);

        if (mFlash) {
            mFlash = false;
            mCanvas.drawPaint(mFlashPaint);
        }

        canvas.drawBitmap(mCanvasBitmap, mMatrix, null);
    }
}