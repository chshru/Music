package com.chshru.music.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.search.SearchActivity;
import com.chshru.music.ui.tab.localtab.LocalTabLayout;
import com.chshru.music.ui.tab.searchtab.SearchTabLayout;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.ui.tab.TabAdapter;
import com.chshru.music.service.MediaService;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.PlayService;
import com.chshru.music.util.SongTable;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.Song;


public class HomeActivity extends ActivityBase implements StatusCallback {

    private final int MSG_FRESH_BOTTOM_BAR = 3;
    private final int MSG_CHANGE_SONG = 1;
    private final int MSG_PAUSE_PLAYER = 2;
    private MusicPlayer mPlayer;
    private Intent mIntent;
    private BottomLayout mBottomLayout;

    private SongTable mSongTable;
    private Song mCurSong;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initialize() {
        initializePages();
        initializeParams();
        initButtomBar();
    }

    private void initializePages() {
        BaseTab page1 = new LocalTabLayout(this, R.layout.layout_page_1_my);
        BaseTab page2 = new SearchTabLayout(this, R.layout.layout_page_2_search);
        BaseTab[] pages = {page1, page2};
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        viewPager.setAdapter(new TabAdapter(this, pages));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initializeParams() {
        mIntent = new Intent(this, MediaService.class);
        MusicApp app = (MusicApp) getApplication();
        if (!app.hasInitlized()) app.init(this);
        mSongTable = app.getSongTable();
        mPlayer = app.getPlayer();
        startService(mIntent);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }


    private void initButtomBar() {
        ViewGroup bottomBar = findViewById(R.id.bottom_bar);
        View bottomLayout = View.inflate(
                getApplicationContext(),
                R.layout.bottom_play_layout,
                bottomBar
        );
        mBottomLayout = new BottomLayout(bottomLayout, this);
        findViewById(R.id.search_button)
                .setOnClickListener(v -> startSearchActivity());
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mPlayer.addPlayService((PlayService) binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.delPlayService();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (!mPlayer.isPlaying()) {
//            stopService(mIntent);
//        } else {
        unbindService(conn);
//        }
    }

    @Override
    public void requestChangeSong(Song song) {
        mHandler.obtainMessage(
                MSG_CHANGE_SONG,
                song
        ).sendToTarget();
    }

    @Override
    public void requestPausePlayer(boolean pause) {
        if (!mPlayer.hasPrepare()) {
            return;
        }
        mHandler.obtainMessage(MSG_PAUSE_PLAYER).sendToTarget();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Song song = (Song) msg.obj;
            switch (msg.what) {
                case MSG_CHANGE_SONG:
                    changeSong(song);
                    obtainMessage(
                            MSG_FRESH_BOTTOM_BAR,
                            song
                    ).sendToTarget();
                    break;
                case MSG_PAUSE_PLAYER:
                    tooglePause();
                    sendEmptyMessage(MSG_FRESH_BOTTOM_BAR);
                    break;
                case MSG_FRESH_BOTTOM_BAR:
                    boolean playing = mPlayer.hasPrepare() && mPlayer.isPlaying();
                    mBottomLayout.freshLayout(playing, song);
                    break;
            }

        }
    };

    private void tooglePause() {
        if (!mPlayer.hasPrepare()) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }

    private void changeSong(Song song) {
        System.out.println(song);
        if (mCurSong != null && mCurSong.equals(song)) {
            return;
        }
        mCurSong = song;
        String url = QQMusicApi.buildSongUrl(song.mid);
        mPlayer.prepare(url);
    }
}
