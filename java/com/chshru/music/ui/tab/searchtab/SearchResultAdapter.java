package com.chshru.music.ui.tab.searchtab;

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


import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class SearchResultAdapter extends RecyclerView.Adapter {


    private List<Song> mSong;
    private OnItemClickListener mClickListener;

    public SearchResultAdapter(List<Song> list) {
        mSong = list;
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
        Song song = mSong.get(pos);
        h.name.setText(song.title);
        h.artist.setText(song.artist);
        String url = QQMusicApi.buildAlbumUrl(song.album);
        h.album.setImageUrl(url);
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public static class OnItemClickListener {
        void onItemClick(View v) {

        }

        void OnItemLongClick(View v) {

        }
    }

    private static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView artist;
        public AlbumImage album;


        public Holder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            artist = view.findViewById(R.id.tv_artist);
            album = view.findViewById(R.id.iv_cover);
        }
    }
}
