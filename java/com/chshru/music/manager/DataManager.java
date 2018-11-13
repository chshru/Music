package com.chshru.music.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.util.Song;

import java.util.List;

/**
 * Created by abc on 18-10-23.
 */

public class DataManager {

    private static final String NAME = "data";
    private static final String PLAY_TABLE = "table";
    private static final String PLAY_POS = "pos";
    private static final String PLAY_DUR = "dur";
    private static final String PLAY_CURDUR = "curdur";
    private static final String PLAY_MODE = "mode";


    private SharedPreferences mData;
    private Context mContext;
    private ListData mListData;

    public DataManager(Context context, ListData listData) {
        mListData = listData;
        mContext = context;
        mData = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void setPlayTable(int p) {
        mData.edit().putInt(PLAY_TABLE, p).apply();
        if (p >= ListData.P_SEARCH) {
            mData.edit().putInt(PLAY_TABLE, ListData.P_HISTORY).apply();
        }
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

    private int getPlayTable() {
        return mData.getInt(PLAY_TABLE, -1);
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
        int table = getPlayTable();
        if (id == -1 || table == -1) {
            return null;
        }
        if (table >= ListData.P_SEARCH) {
            table = ListData.P_HISTORY;
        }
        mListData.setPos(table);
        mListData.setCurMode(getMode());
        List<Song> list = mListData.getList(table);
        for (Song s : list) {
            if (s.id == id) {
                song = s;
                break;
            }
        }
        return song;
    }

}
