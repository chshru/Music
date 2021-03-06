package com.chshru.music.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;

import com.chshru.music.data.model.OnlineList;
import com.chshru.music.data.model.Song;
import com.chshru.music.data.sql.DataHelper;
import com.chshru.music.ui.main.list.ListData;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongScanner {

    private Context mContext;
    private List<Song> mLocalList;
    private List<Song> mHistoryList;
    private List<Song> mLoveList;
    private ChinaInitial mChina;
    private Handler mHandler;
    private Runnable mRunnable;

    public SongScanner(Context context) {
        mChina = new ChinaInitial();
        mContext = context;
    }

    public void setLocalList(List<Song> localList) {
        mLocalList = localList;
    }

    public void setHistoryList(List<Song> historyList) {
        mHistoryList = historyList;
    }

    public void setLoveList(List<Song> loveList) {
        mLoveList = loveList;
    }

    public void setCallback(Handler handler, Runnable runnable) {
        mHandler = handler;
        mRunnable = runnable;
    }

    public void startLoveScan(DataHelper.LoveHelper helper) {
        Cursor cursor = helper.query();
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String album = cursor.getString(2);
                String albumName = cursor.getString(3);
                String mid = cursor.getString(4);
                String title = cursor.getString(5);
                String artist = cursor.getString(6);
                String link = cursor.getString(7);
                String time = cursor.getString(8);
                Song song = new Song(id, type, album, albumName, mid, title, artist, link);
                song.time = time;
                mLoveList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (mHandler != null && mRunnable != null) {
            mHandler.post(mRunnable);
        }
    }

    public void startHistoryScan(DataHelper.HistoryHelper helper) {
        Cursor cursor = helper.query();
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String album = cursor.getString(2);
                String albumName = cursor.getString(3);
                String mid = cursor.getString(4);
                String title = cursor.getString(5);
                String artist = cursor.getString(6);
                String link = cursor.getString(7);
                String time = cursor.getString(8);
                Song song = new Song(id, type, album, albumName, mid, title, artist, link);
                song.time = time;
                mHistoryList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(mHistoryList, cmpByTime);
        if (mHandler != null && mRunnable != null) {
            mHandler.post(mRunnable);
        }
    }

    public void startLocalScan() {
        mLocalList.clear();
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
                    String albumName = cursor.getString(cursor.getColumnIndex(Media.ALBUM));
                    int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
                    int isMusic = cursor.getInt(cursor.getColumnIndex(Media.IS_MUSIC));
                    if (isMusic != 0 && duration > 60000) {
                        Song song = new Song(
                                Integer.valueOf(id),
                                Song.TYPE_LOCAL,
                                LocalMusicApi.getAlbumArtFromId(album, mContext),
                                albumName,
                                null,
                                title,
                                artist,
                                link
                        );
                        mLocalList.add(song);
                    }
                }
            }
            Collections.sort(mLocalList, cmpByTitle);
            if (cursor != null) {
                cursor.close();
            }
            if (mHandler != null && mRunnable != null) {
                mHandler.post(mRunnable);
            }
        }).start();
    }

    private Comparator<Song> cmpByTime = (o1, o2) -> {
        if (o2.time != null && o1.time != null) {
            return o2.time.compareTo(o1.time);
        }
        return 0;
    };

    private Comparator<Song> cmpByTitle = (o1, o2) -> {
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

    private List<OnlineList> mOnlineList;

    public void setOnlineList(List<OnlineList> list) {
        this.mOnlineList = list;
    }

    public void scanOnlineList(DataHelper.OnlineSongHelper songHelper, DataHelper.OnlineListHelper listHelper,
                               ListData listData) {
        Cursor cursor = listHelper.query();
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                String listId = cursor.getString(0);
                String listName = cursor.getString(1);
                String listLogo = cursor.getString(2);
                String listNum = cursor.getString(3);
                mOnlineList.add(new OnlineList(listId, listName, listLogo, listNum));
            } while (cursor.moveToNext());
            if (!mOnlineList.isEmpty()) {
                for (OnlineList list : mOnlineList) {
                    Cursor c = songHelper.query(list.id);
                    c.moveToFirst();
                    if (c.getCount() != 0) {
                        List<Song> tempList = new ArrayList<>();
                        do {
                            int id = c.getInt(0);
                            int type = c.getInt(1);
                            String album = c.getString(2);
                            String albumName = c.getString(3);
                            String mid = c.getString(4);
                            String title = c.getString(5);
                            String artist = c.getString(6);
                            String link = c.getString(7);
                            Song song = new Song(id, type, album, albumName, mid, title, artist, link);
                            tempList.add(song);
                        } while (c.moveToNext());
                        listData.addOnlineList(list.id, tempList);
                    }
                    c.close();
                }
            }
        }
        cursor.close();
    }
}
