package com.chshru.music.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by abc on 19-1-23.
 */

public class ListQueryHandler {

    private OnFinishListener mListener;

    public ListQueryHandler(Looper looper, OnFinishListener listener) {
        mHandler = new Handler(looper);
        mListener = listener;
    }

    private Handler mHandler;

    public void exeComplete(String result) {
        mHandler.post(() -> mListener.onFinish(result));
    }

    public interface OnFinishListener {
        void onFinish(String result);
    }
}
