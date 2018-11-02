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
import com.chshru.music.ui.tab.onlinetab.OnlineTab;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.ui.tab.TabAdapter;
import com.chshru.music.service.MediaService;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.PlayService;
import com.chshru.music.util.HistoryTable;
import com.chshru.music.util.Song;


public class HomeActivity extends ActivityBase implements StatusCallback, BottomLayout.BottomController {

    private MusicPlayer mPlayer;
    private Intent mIntent;
    private BottomLayout mBottomLayout;

    private HistoryTable mHistoryTable;
    private BaseTab[] mTabs;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initialize() {
        initBottomBar();
        initializeParams();
        initializePages();
    }

    private void initializePages() {
        BaseTab page1 = new LocalTab(this, R.layout.page_one_local);
        BaseTab page2 = new OnlineTab(this, R.layout.page_two_online);
        BaseTab[] pages = {page1, page2};
        mTabs = pages;
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new TabAdapter(this, pages));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initializeParams() {
        mIntent = new Intent(this, MediaService.class);
        MusicApp app = (MusicApp) getApplication();
        if (!app.hasInitialized()) app.init();
        mHistoryTable = app.getHistoryTable();
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
        mBottomLayout.setController(this);
        findViewById(R.id.search_button).setOnClickListener(
                view -> startSearchActivity());
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mPlayer.setService((PlayService) binder);
            unbindService(conn);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        onSongChanged(null);
        mPlayer.addCallback(this);
        if (!mPlayer.hasService()) {
            bindService(
                    mIntent,
                    conn,
                    BIND_AUTO_CREATE
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.rmCallback(this);
    }

    @Override
    public void togglePlayer(boolean real) {
        if (!real) {
            mBottomLayout.freshLayout(mPlayer.isPlaying(), null);
            return;
        }
        mPlayer.togglePause();
    }

    @Override
    public void onSongChanged(Song song) {
        mBottomLayout.freshLayout(mPlayer.isPlaying(), song);
        if (mTabs != null) {
            for (BaseTab tab : mTabs) {
                tab.freshChild();
            }
        }
    }

    @Override
    public void onPauseClick() {
        togglePlayer(true);
    }

    @Override
    public void onNextClick() {
        mPlayer.next();
    }
}
