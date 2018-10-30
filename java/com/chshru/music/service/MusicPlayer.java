package com.chshru.music.service;

import android.content.Context;
import android.media.MediaPlayer;

import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.HistoryTable;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.Song;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.util.List;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private HttpProxyCacheServer mCacheServer;
    private PlayService mService;
    private CacheListener mCacheListener;
    private StatusCallback mCallback;
    private Song mCurSong;
    private HistoryTable mHistoryTable;

    public MusicPlayer(Context context, StatusCallback callback) {
        mCacheServer = new HttpProxyCacheServer(
                context.getApplicationContext());
        mCallback = callback;
    }

    public void setCallback(StatusCallback callback) {
        mCallback = callback;
        mCallback.onSongChanged(mCurSong);
    }

    public void setHistoryTable(HistoryTable historyTable) {
        mHistoryTable = historyTable;
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
        if (mCurSong != null) {
            mCurSong.playing = false;
        }
        String local, url;
        if (song.type == Song.TYPE_NET) {
            url = QQMusicApi.buildSongUrl(song.mid);
            local = mCacheServer.getProxyUrl(url);
            mCurSong = new Song(song);
        } else if (song.link == null) {
            url = QQMusicApi.buildSongUrl(song.mid);
            local = mCacheServer.getProxyUrl(url);
            mCurSong = song;
        } else {
            mCurSong = song;
            url = mCurSong.link;
            local = url;
        }
        List<Song> history = ((MusicApp) mCallback.getApplication())
                .getListData().getList(ListData.P_HISTORY);
        boolean wasIn = false;
        for (Song s : history) {
            if (s.equals(mCurSong)) {
                wasIn = true;
                break;
            }
        }
        if (!wasIn) {
            mCurSong.type = Song.TYPE_LOCAL;
            history.add(mCurSong);
        }
        if (mCacheListener != null) {
            mCacheServer.registerCacheListener(mCacheListener, url);
        }
        mService.setPreparedListener(this);
        mService.prepare(local);
        if (mCallback != null) {
            mCallback.onSongChanged(mCurSong);
        }
    }


    @Override
    public void onPrepared(MediaPlayer player) {
        mService.serPrepared(true);
        player.start();
        mCallback.togglePlayer(false);
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
