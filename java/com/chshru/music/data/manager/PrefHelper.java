package com.chshru.music.data.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.data.model.Song;

import java.util.List;

/**
 * Created by abc on 18-10-23.
 */

public class PrefHelper {

    private static final String NAME = "data";
    private static final String PLAY_TABLE = "table";
    private static final String PLAY_POS = "pos";
    private static final String PLAY_DUR = "dur";
    private static final String PLAY_CURDUR = "curdur";
    private static final String PLAY_MODE = "mode";
    private static final String PLAY_ALBUM_TYPE = "album_type";

    public static final int PLAYING_ALBUM_PIC = 1;
    public static final int PLAYING_ALBUM_VIS = 2;

    private SharedPreferences mData;
    private Context mContext;
    private ListData mListData;

    public PrefHelper(Context context, ListData listData) {
        mListData = listData;
        mContext = context;
        mData = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void setPlayTable(String p) {
        mData.edit().putString(PLAY_TABLE, p).apply();
    }

    public void setAlbumType(int type) {
        mData.edit().putInt(PLAY_ALBUM_TYPE, type).apply();
    }

    public int getAlbumType() {
        return mData.getInt(PLAY_ALBUM_TYPE, PLAYING_ALBUM_PIC);
    }

    public void setSong(Song song) {
        mData.edit().putInt(PLAY_POS, song.id).apply();
    }

    public void setDuration(int p) {
        mData.edit().putInt(PLAY_DUR, p).apply();
    }

    public void setCurDuration(int p) {
        mData.edit().putInt(PLAY_CURDUR, p).apply();
    }

    public void setMode(int p) {
        mData.edit().putInt(PLAY_MODE, p).apply();
    }

    private String getPlayTable() {
        return mData.getString(PLAY_TABLE, ListData.P_SEARCH);
    }

    private int getSongId() {
        return mData.getInt(PLAY_POS, -1);
    }

    public int getDuration() {
        return mData.getInt(PLAY_DUR, -1);
    }

    public int getCurDuration() {
        return mData.getInt(PLAY_CURDUR, -1);
    }

    public int getMode() {
        return mData.getInt(PLAY_MODE, 0);
    }

    public Song getSong() {
        Song song = null;
        int id = getSongId();
        String table = getPlayTable();
        if (table == null || table.isEmpty()) {
            return null;
        }
        if (table.equals(ListData.P_SEARCH)) {
            table = ListData.P_HISTORY;
        }
        mListData.setPos(table);
        mListData.setCurMode(getMode());
        List<Song> list = mListData.getList(table);
        if (list == null) {
            return null;
        }
        for (Song s : list) {
            if (s.id == id) {
                song = s;
                break;
            }
        }
        return song;
    }

}
