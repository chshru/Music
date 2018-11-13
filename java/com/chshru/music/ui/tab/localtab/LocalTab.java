package com.chshru.music.ui.tab.localtab;

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

    public static final String STARY_TYPE = "start_type";
    public static final String STARY_TITLE = "start_title";

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
    public void freshChild() {
        mHandler.post(mFreshRunnable);
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.local_tab_title;
    }

    @Override
    public void initChild(View root) {
        initLocalList(root);
        initHistoryList(root);
        initMyLoveList(root);
        mHandler.postDelayed(mFreshRunnable, 2000);
    }

    private void initMyLoveList(View root) {
        View myLove = root.findViewById(R.id.list_my_love);
        app = (MusicApp) mCallback.getApplication();
        List<Song> loveList = app.getListData().getList(ListData.P_LOVE);
        mLoveList = new MyLoveList(mContext, myLove, loveList);
        mLoveList.freshCount();
        myLove.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_LOVE);
            intent.putExtra(STARY_TITLE, R.string.my_love);
            mContext.startActivity(intent);
        });
    }

    private void initHistoryList(View root) {
        View history = root.findViewById(R.id.list_history);
        app = (MusicApp) mCallback.getApplication();
        List<Song> historyList = app.getListData().getList(ListData.P_HISTORY);
        mHistory = new HistoryList(mContext, history, historyList);
        mHistory.freshCount();
        history.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_HISTORY);
            intent.putExtra(STARY_TITLE, R.string.history);
            mContext.startActivity(intent);
        });
    }

    private void initLocalList(View root) {
        View localSong = root.findViewById(R.id.list_local_song);
        app = (MusicApp) mCallback.getApplication();
        List<Song> localList = app.getListData().getList(ListData.P_LOCAL);
        mLocalSong = new LocalSongList(mContext, localSong, localList);
        mLocalSong.freshCount();
        localSong.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_LOCAL);
            intent.putExtra(STARY_TITLE, R.string.local_song);
            mContext.startActivity(intent);
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
