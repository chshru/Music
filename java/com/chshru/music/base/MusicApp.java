package com.chshru.music.base;

import android.app.Application;
import android.content.Context;

import com.chshru.music.manager.DataManager;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.CacheTable;
import com.chshru.music.util.HistoryTable;
import com.chshru.music.util.LoveTable;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by abc on 18-10-22.
 */

public class MusicApp extends Application {

    private MusicPlayer mPlayer;
    private HistoryTable mHistoryTable;
    private boolean mHasInit;
    private ListData mListData;
    private LoveTable mLoveTable;
    private CacheTable mCacheTable;
    private HttpProxyCacheServer mCacheServer;
    private DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mHasInit = false;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mCacheServer.shutdown();
    }

    public void init() {
        Context context = getApplicationContext();
        mCacheServer = new HttpProxyCacheServer(context);
        mPlayer = new MusicPlayer(context, this);
        mHistoryTable = new HistoryTable(context);
        mLoveTable = new LoveTable(context);
        mCacheTable = new CacheTable(context);
        mPlayer.setHistoryTable(mHistoryTable);
        mListData = new ListData();
        mDataManager = new DataManager(context, mListData);
        mHasInit = true;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public HttpProxyCacheServer getServer() {
        return mCacheServer;
    }

    public LoveTable getLoveTable() {
        return mLoveTable;
    }

    public CacheTable getCacheTable() {
        return mCacheTable;
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
