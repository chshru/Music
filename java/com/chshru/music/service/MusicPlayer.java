package com.chshru.music.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.HistoryTable;
import com.chshru.music.util.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private PlayService mService;
    private Song mCurSong;
    private HistoryTable mHistoryTable;
    private List<StatusCallback> mCallbacks;
    private MusicApp mApp;
    private List<Song> mCurSongList;
    private AudioManager mAudioManager;
    private boolean mHasAudioFocus;
    private boolean mPauseByFocus;

    public MusicPlayer(Context context, MusicApp app) {
        mCallbacks = new ArrayList<>();
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mHasAudioFocus = false;
        mPauseByFocus = false;
        mApp = app;
    }

    public void addCallback(StatusCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
            callback.onSongChanged(mCurSong);
        }
    }

    private boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(mFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(mFocusListener);
    }

    private AudioManager.OnAudioFocusChangeListener mFocusListener = focusChange -> {
        if (!hasService() || !hasPrepare()) {
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (hasPrepare() && !isPlaying() && mPauseByFocus) {
                    togglePause();
                    mPauseByFocus = false;
                }
                mHasAudioFocus = true;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (hasPrepare() && isPlaying()) {
                    togglePause();
                }
                mHasAudioFocus = false;
                abandonAudioFocus();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (hasPrepare() && isPlaying()) {
                    togglePause();
                    mPauseByFocus = true;
                }
                mHasAudioFocus = false;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mHasAudioFocus = false;
                break;
            default:
                break;
        }
    };

    public void setHistoryTable(HistoryTable historyTable) {
        mHistoryTable = historyTable;
    }

    public void rmCallback(StatusCallback callback) {
        mCallbacks.remove(callback);
    }

    public void setService(PlayService service) {
        mService = service;
    }

    public void removeService() {
        mService = null;
    }

    public void prepare(Song song) {
        if (mService == null) {
            return;
        }
        if (mCurSong != null && mCurSong.equals(song)) {
            return;
        }
        if (mCurSong != null) {
            mCurSong.playing = false;
        }
        String local = song.link;
        if (local == null) {
            local = mApp.getServer().getProxyUrl(song.mid);
        }
        mService.setPreparedListener(this);
        mService.setCompletionListener(mOnComplete);
        mService.prepare(local);

        mCurSong = new Song(song);
        mCurSong.type = Song.TYPE_LOCAL;
        mCurSong.time = String.valueOf(System.currentTimeMillis());
        if (mApp.getListData().getPos() != ListData.P_HISTORY) {
            List<Song> history = mApp.getListData().getList(ListData.P_HISTORY);
            Song tempSong = null;
            for (Song s : history) {
                if (s.equals(mCurSong)) {
                    tempSong = s;
                    history.remove(s);
                    break;
                }
            }
            if (tempSong != null) {
                tempSong.copyFrom(mCurSong);
                mHistoryTable.update(mCurSong);
            } else {
                tempSong = new Song(mCurSong);
                mHistoryTable.insert(mCurSong);
            }
            history.add(0, tempSong);
        }

        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onSongChanged(mCurSong);
            }
        }
    }

    public boolean hasService() {
        return mService != null;
    }

    private MediaPlayer.OnCompletionListener mOnComplete = mp -> toggleNextSong(1);

    public void seekTo(int p) {
        if (mService.hasPrepare()) {
            mService.seekTo(p);
        }
    }

    private void toggleNextSong(int d) {
        int listPos = mApp.getListData().getPos();
        if (mCurSong == null) {
            return;
        }
        if (listPos == ListData.P_SEARCH) {
            listPos = ListData.P_HISTORY;
        }
        mCurSongList = mApp.getListData().getList(listPos);
        if (mCurSongList.size() == 0) return;
        int songPos = -1;
        for (int i = 0; i < mCurSongList.size(); i++) {
            if (mCurSongList.get(i).equals(mCurSong)) {
                songPos = i;
                break;
            }
        }
        if (songPos == -1) songPos = 0;
        int tot = mCurSongList.size();
        if (d > 0) {
            songPos = (songPos + 1) % tot;
        }
        if (d < 0) {
            songPos = songPos - 1 >= 0 ? songPos - 1 : tot - 1;
        }
        prepare(mCurSongList.get(songPos));
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mService.serPrepared(true);
        if (!mHasAudioFocus && !requestAudioFocus()) {
            return;
        }
        player.start();
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
        }
    }

    public Song getCurSong() {
        return mCurSong;
    }

    public void start() {
        if (mService == null) {
            return;
        }
        if (!mHasAudioFocus && !requestAudioFocus()) {
            return;
        }
        mService.start();
    }

    public void release() {
        if (mService == null) {
            return;
        }
        mService.release();
    }

    public void next() {
        toggleNextSong(1);
    }

    public void prev() {
        toggleNextSong(-1);
    }

    public boolean isPlaying() {
        return mService != null && mService.isPlaying();
    }

    public void pause() {
        if (mService == null) {
            return;
        }
        mService.pause();
    }

    public int getDuration() {
        if (mService == null) {
            return 0;
        }
        return mService.getDuration();
    }

    public int getCurDuration() {
        if (mService == null) {
            return 0;
        }
        return mService.getCurDuration();
    }

    public boolean hasPrepare() {
        return mService != null && mService.hasPrepare();
    }

    public void togglePause() {
        if (!hasPrepare()) {
            return;
        }
        boolean playing = isPlaying();
        if (playing) pause();
        else start();
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
        }
    }
}
