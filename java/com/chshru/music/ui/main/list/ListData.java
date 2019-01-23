package com.chshru.music.ui.main.list;


import com.chshru.music.data.model.OnlineList;
import com.chshru.music.data.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListData {

    public static final int PLAY_LIST = 0;
    public static final int PLAY_ONE = 1;
    public static final int PLAY_RAND = 2;
    public static final int MODE_COUNT = 3;

    public static final String P_LOCAL = "0";
    public static final String P_HISTORY = "1";
    public static final String P_LOVE = "2";
    public static final String P_SEARCH = "3";

    public static final int P_LOCAL_LIST_END = 3;

    private Map<String, List<Song>> mList;
    private List<OnlineList> mOnlineList;
    private String mPos;
    private int mCurMode;

    public ListData() {
        mCurMode = 0;
        mList = new HashMap<>();
        mList.put(P_LOCAL, new ArrayList<>());
        mList.put(P_HISTORY, new ArrayList<>());
        mList.put(P_LOVE, new ArrayList<>());
        mOnlineList = new ArrayList<>();
    }

    public void addOnlineList(String key, List<Song> list) {
        mList.put(key, list);
    }

    public List<OnlineList> getOnlineList() {
        return mOnlineList;
    }

    public void setCurMode(int curMode) {
        mCurMode = curMode;
    }

    public int getCurMode() {
        return mCurMode;
    }

    public void setPos(String pos) {
        mPos = pos;
    }

    public String getPos() {
        return mPos;
    }

    public void addAll(List<Song> list, String p) {
        mList.get(p).addAll(list);
    }

    public void clear(String p) {
        mList.get(p).clear();
    }

    public List<Song> getList(String pos) {
        return mList.get(pos);
    }
}
