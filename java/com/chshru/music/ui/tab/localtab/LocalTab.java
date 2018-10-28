package com.chshru.music.ui.tab.localtab;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.os.Handler;

import com.chshru.music.R;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListActivity;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.util.Song;
import com.chshru.music.util.SongScanner;

import java.util.List;


/**
 * Created by abc on 18-10-25.
 */

public class LocalTab extends BaseTab {

    private LocalSongList mLocalSong;
    private HistoryList mHistory;
    private MyLoveList mLoveList;
    private Handler mHandler;
    private MusicApp app;

    public LocalTab(StatusCallback callback, int resId) {
        super(callback, resId);
        mHandler = new Handler(mCallback.getMainLooper());
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.local_tab_title;
    }

    @Override
    public void initChild(Context context, View root) {
        initLocalList(context, root);
        initHistoryList(context, root);
        initMyLoveList(context, root);
    }

    private void initMyLoveList(Context context, View root) {
        View myLove = root.findViewById(R.id.list_my_love);
        app = (MusicApp) mCallback.getApplication();
        List<Song> list = app.getListData().getList(ListData.P_LOVE);
        mLoveList = new MyLoveList(mContext, myLove, list);
        myLove.setOnClickListener(view -> {
            app.getListData().setPos(ListData.P_LOVE);
            app.getListData().setTitle(mContext.getString(R.string.my_love));
            Intent intent = new Intent(mContext, ListActivity.class);
            mContext.startActivity(intent);
        });
    }

    private void initHistoryList(Context context, View root) {
        View history = root.findViewById(R.id.list_history);
        app = (MusicApp) mCallback.getApplication();
        List<Song> list = app.getListData().getList(ListData.P_HISTORY);
        //SongScanner scanner = new SongScanner(mContext, list);
        //scanner.setCallback(mHandler, mFreshRunnable);
        //scanner.startScan();
        mHistory = new HistoryList(mContext, history, list);
        history.setOnClickListener(view -> {
            app.getListData().setPos(ListData.P_HISTORY);
            app.getListData().setTitle(mContext.getString(R.string.history));
            Intent intent = new Intent(mContext, ListActivity.class);
            mContext.startActivity(intent);
        });
    }

    private void initLocalList(Context context, View root) {
        View localSong = root.findViewById(R.id.list_local_song);
        app = (MusicApp) mCallback.getApplication();
        List<Song> list = app.getListData().getList(ListData.P_LOCAL);
        SongScanner scanner = new SongScanner(mContext, list);
        scanner.setCallback(mHandler, mFreshRunnable);
        scanner.startScan();
        mLocalSong = new LocalSongList(mContext, localSong, list);
        localSong.setOnClickListener(view -> {
            app.getListData().setPos(ListData.P_LOCAL);
            app.getListData().setTitle(mContext.getString(R.string.local_song));
            Intent intent = new Intent(mContext, ListActivity.class);
            mContext.startActivity(intent);
        });
    }

    private Runnable mFreshRunnable = new Runnable() {
        @Override
        public void run() {
            mLocalSong.freshCount();
        }
    };
}
