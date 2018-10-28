package com.chshru.music.service;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.chshru.music.util.Song;

/**
 * Created by abc on 18-10-25.
 */

public interface StatusCallback {

    void onSongChanged(Song song);

    void togglePlayer();

    Looper getMainLooper();

    Context getApplicationContext();

    Application getApplication();
}
