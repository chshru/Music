package com.chshru.music.base;

import android.app.Application;
import android.content.Context;

import com.chshru.music.data.manager.PrefHelper;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.data.sql.DataHelper;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by abc on 18-10-22.
 */

public class MusicApp extends Application {

    private MusicPlayer mPlayer;
    private DataHelper mHelper;
    private boolean mHasInit;
    private ListData mListData;
    private HttpProxyCacheServer mCacheServer;
    private PrefHelper mPrefHelper;

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
        mHelper = new DataHelper(context);
        mListData = new ListData();
        mPrefHelper = new PrefHelper(context, mListData);
        mHasInit = true;
    }

    public DataHelper getHelper() {
        return mHelper;
    }

    public PrefHelper getPrefHelper() {
        return mPrefHelper;
    }

    public HttpProxyCacheServer getServer() {
        return mCacheServer;
    }

    public boolean hasInitialized() {
        return mHasInit;
    }

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    public ListData getListData() {
        return mListData;
    }
}
