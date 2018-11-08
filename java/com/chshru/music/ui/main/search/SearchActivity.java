package com.chshru.music.ui.main.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.main.search.SearchResultAdapter.OnItemClickListener;
import com.chshru.music.ui.view.ActionSearchView;
import com.chshru.music.ui.view.ActionSearchView.OnTextChangeListener;
import com.chshru.music.ui.view.RotateLoading;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.QueryHandler;
import com.chshru.music.util.QueryHandler.OnFinishRunnable;
import com.chshru.music.util.Song;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by abc on 18-10-26.
 */

public class SearchActivity extends ActivityBase implements StatusCallback {

    private ActionSearchView mSearch;
    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private QueryHandler mHandler;
    private MusicApp app;
    private int mQueriedPos;
    private LinearLayoutManager mLayoutManager;
    private String mQueryString;
    private OnScrollListener mOnScrollListener;
    private RotateLoading mTopLoading;
    private RotateLoading mBottomLoading;

    @Override
    protected int getLayoutId() {
        overridePendingTransition(0, 0);
        return R.layout.activity_search;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void initialize() {
        mQueriedPos = 1;
        app = (MusicApp) getApplication();
        mSearch = findViewById(R.id.sv_search_aty);
        mSearch.setOnQueryTextListener(mSearchListener);
        mRecycler = findViewById(R.id.search_aty_recycler);
        List<Song> list = new ArrayList<>();
        mAdapter = new SearchResultAdapter(list, getMainLooper());
        mAdapter.setCacheServer(app.getServer());
        mAdapter.setOnItemClickListener(mItemClickListener);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setHasFixedSize(true);
        RecyclerView.RecycledViewPool pool = mRecycler.getRecycledViewPool();
        pool.setMaxRecycledViews(0, 60);
        for (int index = 0; index < 60; index++) {
            pool.putRecycledView(mAdapter.createViewHolder(mRecycler, 0));
        }

        mAdapter.notifyDataSetChanged();
        mHandler = new QueryHandler(getMainLooper(), mRunnable);
        mTopLoading = findViewById(R.id.search_loading);
        mBottomLoading = findViewById(R.id.more_loading);
        int color = getResources().getColor(R.color.colorPrimary);
        mTopLoading.setLoadingColor(color);
        mBottomLoading.setLoadingColor(color);
        findViewById(R.id.back).setOnClickListener(view -> {
            mSearch.clearFocus();
            finish();
        });
        mOnScrollListener = new OnScrollListener();
        mRecycler.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onSongChanged(Song song) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.get(i).playing = mAdapter.get(i).equals(song);
        }
        mAdapter.notifyDataDelayed(200);
    }

    @Override
    public void togglePlayer(boolean real) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        app.getPlayer().addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.getPlayer().rmCallback(this);
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {

        public int totalItemCount;
        public int previousTotal;
        public int visibleItemCount;
        public int firstVisibleItem;
        public boolean loading = true;

        public void init() {
            totalItemCount = 0;
            previousTotal = 0;
            visibleItemCount = 0;
            firstVisibleItem = 0;
            loading = true;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }

            if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem) {
                mQueriedPos++;
                loading = true;
                onQueryMore();
            }
        }

    }


    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v) {
            app.getListData().setPos(ListData.P_SEARCH);
            int pos = mRecycler.getChildAdapterPosition(v);
            app.getPlayer().prepare(mAdapter.get(pos));
        }
    };

    private OnFinishRunnable mRunnable = new OnFinishRunnable() {
        @Override
        public void run() {
            onQueryComplete(getResult());
        }
    };

    private void onQueryStart() {
        mQueriedPos = 1;
        mAdapter.clear();
        if (!mTopLoading.isStart()) {
            mTopLoading.start();
        }
        new Thread(() -> QQMusicApi.query(mQueriedPos, 60, mQueryString, mHandler)).start();
    }

    private void onQueryMore() {
        if (!mBottomLoading.isStart()) {
            mBottomLoading.start();
        }
        new Thread(() -> QQMusicApi.query(mQueriedPos, 60, mQueryString, mHandler)).start();
    }

    private void onQueryComplete(String result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        List<Song> list = QQMusicApi.getSongFromResult(result);
        for (Song song : list) {
            if (song.equals(app.getPlayer().getCurSong())) {
                song.playing = true;
                break;
            }
        }
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mTopLoading.isStart()) {
            mTopLoading.stop();
        }
        if (mBottomLoading.isStart()) {
            mBottomLoading.stop();
        }
    }

    private void onQuerySubmit(String str) {
        if (str.equals(mQueryString)) {
            return;
        }
        mQueryString = str;
        onQueryStart();
    }

    private OnTextChangeListener mSearchListener = new OnTextChangeListener() {

        @Override
        public boolean onQueryTextSubmit(String queryText) {
            mSearch.clearFocus();
            mAdapter.stopLoad();
            mOnScrollListener.init();
            onQuerySubmit(queryText);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (mAdapter.getItemCount() != 0) {
                mAdapter.stopLoad();
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
            }
            if (mTopLoading.isStart()) {
                mTopLoading.stop();
            }
            if (mBottomLoading.isStart()) {
                mBottomLoading.stop();
            }
            return true;
        }
    };
}
