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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private HttpProxyCacheServer mCacheServer;
    private PlayService mService;
    private CacheListener mCacheListener;
    private Song mCurSong;
    private HistoryTable mHistoryTable;
    private List<StatusCallback> mCallbacks;
    private MusicApp mApp;
    private List<Song> mCurSongList;

    public MusicPlayer(Context context, MusicApp app) {
        mCacheServer = new HttpProxyCacheServer(
                context.getApplicationContext());
        mCallbacks = new ArrayList<>();
        mApp = app;
    }

    public void addCallback(StatusCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
            callback.onSongChanged(mCurSong);
        }
    }

    public void setHistoryTable(HistoryTable historyTable) {
        mHistoryTable = historyTable;
    }

    public void addCacheListener(CacheListener listener) {
        mCacheListener = listener;
    }

    public void rmCallback(StatusCallback callback) {
        mCallbacks.remove(callback);
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
        if (song.type == Song.TYPE_NET || song.link == null) {
            url = QQMusicApi.buildSongUrl(song.mid);
            local = mCacheServer.getProxyUrl(url);
        } else {
            url = song.link;
            local = url;
        }
        mService.setPreparedListener(this);
        mService.prepare(local);

        mCurSong = new Song(song);
        mCurSong.type = Song.TYPE_LOCAL;
        mCurSong.time = String.valueOf(System.currentTimeMillis());
        if (mApp.getListData().getPos() != ListData.P_HISTORY) {
            List<Song> history = mApp.getListData().getList(ListData.P_HISTORY);
            Song tempSong = null;
            for (Song s : history) {
                if (s.equals(mCurSong)) {
                    tempSong = s;
                    history.remove(s);
                    break;
                }
            }
            if (tempSong != null) {
                tempSong.copyFrom(mCurSong);
                mHistoryTable.update(mCurSong);
            } else {
                tempSong = new Song(mCurSong);
                mHistoryTable.insert(mCurSong);
            }
            history.add(0, tempSong);
        }

        if (mCacheListener != null) {
            mCacheServer.registerCacheListener(mCacheListener, url);
        }

        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onSongChanged(mCurSong);
            }
        }

    }

    public boolean hasService() {
        return mService != null;
    }

    private MediaPlayer.OnCompletionListener mOnComplete = mp -> toggleNextSong();

    private void toggleNextSong() {
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
        }
        int listPos = mApp.getListData().getPos();
        mCurSongList = mApp.getListData().getList(listPos);
        int songPos = -1;
        for (int i = 0; i < mCurSongList.size(); i++) {
            if (mCurSongList.get(i).equals(mCurSong)) {
                songPos = i;
                break;
            }
        }
        if (songPos != -1) {
            songPos = (songPos + 1) % mCurSongList.size();

        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mService.serPrepared(true);
        player.start();
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
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

    public void togglePause() {
        if (!hasPrepare()) {
            return;
        }
        boolean playing = isPlaying();
        if (playing) pause();
        else start();
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
        }
    }
}
