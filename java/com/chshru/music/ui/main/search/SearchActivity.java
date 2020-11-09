package com.chshru.music.ui.main.search;


import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.main.listener.OnItemClickListener;
import com.chshru.music.ui.view.ActionSearchView;
import com.chshru.music.ui.view.ActionSearchView.OnTextChangeListener;
import com.chshru.music.ui.view.RotateLoading;
import com.chshru.music.api.QQMusicApi;
import com.chshru.music.util.SongQueryHandler;
import com.chshru.music.data.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-26.
 */

public class SearchActivity extends ActivityBase implements StatusCallback {

    private ActionSearchView mSearch;
    private RecyclerView mRecycler;
    private SearchAdapter mAdapter;
    private SongQueryHandler mSongQueryHandler;
    private MusicApp app;
    private int mQueriedPos;
    private LinearLayoutManager mLayoutManager;
    private String mQueryString;
    private OnScrollListener mOnScrollListener;
    private RotateLoading mTopLoading;
    private Handler mHandler;

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
        mAdapter = new SearchAdapter(new ArrayList<>());
        mAdapter.setOnItemClickListener(mItemClickListener);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setHasFixedSize(true);
        mSongQueryHandler = new SongQueryHandler(getMainLooper(), mSongQueryListener);
        mTopLoading = findViewById(R.id.search_loading);
        findViewById(R.id.back).setOnClickListener(view -> {
            mSearch.clearFocus();
            finish();
        });
        mOnScrollListener = new OnScrollListener();
        mRecycler.addOnScrollListener(mOnScrollListener);
        mHandler = new Handler();
    }

    @Override
    public void onSongChanged(Song song) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.get(i).playing = mAdapter.get(i).equals(song);
        }
        mHandler.postDelayed(() -> mAdapter.notifyDataSetChanged(), 300);
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

    private SongQueryHandler.OnFinishListener mSongQueryListener = this::onQueryComplete;


    private void onQueryStart() {
        mQueriedPos = 1;
        mAdapter.clear();
        if (!mTopLoading.isStart()) {
            mTopLoading.start();
        }
        new Thread(() -> QQMusicApi.querySong(mQueriedPos, 60, mQueryString, mSongQueryHandler)).start();
    }

    private void onQueryMore() {
        mAdapter.setLoadState(SearchAdapter.STATE_LOADING);
        new Thread(() -> QQMusicApi.querySong(mQueriedPos, 60, mQueryString, mSongQueryHandler)).start();
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
        if (list.size() == 0) {
            mAdapter.setLoadState(SearchAdapter.STATE_NO_MORE);
        } else {
            mAdapter.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
        if (mTopLoading.isStart()) {
            mTopLoading.stop();
        }
    }

    private void onQuerySubmit(String str) {
        if (str.equals(mQueryString)) {
            return;
        }
        if (mAdapter.getItemCount() != 0) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
        mOnScrollListener.init();
        mQueryString = str;
        onQueryStart();
    }

    private OnTextChangeListener mSearchListener = new OnTextChangeListener() {

        @Override
        public boolean onQueryTextSubmit(String queryText) {
            mSearch.clearFocus();
            mSearch.setFocusable(false);
            mSearch.setSubmitButtonEnabled(false);
            onQuerySubmit(queryText);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mSearch.setFocusable(true);
            mSearch.setSubmitButtonEnabled(true);
            if (mTopLoading.isStart()) {
                mTopLoading.stop();
            }
            return true;
        }
    };
}
