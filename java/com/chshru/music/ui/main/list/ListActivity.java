package com.chshru.music.ui.main.list;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.search.SearchResultAdapter;
import com.chshru.music.util.Song;

import java.util.List;

public class ListActivity extends ActivityBase {

    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;

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
        MusicApp app = (MusicApp) getApplication();
        String title = app.getListData().getTitle();
        int pos = app.getListData().getPos();
        List<Song> list = app.getListData().getList(pos);
        mAdapter = new SearchResultAdapter(list, getMainLooper());
        mRecycler = findViewById(R.id.list_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }
}
