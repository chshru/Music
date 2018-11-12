package com.chshru.music.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;
import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.SongScanner;

/**
 * Created by abc on 18-11-12.
 */

public class WelcomeActivity extends ActivityBase {

    private final String[] PM = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final int PM_CODE = 233333;
    private SongScanner mScanner;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initialize() {
        mScanner = new SongScanner(getApplicationContext());
        if (checkPrimission()) {
            MusicApp app = (MusicApp) getApplication();
            if (!app.hasInitialized()) {
                app.init();
                scanSongs(app);
            }
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            requestPermission();
        }
    }

    private void scanSongs(MusicApp app) {
        mScanner.setHistoryList(app.getListData().getList(ListData.P_HISTORY));
        mScanner.setLocalList(app.getListData().getList(ListData.P_LOCAL));
        mScanner.setLoveList(app.getListData().getList(ListData.P_LOVE));
        mScanner.startLocalScan();
        mScanner.startHistoryScan(app.getHistoryTable());
        mScanner.startLoveScan(app.getLoveTable());
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PM, PM_CODE);
        }
    }

    private boolean checkPrimission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String aPM : PM) {
                if (checkSelfPermission(aPM) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int rCode, @NonNull String[] pre, @NonNull int[] result) {
        if (rCode == PM_CODE) {
            boolean flag = true;
            for (int aResult : result) {
                if (aResult != PackageManager.PERMISSION_GRANTED) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                MusicApp app = (MusicApp) getApplication();
                if (!app.hasInitialized()) {
                    app.init();
                    scanSongs(app);
                }
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                finish();
            }
        }
    }
}
