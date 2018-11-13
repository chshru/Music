package com.chshru.music.ui.main.list;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.tab.localtab.LocalTab;
import com.chshru.music.util.BaseTable;
import com.chshru.music.util.Song;
import com.chshru.music.ui.main.list.DelAdapter.OnItemClickListener;

import java.util.List;

public class DelActivity extends ActivityBase implements StatusCallback {

    private RecyclerView mRecycler;
    private DelAdapter mAdapter;
    private MusicApp mApp;
    private int mStartType;
    private List<Song> mSong;
    private Handler mHandler;
    private BaseTable mTable;

    @Override
    protected int getLayoutId() {
        overridePendingTransition(0, 0);
        return R.layout.activity_remove;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    @Override
    protected void initialize() {
        mHandler = new Handler();
        mApp = (MusicApp) getApplication();
        mStartType = getIntent().getIntExtra(LocalTab.STARY_TYPE, -1);
        if (mStartType == -1) {
            return;
        } else if (mStartType == ListData.P_HISTORY) {
            mTable = mApp.getHistoryTable();
        } else if (mStartType == ListData.P_LOVE) {
            mTable = mApp.getLoveTable();
        }

        mSong = mApp.getListData().getList(mStartType);
        Song curSong = mApp.getPlayer().getCurSong();
        for (Song song : mSong) {
            song.playing = song.equals(curSong);
        }
        mAdapter = new DelAdapter(mSong);
        mRecycler = findViewById(R.id.del_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mRecycler.setHasFixedSize(true);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> onDeleteClick());
        mAdapter.setOnItemClickListener(mItemClick);
    }

    private void onDeleteClick() {
        List<Song> del = mAdapter.getSelected();
        for (Song song : del) {
            if (!song.playing) {
                continue;
            }
            for (Song s : mSong) {
                if (song.equals(s)) {
                    mSong.remove(s);
                    if (mTable != null) {
                        mTable.delete(s);
                    }
                    break;
                }
            }
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApp.getPlayer().addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApp.getPlayer().rmCallback(this);
    }

    private Runnable mFreshRun = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onSongChanged(Song song) {
        List<Song> del = mAdapter.getSelected();
        for (Song s : del) {
            if (s.equals(song)) {
                s.playing = false;
                break;
            }
        }
        for (Song s : mSong) {
            s.playing = s.equals(song);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void togglePlayer(boolean real) {

    }

    private OnItemClickListener mItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View v) {
            int pos = mRecycler.getChildAdapterPosition(v);
            if (mSong.get(pos).playing) return;
            mAdapter.getSelected().get(pos).playing = !mAdapter.isCheck(pos);
            mHandler.postDelayed(mFreshRun, 300);
        }
    };

}
