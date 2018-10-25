package com.chshru.music.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by abc on 18-10-25.
 */

public class QueryHandler {

    private ExeRunnable mRunnable;

    public QueryHandler(Looper looper, ExeRunnable runnable) {
        mHandler = new Handler(looper);
        mRunnable = runnable;
    }

    private Handler mHandler;

    public void exeComplete(String result) {
        mRunnable.setResult(result);
        mHandler.post(mRunnable);
    }


    public static abstract class ExeRunnable implements Runnable {

        private String mResult;

        public void setResult(String result) {
            mResult = result;
        }

        public String getResult() {
            return mResult;
        }
    }

}
