package com.chshru.music.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by abc on 18-10-25.
 */

public class SongQueryHandler {

    private OnFinishListener mListener;

    public SongQueryHandler(Looper looper, OnFinishListener listener) {
        mHandler = new Handler(looper);
        mListener = listener;
    }

    private Handler mHandler;

    public void exeComplete(String result) {
        mHandler.postDelayed(() -> mListener.onFinish(result), 500);
    }

    public interface OnFinishListener {
        void onFinish(String result);
    }
}
