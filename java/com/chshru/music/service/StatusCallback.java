package com.chshru.music.service;

import android.content.Context;
import android.os.Looper;

import com.chshru.music.util.Song;

/**
 * Created by abc on 18-10-25.
 */

public interface StatusCallback {

    void requestChangeSong(Song song);

    void requestPausePlayer(boolean pause);

    Looper getMainLooper();

    Context getApplicationContext();
}
