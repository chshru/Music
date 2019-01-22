package com.chshru.music.ui.main;

import android.Manifest;
import android.content.Intent;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.SongScanner;
import com.tbruyelle.rxpermissions.RxPermissions;


/**
 * Created by abc on 18-11-12.
 */

public class WelcomeActivity extends ActivityBase {

    private final String[] PM = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private SongScanner mScanner;

    private RxPermissions rxPermissions;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initialize() {
        rxPermissions = new RxPermissions(this);
        checkPrimission();
    }

    private void scanSongs(MusicApp app, boolean local) {
        mScanner = new SongScanner(getApplicationContext());
        mScanner.setHistoryList(app.getListData().getList(ListData.P_HISTORY));
        mScanner.setLoveList(app.getListData().getList(ListData.P_LOVE));
        if (local) {
            mScanner.setLocalList(app.getListData().getList(ListData.P_LOCAL));
            mScanner.startLocalScan();
        }
        new Thread(() -> {
            mScanner.startHistoryScan(app.getHelper().getHistory());
            mScanner.startLoveScan(app.getHelper().getLove());
        }).start();
    }

    private void startActivityWithPm(boolean pm) {
        MusicApp app = (MusicApp) getApplication();
        if (!app.hasInitialized()) {
            app.init();
            scanSongs(app, pm);
        }
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void checkPrimission() {
        boolean needRequest = false;
        for (String pm : PM) {
            if (!rxPermissions.isGranted(pm)) {
                needRequest = true;
                break;
            }
        }
        if (!needRequest) {
            startActivityWithPm(true);
            return;
        }
        rxPermissions.request(PM).subscribe(granted -> {
            if (granted) {
                startActivityWithPm(true);
            } else {
                startActivityWithPm(false);
            }
        });
    }
}
