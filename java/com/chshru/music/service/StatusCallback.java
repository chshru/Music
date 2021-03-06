package com.chshru.music.service;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.chshru.music.data.model.Song;

/**
 * Created by abc on 18-10-25.
 */

public interface StatusCallback {

    void onSongChanged(Song song);

    void togglePlayer(boolean real);

    Looper getMainLooper();

    Context getApplicationContext();

    Application getApplication();
}
