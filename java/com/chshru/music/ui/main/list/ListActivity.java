package com.chshru.music.ui.main.list;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.search.SearchResultAdapter;
import com.chshru.music.ui.main.search.SearchResultAdapter.OnItemClickListener;
import com.chshru.music.util.Song;

import java.util.List;

public class ListActivity extends ActivityBase {

    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private MusicApp mApp;
    private int mCurPos;

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
                break;
            }
        }
        mAdapter = new SearchResultAdapter(list, getMainLooper());
        mRecycler = findViewById(R.id.list_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                int pos = mRecycler.getChildAdapterPosition(v);
                if (mCurPos != -1 && mCurPos < mAdapter.getItemCount()) {
                    mAdapter.get(mCurPos).playing = false;
                    mAdapter.notifyItemChanged(mCurPos);
                }
                mCurPos = pos;
                mAdapter.get(mCurPos).playing = true;
                mAdapter.notifyItemChanged(mCurPos);
                Song song = mAdapter.get(mCurPos);
                mApp.getPlayer().prepare(song);
            }
        });
    }
}
