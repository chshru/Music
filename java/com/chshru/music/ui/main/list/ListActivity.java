package com.chshru.music.ui.main.list;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.os.Handler;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.listener.OnItemClickListener;
import com.chshru.music.ui.tab.localtab.LocalTab;
import com.chshru.music.data.model.Song;

import java.util.List;

import static com.chshru.music.ui.tab.localtab.LocalTab.STARY_TYPE;

public class ListActivity extends ActivityBase implements StatusCallback {

    private RecyclerView mRecycler;
    private ListAdapter mAdapter;
    private MusicApp mApp;
    private String mStartType;
    private Handler mHandler;
    private String mStartTypeOnline;

    @Override
    protected int getLayoutId() {
        overridePendingTransition(R.xml.slide_in_right, 0);
        return R.layout.activity_list;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.xml.slide_out_right);
    }


    @Override
    protected void initialize() {
        mStartType = getIntent().getStringExtra(STARY_TYPE);
        if (mStartType == null || mStartType.isEmpty()) {
            return;
        }
        mApp = (MusicApp) getApplication();
        String title = getIntent().getStringExtra(LocalTab.STARY_TITLE);
        TextView titleTv = findViewById(R.id.list_title);
        titleTv.setText(title);

        List<Song> list = mApp.getListData().getList(mStartType);

        Song curSong = mApp.getPlayer().getCurSong();
        for (Song song : list) {
            song.playing = song.equals(curSong);
        }
        mAdapter = new ListAdapter(list);
        mRecycler = findViewById(R.id.list_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mRecycler.setHasFixedSize(true);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        mAdapter.setOnItemClickListener(mItemClick);
        mHandler = new Handler();
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
        mHandler.postDelayed(() -> mAdapter.notifyDataSetChanged(), 300);
    }

    @Override
    public void togglePlayer(boolean real) {

    }

    private OnItemClickListener mItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View v) {
            mApp.getListData().setPos(mStartType);
            int pos = mRecycler.getChildAdapterPosition(v);
            Song song = mAdapter.get(pos);
            mApp.getPlayer().prepare(song);
        }

        @Override
        public void OnItemLongClick(View v) {
            if (mStartType.equals(ListData.P_LOVE) || mStartType.equals(ListData.P_HISTORY)) {
                Intent intent = new Intent(ListActivity.this, DelActivity.class);
                intent.putExtra(STARY_TYPE, mStartType);
                startActivity(intent);
            }
        }
    };
}
