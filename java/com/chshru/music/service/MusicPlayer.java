package com.chshru.music.service;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.chshru.music.base.MusicApp;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.data.model.Cache;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.data.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-22.
 */

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private PlayService mService;
    private Song mCurSong;
    private List<StatusCallback> mCallbacks;
    private MusicApp mApp;
    private List<Song> mCurSongList;
    private AudioManager mAudioManager;
    private boolean mHasAudioFocus;
    private boolean mPauseByFocus;
    private boolean mIsFirst;
    private Handler mHandler;

    public MusicPlayer(Context context, MusicApp app) {
        mCallbacks = new ArrayList<>();
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mHasAudioFocus = false;
        mPauseByFocus = false;
        mIsFirst = true;
        mApp = app;
        mHandler = new Handler();
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
        prepare(song, false);
    }

    private void prepareImpl(String local, Song song) {
        mService.setPreparedListener(this);
        mService.setCompletionListener(mOnComplete);
        mService.prepare(local);

        mCurSong = new Song(song);
        mApp.getPrefHelper().setSong(mCurSong);
        mCurSong.type = Song.TYPE_LOCAL;
        mCurSong.time = String.valueOf(System.currentTimeMillis());
        String table = mApp.getListData().getPos();
        mApp.getPrefHelper().setPlayTable(table);
        if (!table.equals(ListData.P_HISTORY)) {
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
                mApp.getHelper().getHistory().update(mCurSong);
            } else {
                tempSong = new Song(mCurSong);
                mApp.getHelper().getHistory().insert(mCurSong);
            }
            history.add(0, tempSong);
        }

        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onSongChanged(mCurSong);
            }
        }
    }

    private void prepare(Song song, boolean same) {
        if (song == null) {
            mIsFirst = false;
            return;
        }
        if (mService == null) {
            return;
        }
        if (mCurSong != null && mCurSong.equals(song) && !same) {
            return;
        }
        if (mCurSong != null) {
            mCurSong.playing = false;
        }
        String local = song.link;
        if (local == null) {
            Cursor cursor = mApp.getHelper().getCache().query();
            cursor.moveToFirst();
            String cacheUrl = null;
            if (cursor.getCount() > 0) {
                do {
                    int id = cursor.getInt(0);
                    if (id == song.id) {
                        cacheUrl = cursor.getString(2);
                        break;
                    }
                } while (cursor.moveToNext());
            }
            if (cacheUrl != null) {
                if (mApp.getServer().isCached(cacheUrl)) {
                    local = mApp.getServer().getProxyUrl(cacheUrl);
                    prepareImpl(local, song);
                }
            }
            if (local == null) {
                new GetUrlThread(song.mid, song).start();
            }
        } else {
            prepareImpl(local, song);
        }

    }

    private class GetUrlThread extends Thread {
        private String url;
        private Song song;

        GetUrlThread(String url, Song song) {
            super();
            this.url = url;
            this.song = song;
        }

        @Override
        public void run() {
            String result = QQMusicApi.buildSongUrl(url);
            if (result.equals(QQMusicApi.SONG_GET_KEY_ERROR)) {
                mCurSong = new Song(song);
                mHandler.post(() -> toggleNextSong(1));
            } else {
                String local = mApp.getServer().getProxyUrl(result);
                long date = System.currentTimeMillis();
                Cache cache = new Cache(song.id, song.mid, result, String.valueOf(date));
                mApp.getHelper().getCache().insert(cache);
                mHandler.post(() -> prepareImpl(local, song));
            }
        }
    }

    public boolean hasService() {
        return mService != null;
    }

    private MediaPlayer.OnCompletionListener mOnComplete = mp -> toggleNextSong(0);

    public void seekTo(int p) {
        if (mService.hasPrepare()) {
            mService.seekTo(p);
        }
    }

    private void toggleNextSong(int d) {
        String listPos = mApp.getListData().getPos();
        if (mCurSong == null) {
            return;
        }
        if (listPos.equals(ListData.P_SEARCH)) {
            listPos = ListData.P_HISTORY;
            mApp.getListData().setPos(ListData.P_HISTORY);
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
        int mode = mApp.getListData().getCurMode();
        switch (mode) {
            case ListData.PLAY_LIST:
                if (d >= 0) {
                    songPos = (songPos + 1) % tot;
                } else {
                    songPos = songPos - 1 >= 0 ? songPos - 1 : tot - 1;
                }
                break;
            case ListData.PLAY_ONE:
                if (d > 0) {
                    songPos = (songPos + 1) % tot;
                } else if (d < 0) {
                    songPos = songPos - 1 >= 0 ? songPos - 1 : tot - 1;
                }
                break;
            case ListData.PLAY_RAND:
                if (tot > 1) {
                    int tempPos;
                    do {
                        tempPos = (int) (Math.random() * tot);
                    } while (tempPos == songPos);
                    songPos = tempPos;
                }
                break;
        }
        prepare(mCurSongList.get(songPos), true);
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        mService.serPrepared(true);
        if (!mHasAudioFocus && !requestAudioFocus()) {
            return;
        }
        if (mIsFirst) {
            int dur = mApp.getPrefHelper().getCurDuration();
            if (dur < 0 || dur > player.getDuration()) {
                dur = 0;
            }
            player.seekTo(dur);
            mIsFirst = false;
        } else {
            player.start();
        }
        for (StatusCallback callback : mCallbacks) {
            if (callback != null) {
                callback.togglePlayer(false);
            }
        }
    }

    public int getAudioSessionId() {
        return mService.getAudioSessionId();
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
