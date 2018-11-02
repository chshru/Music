package com.chshru.music.ui.main.list;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.search.SearchResultAdapter;
import com.chshru.music.ui.main.search.SearchResultAdapter.OnItemClickListener;
import com.chshru.music.ui.tab.localtab.LocalTab;
import com.chshru.music.util.Song;

import java.util.List;

public class ListActivity extends ActivityBase implements StatusCallback {

    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private MusicApp mApp;
    private int mCurPos;
    private int mStartType;

    @Override
    protected int getLayoutId() {
        overridePendingTransition(0, 0);
        return R.layout.activity_list;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    @Override
    protected void initialize() {
        mCurPos = -1;
        mApp = (MusicApp) getApplication();
        String title = mApp.getListData().getTitle();
        TextView titleTv = findViewById(R.id.list_title);
        titleTv.setText(title);
        int pos = mApp.getListData().getPos();
        List<Song> list = mApp.getListData().getList(pos);
        for (Song song : list) {
            if (song.equals(mApp.getPlayer().getCurSong())) {
                song.playing = true;
                mCurPos = list.indexOf(song);
            } else if (song.playing) {
                song.playing = false;
            }
        }
        mStartType = getIntent().getIntExtra(LocalTab.STARY_TYPE, -1);
        mAdapter = new SearchResultAdapter(list, getMainLooper());
        mRecycler = findViewById(R.id.list_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        mAdapter.setOnItemClickListener(mItemClick);
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

    @Override
    public void onSongChanged(Song song) {
        if (mAdapter == null) {
            return;
        }
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.get(i).playing = mAdapter.get(i).equals(song);
        }
        mAdapter.notifyDataDelayed(500);
    }

    @Override
    public void togglePlayer(boolean real) {

    }

    private OnItemClickListener mItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View v) {
            if (mStartType == LocalTab.START_BY_LOCAL) {
                int pos = mRecycler.getChildAdapterPosition(v);
                if (mCurPos != -1 && mCurPos < mAdapter.getItemCount()) {
                    mAdapter.get(mCurPos).playing = false;
                }
                mCurPos = pos;
                mAdapter.get(mCurPos).playing = true;
                Song song = mAdapter.get(mCurPos);
                mApp.getPlayer().prepare(song);
                mAdapter.notifyDataDelayed(500);
            } else if (mStartType == LocalTab.START_BY_HISTORY) {
                mAdapter.get(0).playing = false;
                int pos = mRecycler.getChildAdapterPosition(v);
                mAdapter.get(pos).playing = true;
                Song song = mAdapter.get(pos);
                mApp.getPlayer().prepare(song);
                mAdapter.notifyDataDelayed(500);
            } else if (mStartType == LocalTab.START_BY_LOVE) {
                int pos = mRecycler.getChildAdapterPosition(v);
                if (mCurPos != -1 && mCurPos < mAdapter.getItemCount()) {
                    mAdapter.get(mCurPos).playing = false;
                }
                mCurPos = pos;
                mAdapter.get(mCurPos).playing = true;
                Song song = mAdapter.get(mCurPos);
                mApp.getPlayer().prepare(song);
                mAdapter.notifyDataDelayed(500);
            }
        }
    };
}
