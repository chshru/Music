package com.chshru.music.ui.main.list;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        mApp = (MusicApp) getApplication();
        String title = mApp.getListData().getTitle();
        int pos = mApp.getListData().getPos();
        List<Song> list = mApp.getListData().getList(pos);
        mAdapter = new SearchResultAdapter(list, getMainLooper());
        mRecycler = findViewById(R.id.list_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                int pos = mRecycler.getChildAdapterPosition(v);
                Song song = mAdapter.get(pos);
                mApp.getPlayer().prepare(song);
            }
        });
    }
}
