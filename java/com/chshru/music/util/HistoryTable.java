package com.chshru.music.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abc on 18-10-22.
 */

public class HistoryTable extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "history";


    public static final String CREATE_MUSIC_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("type integer,")
                    .append("album text,")
                    .append("mid integer,")
                    .append("title text,")
                    .append("artist text,")
                    .append("link text,")
                    .append("time text")
                    .append(")")
                    .toString();

    public HistoryTable(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LoveTable.CREATE_MUSIC_TABLE);
        db.execSQL(HistoryTable.CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void insert(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Song._id, song.id);
        values.put(Song._type, song.type);
        values.put(Song._album, song.album);
        values.put(Song._mid, song.mid);
        values.put(Song._title, song.title);
        values.put(Song._artist, song.artist);
        values.put(Song._link, song.link);
        values.put(Song._time, song.time);
        delete(song);
        db.insert(TABLE_NAME, null, values);
    }

    public void update(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Song._time, song.time);
        String sql = Song._id + "=?";
        String[] args = {Integer.toString(song.id)};
        db.update(TABLE_NAME, values, sql, args);
    }

    public void delete(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = Song._id + "=?";
        String[] args = {Integer.toString(song.id)};
        db.delete(TABLE_NAME, sql, args);
    }


    public Cursor query() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                Song._id,
                Song._type,
                Song._album,
                Song._mid,
                Song._title,
                Song._artist,
                Song._link,
                Song._time,
        };
        return db.query(
                TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
    }

}
