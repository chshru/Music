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

    public static final int START_BY_LOCAL = 1;
    public static final int START_BY_HISTORY = 2;
    public static final int START_BY_LOVE = 3;
    public static final String STARY_TYPE = "start_type";

    private LocalSongList mLocalSong;
    private HistoryList mHistory;
    private MyLoveList mLoveList;
    private Handler mHandler;
    private MusicApp app;
    private SongScanner scanner;

    public LocalTab(StatusCallback callback, int resId) {
        super(callback, resId);
        mHandler = new Handler(mCallback.getMainLooper());
    }

    @Override
    public void freshChild() {
        mHandler.post(mFreshRunnable);
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.local_tab_title;
    }

    @Override
    public void initChild(Context context, View root) {
        scanner = new SongScanner(context);
        scanner.setCallback(mHandler, mFreshRunnable);
        initLocalList(context, root);
        initHistoryList(context, root);
        initMyLoveList(context, root);
    }

    private void initMyLoveList(Context context, View root) {
        View myLove = root.findViewById(R.id.list_my_love);
        app = (MusicApp) mCallback.getApplication();
        List<Song> loveList = app.getListData().getList(ListData.P_LOVE);
        mLoveList = new MyLoveList(context, myLove, loveList);
        mLoveList.freshCount();
        if (loveList.size() == 0) {
            scanner.setLoveList(loveList);
            scanner.startLoveScan(app.getLoveTable());
        }
        myLove.setOnClickListener(view -> {
            app.getListData().setPos(ListData.P_LOVE);
            app.getListData().setTitle(mContext.getString(R.string.my_love));
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra(STARY_TYPE, START_BY_LOVE);
            context.startActivity(intent);
        });
    }

    private void initHistoryList(Context context, View root) {
        View history = root.findViewById(R.id.list_history);
        app = (MusicApp) mCallback.getApplication();
        List<Song> historyList = app.getListData().getList(ListData.P_HISTORY);
        mHistory = new HistoryList(context, history, historyList);
        mHistory.freshCount();
        if (historyList.size() == 0) {
            scanner.setHistoryList(historyList);
            scanner.startHistoryScan(app.getHistoryTable());
        }
        history.setOnClickListener(view -> {
            List<Song> listHis = app.getListData().getList(ListData.P_HISTORY);
            for (Song song : listHis) {
                if (song.playing) {
                    if (!song.equals(app.getPlayer().getCurSong())) {
                        song.playing = false;
                    }
                }
            }
            app.getListData().setPos(ListData.P_HISTORY);
            app.getListData().setTitle(mContext.getString(R.string.history));
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra(STARY_TYPE, START_BY_HISTORY);
            context.startActivity(intent);
        });
    }

    private void initLocalList(Context context, View root) {
        View localSong = root.findViewById(R.id.list_local_song);
        app = (MusicApp) mCallback.getApplication();
        List<Song> localList = app.getListData().getList(ListData.P_LOCAL);
        mLocalSong = new LocalSongList(context, localSong, localList);
        mLocalSong.freshCount();
        if (localList.size() == 0) {
            scanner.setLocalList(localList);
            scanner.startLocalScan();
        }
        localSong.setOnClickListener(view -> {
            app.getListData().setPos(ListData.P_LOCAL);
            app.getListData().setTitle(mContext.getString(R.string.local_song));
            Intent intent = new Intent(context, ListActivity.class);
            intent.putExtra(STARY_TYPE, START_BY_LOCAL);
            context.startActivity(intent);
        });
    }

    private Runnable mFreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLocalSong != null) {
                mLocalSong.freshCount();
            }
            if (mHistory != null) {
                mHistory.freshCount();
            }

            if (mLoveList != null) {
                mLoveList.freshCount();
            }
        }
    };
}
