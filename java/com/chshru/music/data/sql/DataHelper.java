package com.chshru.music.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chshru.music.data.model.Cache;
import com.chshru.music.data.model.Song;

/**
 * Created by abc on 19-1-22.
 */

public class DataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "data.db";
    private static final int DB_VERSION = 1;
    private static final String CACHE_TABLE_NAME = "cache";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String LOVE_TABLE_NAME = "love";


    private static final String CREATE_CACHE_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(CACHE_TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("mid text,")
                    .append("url text,")
                    .append("date text")
                    .append(")")
                    .toString();

    private static final String CREATE_HISTORY_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(HISTORY_TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("type integer,")
                    .append("album text,")
                    .append("albumName text,")
                    .append("mid text,")
                    .append("title text,")
                    .append("artist text,")
                    .append("link text,")
                    .append("time text")
                    .append(")")
                    .toString();

    private static final String CREATE_LOVE_TABLE =
            new StringBuilder()
                    .append("create table ")
                    .append(LOVE_TABLE_NAME)
                    .append(" (")
                    .append("id integer primary key,")
                    .append("type integer,")
                    .append("album text,")
                    .append("albumName text,")
                    .append("mid integer,")
                    .append("title text,")
                    .append("artist text,")
                    .append("link text,")
                    .append("time text")
                    .append(")")
                    .toString();

    private CacheHelper mCache;
    private HistoryHelper mHistory;
    private LoveHelper mLove;


    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mCache = new CacheHelper();
        mHistory = new HistoryHelper();
        mLove = new LoveHelper();
    }

    public CacheHelper getCache() {
        return mCache;
    }

    public HistoryHelper getHistory() {
        return mHistory;
    }

    public LoveHelper getLove() {
        return mLove;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOVE_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public class LoveHelper implements SongHelper {

        public void insert(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Song._id, song.id);
            values.put(Song._type, song.type);
            values.put(Song._album, song.album);
            values.put(Song._albumName, song.albumName);
            values.put(Song._mid, song.mid);
            values.put(Song._title, song.title);
            values.put(Song._artist, song.artist);
            values.put(Song._link, song.link);
            values.put(Song._time, song.time);
            delete(song);
            db.insert(LOVE_TABLE_NAME, null, values);
        }

        public void update(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Song._time, song.time);
            String sql = Song._id + "=?";
            String[] args = {Integer.toString(song.id)};
            db.update(LOVE_TABLE_NAME, values, sql, args);
        }

        public void delete(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            String sql = Song._id + "=?";
            String[] args = {Integer.toString(song.id)};
            db.delete(LOVE_TABLE_NAME, sql, args);
        }

        public Cursor query() {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {
                    Song._id,
                    Song._type,
                    Song._album,
                    Song._albumName,
                    Song._mid,
                    Song._title,
                    Song._artist,
                    Song._link,
                    Song._time,
            };
            return db.query(
                    LOVE_TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public class HistoryHelper implements SongHelper{
        public void insert(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Song._id, song.id);
            values.put(Song._type, song.type);
            values.put(Song._album, song.album);
            values.put(Song._albumName, song.albumName);
            values.put(Song._mid, song.mid);
            values.put(Song._title, song.title);
            values.put(Song._artist, song.artist);
            values.put(Song._link, song.link);
            values.put(Song._time, song.time);
            delete(song);
            db.insert(HISTORY_TABLE_NAME, null, values);
        }

        public void update(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Song._time, song.time);
            String sql = Song._id + "=?";
            String[] args = {Integer.toString(song.id)};
            db.update(HISTORY_TABLE_NAME, values, sql, args);
        }

        public void delete(Song song) {
            SQLiteDatabase db = getReadableDatabase();
            String sql = Song._id + "=?";
            String[] args = {Integer.toString(song.id)};
            db.delete(HISTORY_TABLE_NAME, sql, args);
        }

        public Cursor query() {
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {
                    Song._id,
                    Song._type,
                    Song._album,
                    Song._albumName,
                    Song._mid,
                    Song._title,
                    Song._artist,
                    Song._link,
                    Song._time,
            };
            return db.query(
                    HISTORY_TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public class CacheHelper {
        public void insert(Cache cache) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Cache._id, cache.id);
            values.put(Cache._mid, cache.mid);
            values.put(Cache._url, cache.url);
            values.put(Cache._date, cache.date);
            delete(cache);
            db.insert(CACHE_TABLE_NAME, null, values);
        }

        public void delete(Cache cache) {
            SQLiteDatabase db = getReadableDatabase();
            String sql = Cache._id + "=?";
            String[] args = {Integer.toString(cache.id)};
            db.delete(CACHE_TABLE_NAME, sql, args);
        }


        public void update(Cache cache) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(Cache._url, cache.url);
            values.put(Cache._date, cache.date);
            String sql = Cache._id + "=?";
            String[] args = {Integer.toString(cache.id)};
            db.update(CACHE_TABLE_NAME, values, sql, args);
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
                    CACHE_TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}
