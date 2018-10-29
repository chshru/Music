package com.chshru.music.service;

import android.media.MediaPlayer;

/**
 * Created by abc on 18-10-22.
 */

public interface PlayService {

    void release();

    void play();

    void pause();

    void start();

    void prepare(String url);


    boolean isPlaying();

    boolean hasPrepare();

    void setPreparedListener(MediaPlayer.OnPreparedListener listener);

    void serPrepared(boolean b);
}
