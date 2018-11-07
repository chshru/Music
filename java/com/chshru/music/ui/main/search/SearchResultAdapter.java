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


import java.io.IOException;
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

    public SearchResultAdapter(List<Song> list, Looper main) {
        mSong = list;
        mHandler = new Handler(main);
        mCacheQueue = new LinkedList<>(list);
        resetSongTimes(mCacheQueue);
        mThread = new LoadThread();
        mThread.start();
    }

    public void setCacheServer(HttpProxyCacheServer cacheServer) {
        mCacheServer = cacheServer;
    }

    public void addAll(List<Song> list) {
        mSong.addAll(list);
        mCacheQueue.addAll(list);
        resetSongTimes(mCacheQueue);
        if (mThread != null && !mThread.isAlive()) {
            mThread = new LoadThread();
            mThread.start();
        }
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
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        Holder h = (Holder) holder;
        if (!h.itemView.hasOnClickListeners()) {
            h.itemView.setOnClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.onItemClick(v);
                }
            });
            h.itemView.setOnLongClickListener(v -> {
                if (mClickListener != null) {
                    mClickListener.OnItemLongClick(v);
                }
                return true;
            });
        }
        Song song = mSong.get(pos);
        h.name.setText(song.title);
        h.artist.setText(song.artist);
        if (song.albumBitmap != null) {
            h.album.setImageBitmap(song.albumBitmap);
        } else {
            h.album.setImageResource(R.drawable.default_album_cover);
        }

        if (song.playing) {
            h.playing.setVisibility(View.VISIBLE);
        } else {
            h.playing.setVisibility(View.INVISIBLE);
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

        @Override
        public void run() {
            while (mCacheQueue.peek() != null) {

                Song song = mCacheQueue.poll();
                if (song.albumBitmap != null) {
                    continue;
                }
                if (song.loadTimes >= Song.MAX_LOAD_TIMES) {
                    continue;
                }
                try {
                    if (song.album != null && song.album.length() > 15) {
                        song.albumBitmap = BitmapFactory.decodeFile(song.album);
                    } else if (song.album != null) {
                        solveNetUrl(QQMusicApi.buildAlbumUrl(song.album), song);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    song.loadTimes++;
                    mCacheQueue.offer(song);
                }
            }
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
                    mHandler.post(mFreshRunnable);
                } else {
                    mCacheQueue.offer(song);
                }
            } else {
                local = local.replace("file://", "");
                song.albumBitmap = BitmapFactory.decodeFile(local);
                mHandler.post(mFreshRunnable);
            }
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

        public Holder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            artist = view.findViewById(R.id.tv_artist);
            album = view.findViewById(R.id.iv_cover);
            playing = view.findViewById(R.id.v_playing);
        }
    }
}
