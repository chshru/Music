package com.chshru.music.ui.tab.searchtab;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.chshru.music.R;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.QueryHandler;
import com.chshru.music.util.QueryHandler.ExeRunnable;
import com.chshru.music.util.Song;
import com.chshru.music.ui.tab.searchtab.SearchResultAdapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class SearchTabLayout extends BaseTab {

    private EditText mInput;
    private QueryHandler mHandler;
    private RecyclerView mRecycler;
    private SearchResultAdapter mAdapter;
    private List<Song> mSong;

    public SearchTabLayout(StatusCallback callback, int resId) {
        super(callback, resId);
        mHandler = new QueryHandler(
                callback.getMainLooper(),
                mRunnable
        );
        mSong = new ArrayList<>();
        mAdapter = new SearchResultAdapter(mSong);

    }

    private ExeRunnable mRunnable = new ExeRunnable() {

        @Override
        public void run() {
            queryComplete(getResult());
        }
    };

    private void queryComplete(String result) {
        System.out.println(result);
        mSong.clear();
        mSong.addAll(QQMusicApi.getSongFromResult(result));
        mAdapter.notifyDataSetChanged();
    }

    private void startQuery(String input) {
        new Thread(() -> QQMusicApi.query(1, 20, input, mHandler)).start();
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.search_tab_title;
    }

    @Override
    public void initChild(Context context, View root) {
        mInput = root.findViewById(R.id.search_input);
        mInput.setOnClickListener(mOnClickListaner);
        mRecycler = root.findViewById(R.id.search_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(context));
        root.findViewById(R.id.search_commit)
                .setOnClickListener(mOnClickListaner);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
    }


    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v) {
            int pos = mRecycler.getChildAdapterPosition(v);
            mCallback.requestChangeSong(mSong.get(pos));
        }
    };


    private View.OnClickListener mOnClickListaner = v -> {
        switch (v.getId()) {
            case R.id.search_input:
                break;
            case R.id.search_commit:
                String input = mInput.getText().toString();
                if (input.isEmpty()) {
                    return;
                }
                startQuery(input);

                break;
        }
    };


}
