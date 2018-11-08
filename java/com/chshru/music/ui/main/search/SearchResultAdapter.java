package com.chshru.music.ui.main.search;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.ui.view.AlbumImage;
import com.chshru.music.util.QQMusicApi;
import com.chshru.music.util.Song;
import com.danikula.videocache.HttpProxyCacheServer;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by abc on 18-10-25.
 */

public class SearchResultAdapter extends RecyclerView.Adapter {

    private HttpProxyCacheServer mCacheServer;
    private List<Song> mSong;
    private Queue<Song> mCacheQueue;
    private OnItemClickListener mClickListener;
    private LoadThread mThread;
    private Handler mHandler;
    private ItemRunnable mItemRunnable;

    public SearchResultAdapter(List<Song> list, Looper main) {
        mSong = list;
        mHandler = new Handler(main);
        mCacheQueue = new LinkedList<>(list);
        mThread = new LoadThread();
        mItemRunnable = new ItemRunnable();
    }

    public void setCacheServer(HttpProxyCacheServer cacheServer) {
        mCacheServer = cacheServer;
    }

    public void startLoad() {
//        if (mThread != null && !mThread.hasRun()) {
//            mThread.start();
//        } else if (mThread != null && !mThread.isAlive()) {
//            mThread = new LoadThread();
//            mThread.start();
//        }
    }

    public void addAll(List<Song> list) {
        mSong.addAll(list);
        notifyDataSetChanged();
        mCacheQueue.addAll(list);
        resetSongTimes(mCacheQueue);
//        if (mThread != null && !mThread.hasRun()) {
//            mThread.start();
//        } else if (mThread != null && !mThread.isAlive()) {
//            mThread = new LoadThread();
//            mThread.start();
//        }
    }

    private void resetSongTimes(Queue<Song> list) {
        for (Song song : list) {
            song.loadTimes = 0;
        }
    }

    public void clear() {
        mSong.clear();
    }

    public Song get(int pos) {
        return mSong.get(pos);
    }

    public void notifyDataDelayed(int time) {
        mHandler.postDelayed(mFreshRunnable, time);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_song, parent, false);
        final Holder holder = new Holder(v);
        v.setOnClickListener(mOnClick);
        v.setOnLongClickListener(mOnLongClick);
        return holder;
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v);
            }
        }
    };

    private View.OnLongClickListener mOnLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                mClickListener.OnItemLongClick(v);
            }
            return true;
        }
    };

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        Holder h = (Holder) holder;
        Song song = mSong.get(pos);
        if (song.id == -1) {
            mSong.remove(pos);
            return;
        }
//        if (song.albumBitmap != null) {
//            h.album.setImageBitmap(song.albumBitmap);
//        } else {
//            h.album.setImageResource(R.drawable.default_album_cover);
//        }

        if (song.playing) {
            h.playing.setVisibility(View.VISIBLE);
            h.name.setText(song.title);
            h.name.setTextColor(0xff05b962);
            h.artist.setText(song.artist);
            h.artist.setTextColor(0xff05b962);
            h.num.setText(String.valueOf(pos + 1));
            h.num.setTextColor(0xff05b962);
        } else {
            h.playing.setVisibility(View.INVISIBLE);
            h.name.setText(song.title);
            h.name.setTextColor(0xff000000);
            h.artist.setText(song.artist);
            h.artist.setTextColor(0xff9e9e9e);
            h.num.setText(String.valueOf(pos + 1));
            h.num.setTextColor(0xff9e9e9e);
        }
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }

    public void stopLoad() {
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
        }
    }

    private class LoadThread extends Thread {

        private boolean hasRun;

        public LoadThread() {
            super();
            hasRun = false;
        }

        @Override
        public synchronized void start() {
            super.start();
            hasRun = false;
        }

        public boolean hasRun() {
            return hasRun;
        }

        @Override
        public void run() {
            hasRun = true;
            while (mCacheQueue.peek() != null) {
                Song song = mCacheQueue.poll();
                if (song.albumBitmap != null
                        || song.id == -1
                        || song.loadTimes >= Song.MAX_LOAD_TIMES) {
                    continue;
                }
                try {
                    if (song.album != null && song.album.length() > 15) {
                        song.albumBitmap = BitmapFactory.decodeFile(song.album);
                    } else if (song.album != null) {
                        solveNetUrl(QQMusicApi.buildAlbumUrl(song.album), song);
                    }
                } catch (Exception e) {
                    System.out.println("failed load : " + song.title);
                    song.loadTimes++;
                    mCacheQueue.offer(song);
                }
                int pos = mSong.indexOf(song);
                mItemRunnable.setPos(pos);
                mHandler.post(mItemRunnable);
            }
            mHandler.post(mFreshRunnable);
        }

        private void solveNetUrl(String str, Song song) throws Exception {
            String local = null;
            String link = str;
            if (mCacheServer != null) {
                local = mCacheServer.getProxyUrl(str);
            }
            if (local == null || local.contains("http")) {
                if (local != null) {
                    link = local;
                }
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    song.albumBitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    mCacheQueue.offer(song);
                }
            } else {
                local = local.replace("file://", "");
                song.albumBitmap = BitmapFactory.decodeFile(local);
            }
        }
    }

    private class ItemRunnable implements Runnable {

        private int pos;

        public void setPos(int pos) {
            this.pos = pos;
        }

        @Override
        public void run() {

        }
    }

    private Runnable mFreshRunnable = this::notifyDataSetChanged;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public static class OnItemClickListener {
        public void onItemClick(View v) {
        }

        public void OnItemLongClick(View v) {
        }
    }

    private static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView artist;
        public AlbumImage album;
        public View playing;
        public TextView num;

        public Holder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            artist = view.findViewById(R.id.tv_artist);
            album = view.findViewById(R.id.iv_cover);
            playing = view.findViewById(R.id.v_playing);
            num = view.findViewById(R.id.tv_num);
        }

    }
}
