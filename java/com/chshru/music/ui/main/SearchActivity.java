package com.chshru.music.ui.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.ui.tab.searchtab.SearchResultAdapter;
import com.chshru.music.ui.view.ActionSearchView;
import com.chshru.music.ui.view.ActionSearchView.OnTextChangeListener;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.QueryHandler;
import com.chshru.music.util.QueryHandler.OnFinishRunnable;
import com.chshru.music.util.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-26.
 */

public class SearchActivity extends ActivityBase {

    private ActionSearchView mSearch;
    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private List<Song> mSong;
    private QueryHandler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initialize() {
        mSong = new ArrayList<>();
        mSearch = findViewById(R.id.sv_saerch_aty);
        mSearch.setOnQueryTextListener(mSearchListener);
        mRecycler = findViewById(R.id.search_aty_recycler);
        mAdapter = new SearchResultAdapter(mSong);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mHandler = new QueryHandler(getMainLooper(), mRunnable);
    }

    private OnFinishRunnable mRunnable = new OnFinishRunnable() {

        @Override
        public void run() {
            queryComplete(getResult());
        }
    };

    private void queryComplete(String result) {
        System.err.println(result);
        mSong.clear();
        mSong.addAll(QQMusicApi.getSongFromResult(result));
        mAdapter.notifyDataSetChanged();
    }

    private void onQuerySubmit(String str) {
        new Thread(() -> QQMusicApi.query(1, 20, str, mHandler)).start();
    }


    private OnTextChangeListener mSearchListener = new OnTextChangeListener() {

        @Override
        public boolean onQueryTextSubmit(String queryText) {
            mSearch.clearFocus();
            onQuerySubmit(queryText);
            return true;
        }
    };
}
