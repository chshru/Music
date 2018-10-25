package com.chshru.music.service;

import android.content.Context;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer {

    private HttpProxyCacheServer mCacheServer;
    private PlayService mService;
    private CacheListener mCacheListener;
    private StatusCallback mCallback;

    public MusicPlayer(Context context,StatusCallback callback) {
        mCacheServer = new HttpProxyCacheServer(
                context.getApplicationContext());
        mCallback = callback;
    }

    public void addCacheListener(CacheListener listener) {
        mCacheListener = listener;
    }

    public void addPlayService(PlayService service) {
        mService = service;
    }

    public void delPlayService() {
        mService = null;
    }

    public void prepare(String url) {
        if (mService == null) {
            return;
        }
        String local = url;
        if (isHttpUrl(url) && !mCacheServer.isCached(url)) {
            local = mCacheServer.getProxyUrl(url);
            if(mCacheListener != null) {
                mCacheServer.registerCacheListener(mCacheListener, url);
            }
        }
        mService.prepare(local);
    }

    public void start() {
        if (mService == null) {
            return;
        }
        mService.start();
    }

    public void release() {
        if (mService == null) {
            return;
        }
        mService.release();
    }

    public boolean isPlaying() {
        return mService != null && mService.isPlaying();
    }

    public void pause() {
        if (mService == null) {
            return;
        }
        mService.pause();
    }

    public boolean hasPrepare() {
        return mService != null && mService.hasPrepare();
    }

    private boolean isHttpUrl(String url) {
        if (url.contains("http")) {
            return true;
        }
        return false;
    }

}
