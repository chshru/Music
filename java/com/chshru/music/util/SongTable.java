package com.chshru.music.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abc on 18-10-22.
 */

public class SongTable extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "music";


    private static final String CREATE_MUSIC_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("type integer,")
                    .append("mid integer,")
                    .append("title text,")
                    .append("artist text,")
                    .append("link text")
                    .append(")")
                    .toString();

    public SongTable(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MUSIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void insert(Song song) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(song._id, song.id);
        values.put(song._type, song.type);
        values.put(song._mid, song.mid);
        values.put(song._title, song.title);
        values.put(song._artist, song.artist);
        values.put(song._link, song.link);
        db.insert(TABLE_NAME, null, values);
    }

    public void update(Song m) {

    }

    public void delete(Song m) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = m._id + "=?";
        String[] args = {Integer.toString(m.id)};
        db.delete(TABLE_NAME, sql, args);
    }


    public Cursor query(Song m) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
