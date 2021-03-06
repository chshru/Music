package com.chshru.music.data.model;


/**
 * Created by abc on 18-10-22.
 */

public class Song {
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_NET = 1;

    public Song(int id, int type, String album, String albumName, String mid, String title, String artist, String link) {
        this.id = id;
        this.type = type;
        this.mid = mid;
        this.albumName = albumName;
        this.title = title;
        this.artist = artist;
        this.link = link;
        this.album = album;
        playing = false;
        if (albumName == null || albumName.isEmpty()) {
            this.albumName = "unknown";
        }
    }

    public Song(Song song) {
        if (song != null) {
            this.id = song.id;
            this.type = song.type;
            this.mid = song.mid;
            this.title = song.title;
            this.artist = song.artist;
            this.link = song.link;
            this.album = song.album;
            this.playing = song.playing;
            this.time = song.time;
            this.albumName = song.albumName;
        }
    }

    public void copyFrom(Song song) {
        if (song != null) {
            this.id = song.id;
            this.type = song.type;
            this.mid = song.mid;
            this.title = song.title;
            this.artist = song.artist;
            this.link = song.link;
            this.album = song.album;
            this.playing = song.playing;
            this.time = song.time;
            this.albumName = song.albumName;
        }
    }

    public boolean playing;

    public int id; //数据库id
    public static String _id = "id";


    public int type; //网络,本地
    public static String _type = "type";


    public String album;
    public static String _album = "album";


    public String albumName;
    public static String _albumName = "albumName";


    public String mid; //网络songid
    public static String _mid = "mid";


    public String title;
    public static String _title = "title";


    public String artist;
    public static String _artist = "artist";


    public String link;
    public static String _link = "link";

    public String time;
    public static String _time = "time";


    public boolean equals(Song song) {
        if (song == null) {
            return false;
        }
        return id == song.id;
    }


    @Override
    public String toString() {
        return _id + "=" + id +
                _type + "=" + type +
                _mid + "=" + mid +
                _title + "=" + title +
                _artist + "=" + artist +
                _link + "=" + link +
                _album + "=" + album;
    }

}
