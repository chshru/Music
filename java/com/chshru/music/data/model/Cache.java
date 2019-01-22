package com.chshru.music.data.model;

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

    public int id;
    public static final String _id = "id";


    public String mid;
    public static final String _mid = "mid";

    public String url;
    public static final String _url = "url";

    public String date;
    public static final String _date = "date";

}
