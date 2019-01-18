package com.chshru.music.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by abc on 19-1-18.
 */

public class CacheTable extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "cache";


    public static final String CREATE_CACHE_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("mid text,")
                    .append("url text,")
                    .append("date text")
                    .append(")")
                    .toString();

    public CacheTable(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LoveTable.CREATE_LOVE_TABLE);
        db.execSQL(HistoryTable.CREATE_HISTORY_TABLE);
        db.execSQL(CacheTable.CREATE_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(Cache cache) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cache._id, cache.id);
        values.put(Cache._mid, cache.mid);
        values.put(Cache._url, cache.url);
        values.put(Cache._date, cache.date);
        delete(cache);
        db.insert(TABLE_NAME, null, values);
    }

    public void delete(Cache cache) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = Cache._id + "=?";
        String[] args = {Integer.toString(cache.id)};
        db.delete(TABLE_NAME, sql, args);
    }


    public void update(Cache cache) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cache._url, cache.url);
        values.put(Cache._date, cache.date);
        String sql = Cache._id + "=?";
        String[] args = {Integer.toString(cache.id)};
        db.update(TABLE_NAME, values, sql, args);
    }

    public Cursor query() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                Cache._id,
                Cache._mid,
                Cache._url,
                Cache._date
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
