package com.chshru.music.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abc on 18-10-25.
 */


public class AlbumImage extends AppCompatImageView {

    private final int GET_DATA_SUCCESS = 1;
    private final int NETWORK_ERROR = 2;
    private final int SERVER_ERROR = 3;
    private LoadThread mThread;

    public void setImageUrl(String path) {
        mThread = new LoadThread(path);
        mThread.start();

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR:
                    break;
                case SERVER_ERROR:
                    break;
            }
            if (mThread != null && (mThread.isAlive() || !mThread.isInterrupted())) {
                mThread.interrupt();
                mThread = null;
            }
        }
    };


    private class LoadThread extends Thread {
        private String mPath;

        public LoadThread(String path) {
            super();
            mPath = path;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(mPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Message msg = Message.obtain();
                    msg.obj = bitmap;
                    msg.what = GET_DATA_SUCCESS;
                    mHandler.sendMessage(msg);
                    inputStream.close();
                } else {
                    mHandler.sendEmptyMessage(SERVER_ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(NETWORK_ERROR);
            }
        }
    }

    public AlbumImage(Context context) {
        super(context);
    }

    public AlbumImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
