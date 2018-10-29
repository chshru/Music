package com.chshru.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.SoftReference;

/**
 * Created by abc on 18-10-22.
 */

public class MediaService extends Service {

    private MediaPlayer mPlayer;
    private boolean mHasPrepare;

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mHasPrepare = false;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    private boolean hasPrepare() {
        return mHasPrepare;
    }

    private void prepare(String url) {
        if (hasPrepare()) {
            mPlayer.reset();
        }
        try {
            mHasPrepare = false;
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() {
        mPlayer.start();
    }


    private void stop() {
        if (hasPrepare()) {
            mPlayer.stop();
        }
        mHasPrepare = false;
        mPlayer.release();
    }

    void play() {
        mPlayer.start();
    }

    void pause() {
        mPlayer.pause();
    }

    private boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void setPrepared(boolean prepared) {
        mHasPrepare = prepared;
    }

    private void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPlayer.setOnPreparedListener(listener);
    }

    private class PlayBinder extends Binder implements PlayService {

        private SoftReference<MediaService> mService;

        PlayBinder(MediaService service) {
            mService = new SoftReference<>(service);
        }

        @Override
        public void release() {
            mService.get().stop();
        }

        @Override
        public void play() {
            mService.get().play();
        }

        @Override
        public void pause() {
            mService.get().pause();
        }

        @Override
        public void start() {
            mService.get().start();
        }

        @Override
        public void prepare(String url) {
            mService.get().prepare(url);
        }

        @Override
        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        @Override
        public boolean hasPrepare() {
            return mService.get().hasPrepare();
        }

        @Override
        public void setPreparedListener(MediaPlayer.OnPreparedListener listener) {
            mService.get().setPreparedListener(listener);
        }

        @Override
        public void serPrepared(boolean b) {
            mService.get().setPrepared(b);
        }


    }


}
