package com.chshru.music.ui.main.list;


import com.chshru.music.data.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ListData {

    public static final int PLAY_LIST = 0;
    public static final int PLAY_ONE = 1;
    public static final int PLAY_RAND = 2;
    public static final int MODE_COUNT = 3;

    public static final int P_LOCAL = 0;
    public static final int P_HISTORY = 1;
    public static final int P_LOVE = 2;
    public static final int P_SEARCH = 3;

    private List<List<Song>> mList;
    private int mPos;
    private int mCurMode;

    public ListData() {
        mPos = mCurMode = 0;
        mList = new ArrayList<>();
        mList.add(new ArrayList<>());
        mList.add(new ArrayList<>());
        mList.add(new ArrayList<>());
    }

    public void setCurMode(int curMode) {
        mCurMode = curMode;
    }

    public int getCurMode() {
        return mCurMode;
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    public int getPos() {
        return mPos;
    }

    public void addAll(List<Song> list, int p) {
        mList.get(p).addAll(list);
    }

    public void clear(int p) {
        mList.get(p).clear();
    }

    public List<Song> getList(int pos) {
        return mList.get(pos);
    }
}
