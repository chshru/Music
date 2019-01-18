package com.chshru.music.util;

/**
 * Created by abc on 19-1-18.
 */

public class Cache {
    public Cache(int id, String mid, String url, String date) {
        this.id = id;
        this.mid = mid;
        this.url = url;
        this.date = date;
    }

    int id;
    public static final String _id = "id";


    String mid;
    public static final String _mid = "mid";

    String url;
    public static final String _url = "url";

    String date;
    public static final String _date = "date";

}
