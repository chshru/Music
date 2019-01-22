package com.chshru.music.ui.view.visualizer.simple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;

import com.chshru.music.R;
import com.chshru.music.ui.view.visualizer.VisualizerView;
import com.chshru.music.ui.view.visualizer.multiple.renderer.Renderer;

/**
 * Created by abc on 19-1-15.
 */

public class SimpleVisualizerView extends VisualizerView {

    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();
    private int mSpectrumNum = 48;

    private Visualizer mVisualizer;
    private boolean mHasRelease;

    public SimpleVisualizerView(Context context) {
        super(context);
        mHasRelease = true;
        initialize();
    }

    @Override
    public void addRenderer(Renderer renderer) {
        if (mVisualizer != null && !mHasRelease) {
            if (!mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(true);
            }
        }
    }

    @Override
    public void clearRenderers() {
        if (mVisualizer != null && !mHasRelease) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }
        }
    }

    @Override
    public boolean hasRenders() {
        return mVisualizer.getEnabled();
    }

    private void initialize() {
        mBytes = null;
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(getContext().getResources().getColor(R.color.gray));
    }

    @Override
    public void release() {
        if (mVisualizer != null) {
            mVisualizer.release();
            mHasRelease = true;
        }
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
                updateVisualizer(bytes);
            }
        };

        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, false, true);
        mVisualizer.setEnabled(true);
    }


    public void updateVisualizer(byte[] fft) {

        byte[] model = new byte[fft.length / 2 + 1];

        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mSpectrumNum; i += 2, j++) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
        }
        mBytes = model;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        mForePaint.setStrokeWidth((getWidth() - 70) / mSpectrumNum);
        mRect.set(0, 0, getWidth(), getHeight());

        final int baseX = mRect.width() / mSpectrumNum;
        final int height = mRect.height() * 2 / 3;

        for (int i = 0; i < mSpectrumNum; i++) {
            if (mBytes[i] < 0) {
                mBytes[i] = 127;
            }

            final int xi = baseX * i + baseX / 2;

            mPoints[i * 4] = xi;
            mPoints[i * 4 + 1] = height;

            mPoints[i * 4 + 2] = xi;
            mPoints[i * 4 + 3] = height - mBytes[i];
        }

        canvas.drawLines(mPoints, mForePaint);
    }
}
