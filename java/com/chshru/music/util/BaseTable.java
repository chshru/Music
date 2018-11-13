package com.chshru.music.util;

import android.database.Cursor;

/**
 * Created by abc on 18-11-13.
 */

public interface BaseTable {

    void insert(Song song);

    void delete(Song song);

    void update(Song song);

    Cursor query();
}
