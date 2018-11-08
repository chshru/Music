package com.chshru.music.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class LocalMusicApi {

    private static final String URI_ALBUM = "content://media/external/audio/albums";

    public static String getAlbumArtFromId(String id, Context context) {

        String[] projection = new String[]{"album_art"};
        Cursor cursor = context.getContentResolver().query(
                Uri.parse(URI_ALBUM + "/" + id),
                projection, null, null, null);
        String album_art = null;
        if (cursor != null && cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            album_art = cursor.getString(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return album_art;
    }


}
