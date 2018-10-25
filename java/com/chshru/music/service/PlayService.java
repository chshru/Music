package com.chshru.music.service;

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

}
