package com.chshru.music.base;

import android.app.Application;
import android.content.Context;

import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.HistoryTable;

/**
 * Created by abc on 18-10-22.
 */

public class MusicApp extends Application {

    private MusicPlayer mPlayer;
    private HistoryTable mHistoryTable;
    private boolean mHasInit;
    private ListData mListData;

    @Override
    public void onCreate() {
        super.onCreate();
        mHasInit = false;
    }

    public void init(StatusCallback callback) {
        Context context = getApplicationContext();
        mPlayer = new MusicPlayer(context, callback);
        mHistoryTable = new HistoryTable(context);
        mPlayer.setHistoryTable(mHistoryTable);
        mListData = new ListData();
        mHasInit = true;
    }

    public boolean hasInitialized() {
        return mHasInit;
    }

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    public HistoryTable getHistoryTable() {
        return mHistoryTable;
    }

    public ListData getListData() {
        return mListData;
    }
}
