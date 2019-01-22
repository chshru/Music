package com.chshru.music.data.sql;

import android.database.Cursor;

import com.chshru.music.data.model.Song;

/**
 * Created by abc on 18-11-13.
 */

public interface SongHelper {

    void insert(Song song);

    void delete(Song song);

    void update(Song song);

    Cursor query();
}
