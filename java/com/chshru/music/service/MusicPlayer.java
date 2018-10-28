package com.chshru.music.service;

import android.content.Context;

import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.Song;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer {

    private HttpProxyCacheServer mCacheServer;
    private PlayService mService;
    private CacheListener mCacheListener;
    private StatusCallback mCallback;
    private Song mCurSong;

    public MusicPlayer(Context context, StatusCallback callback) {
        mCacheServer = new HttpProxyCacheServer(
                context.getApplicationContext());
        mCallback = callback;
    }

    public void addCacheListener(CacheListener listener) {
        mCacheListener = listener;
    }

    public void setService(PlayService service) {
        mService = service;
    }

    public void removeService() {
        mService = null;
    }

    public void prepare(Song song) {
        if (mService == null) {
            return;
        }
        if (mCurSong != null && mCurSong.equals(song)) {
            return;
        }
        if (mCurSong != null && mCurSong.type == Song.TYPE_LOCAL) {
            mCurSong.playing = false;
        }
        String local, url;
        if (song.type == Song.TYPE_NET) {
            url = QQMusicApi.buildSongUrl(song.mid);
            local = mCacheServer.getProxyUrl(url);
            mCurSong = song;
        } else {
            mCurSong = new Song(song);
            url = song.link;
            local = url;
        }
        if (mCacheListener != null) {
            mCacheServer.registerCacheListener(mCacheListener, url);
        }
        mService.prepare(local);
        if (mCallback != null) {
            mCallback.onSongChanged(mCurSong);
        }
    }

    public Song getCurSong() {
        return mCurSong;
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

}
