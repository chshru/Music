package com.chshru.music.ui.main.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.search.SearchResultAdapter.OnItemClickListener;
import com.chshru.music.ui.view.ActionSearchView;
import com.chshru.music.ui.view.ActionSearchView.OnTextChangeListener;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.QueryHandler;
import com.chshru.music.util.QueryHandler.OnFinishRunnable;

import java.util.ArrayList;

/**
 * Created by abc on 18-10-26.
 */

public class SearchActivity extends ActivityBase {

    private ActionSearchView mSearch;
    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private QueryHandler mHandler;
    private MusicApp app;
    private int mCurPos;
    private int mQueriedPos;
    private LinearLayoutManager mLayoutManager;
    private String mQueryString;

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
        mCurPos = -1;
        mQueriedPos = 1;
        app = (MusicApp) getApplication();
        mSearch = findViewById(R.id.sv_search_aty);
        mSearch.setOnQueryTextListener(mSearchListener);
        mRecycler = findViewById(R.id.search_aty_recycler);
        mAdapter = new SearchResultAdapter(new ArrayList<>(), getMainLooper());
        mAdapter.setOnItemClickListener(mItemClickListener);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mHandler = new QueryHandler(getMainLooper(), mRunnable);
        findViewById(R.id.back).setOnClickListener(view -> {
            mSearch.clearFocus();
            finish();
        });
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int totalItemCount;
            private int previousTotal = 0;
            private int visibleItemCount;
            private int firstVisibleItem;
            private boolean loading = true;

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
                    queryMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v) {
            int pos = mRecycler.getChildAdapterPosition(v);
            if (mCurPos != -1 && mAdapter.getItemCount() < mCurPos) {
                mAdapter.get(mCurPos).playing = false;
                mAdapter.notifyItemChanged(mCurPos);
            }
            mCurPos = pos;
            mAdapter.get(mCurPos).playing = true;
            mAdapter.notifyItemChanged(mCurPos);
            String url = QQMusicApi.buildSongUrl(mAdapter.get(pos).mid);
            app.getPlayer().prepare(url);
        }
    };

    private OnFinishRunnable mRunnable = new OnFinishRunnable() {
        @Override
        public void run() {
            queryComplete(getResult());
        }
    };

    private void startQuery() {
        mQueriedPos = 1;
        mAdapter.clear();
        new Thread(() -> QQMusicApi.query(mQueriedPos, 20, mQueryString, mHandler)).start();
    }

    private void queryMore() {
        new Thread(() -> QQMusicApi.query(mQueriedPos, 20, mQueryString, mHandler)).start();
    }

    private void queryComplete(String result) {
        mAdapter.addAll(QQMusicApi.getSongFromResult(result));
        mAdapter.notifyDataSetChanged();
    }

    private void onQuerySubmit(String str) {
        System.out.println("chenshanru mQueryString = " + mQueryString);
        if (str.equals(mQueryString)) {
            return;
        }
        mQueryString = str;
        startQuery();
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
