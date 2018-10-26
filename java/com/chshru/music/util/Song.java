package com.chshru.music.util;

import android.graphics.Bitmap;

import java.util.BitSet;

/**
 * Created by abc on 18-10-22.
 */

public class Song {
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_NET = 1;

    public Song(int id, int type, String mid, String title, String artist, String link, String album) {
        this.id = id;
        this.type = type;
        this.mid = mid;
        this.title = title;
        this.artist = artist;
        this.link = link;
        this.album = album;
        playing = false;
    }


    public boolean playing;
    public Bitmap albumBitmap;

    public int id; //数据库id
    String _id = "id";

    public int type; //网络,本地

    public String album;

    String _album = "album";

    String _type = "type";

    public String mid; //网络songid
    String _mid = "mid";

    public String title;
    String _title = "title";

    public String artist;
    String _artist = "artist";

    public String link;
    String _link = "link";

    public boolean equals(Song song) {
        return this.id == song.id;
    }


    @Override
    public String toString() {
        return _id + "=" + id +
                _type + "=" + type +
                _mid + "=" + mid +
                _title + "=" + title +
                _artist + "=" + artist +
                _link + "=" + link;
    }
}
