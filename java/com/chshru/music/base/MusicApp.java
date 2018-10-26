package com.chshru.music.base;

import android.app.Application;
import android.content.Context;

import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.util.SongTable;

/**
 * Created by abc on 18-10-22.
 */

public class MusicApp extends Application {

    private MusicPlayer mPlayer;
    private SongTable mSongTable;
    private boolean mHasInit;

    @Override
    public void onCreate() {
        super.onCreate();
        mHasInit = false;
    }

    public void init(StatusCallback callback) {
        Context context = getApplicationContext();
        mPlayer = new MusicPlayer(context, callback);
        mSongTable = new SongTable(context);
        mHasInit = true;
    }

    public boolean hasInitlized() {
        return mHasInit;
    }

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    public SongTable getSongTable() {
        return mSongTable;
    }
}
