package com.chshru.music.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongScanner {

    private Context mContext;
    private List<Song> mList;
    private ChinaInitial mChina;
    private Handler mHandler;
    private Runnable mRunnable;

    public SongScanner(Context context, List<Song> list) {
        mChina = new ChinaInitial();
        mContext = context;
        mList = list;
    }


    public void setCallback(Handler handler, Runnable runnable) {
        mHandler = handler;
        mRunnable = runnable;
    }

    public void startScan() {
        mList.clear();
        new Thread(() -> {
            Cursor cursor = mContext.getContentResolver().query(
                    Media.EXTERNAL_CONTENT_URI,
                    null, null, null, null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                    String link = cursor.getString(cursor.getColumnIndex(Media.DATA));
                    String album = cursor.getString(cursor.getColumnIndex(Media.ALBUM_ID));
                    int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
                    int isMusic = cursor.getInt(cursor.getColumnIndex(Media.IS_MUSIC));
                    if (isMusic != 0 && duration > 60000) {
                        Song song = new Song(
                                Integer.valueOf(id),
                                Song.TYPE_LOCAL,
                                null,
                                title,
                                artist,
                                link,
                                LocalMusicApi.getAlbumArtFromId(album, mContext)
                        );
                        mList.add(song);
                    }
                }
            }
            Collections.sort(mList, cmp);
            if (cursor != null) {
                cursor.close();
            }
            if (mHandler != null) {
                mHandler.post(mRunnable);
            }
        }).start();
    }

    private Comparator<Song> cmp = (o1, o2) -> {
        String str1 = o1.title;
        String str2 = o2.title;
        if (isEnglish(str1) && isEnglish(str2))
            return str1.compareTo(str2);
        else if (!isEnglish(str1) && !isEnglish(str2)) {
            String[] str = {str1, str2};
            Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
            Arrays.sort(str, com);
            if (o1.title.equals(str[0]))
                return -1;
            return 1;
        } else {
            if (!isEnglish(str1))
                str1 = getHeadChar(str1);
            if (!isEnglish(str2))
                str2 = getHeadChar(str2);
            return str1.compareTo(str2);
        }
    };

    private boolean isEnglish(String str) {
        char[] a = str.toCharArray();
        return (a[0] >= 'a' && a[0] <= 'z') || (a[0] >= 'A' && a[0] <= 'Z');
    }

    private String getHeadChar(String str) {
        return mChina.getPyHeadStr(str, true);
    }
}
