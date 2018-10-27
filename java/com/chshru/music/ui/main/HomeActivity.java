package com.chshru.music.ui.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.search.SearchActivity;
import com.chshru.music.ui.tab.localtab.LocalTab;
import com.chshru.music.ui.tab.searchtab.OnlineTab;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.ui.tab.TabAdapter;
import com.chshru.music.service.MediaService;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.PlayService;
import com.chshru.music.util.SongTable;
import com.chshru.music.util.Song;


public class HomeActivity extends ActivityBase implements StatusCallback {

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
        initBottomBar();
    }

    private void initializePages() {
        BaseTab page1 = new LocalTab(this, R.layout.page_one_local);
        BaseTab page2 = new OnlineTab(this, R.layout.page_two_online);
        BaseTab[] pages = {page1, page2};
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new TabAdapter(this, pages));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initializeParams() {
        mIntent = new Intent(this, MediaService.class);
        MusicApp app = (MusicApp) getApplication();
        if (!app.hasInitialized()) app.init(this);
        mSongTable = app.getSongTable();
        mPlayer = app.getPlayer();
        startService(mIntent);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(
                getApplicationContext(),
                SearchActivity.class
        );
        startActivity(intent);
    }


    private void initBottomBar() {
        ViewGroup bottomBar = findViewById(R.id.bottom_bar);
        View bottomLayout = View.inflate(
                getApplicationContext(),
                R.layout.bottom_play_layout,
                bottomBar
        );
        mBottomLayout = new BottomLayout(bottomLayout, this);
        findViewById(R.id.search_button).setOnClickListener(
                view -> startSearchActivity());
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mPlayer.setService((PlayService) binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.removeService();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(
                mIntent,
                conn,
                BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    @Override
    public void togglePlayer() {
        if (!mPlayer.hasPrepare()) {
            return;
        }
        boolean playing = mPlayer.isPlaying();
        if (playing) mPlayer.pause();
        else mPlayer.start();
        mBottomLayout.freshLayout(!playing, null);
    }

    @Override
    public void onSongChanged(Song song) {
        mBottomLayout.freshLayout(true, song);
    }
}
