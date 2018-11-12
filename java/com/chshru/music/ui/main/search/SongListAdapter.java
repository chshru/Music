package com.chshru.music.ui.main.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chshru.music.R;
import com.chshru.music.util.Song;

import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class SongListAdapter extends RecyclerView.Adapter {

    private List<Song> mSong;
    private OnItemClickListener mClickListener;

    public SongListAdapter(List<Song> list) {
        mSong = list;
    }

    public void addAll(List<Song> list) {
        mSong.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mSong.clear();
    }

    public Song get(int pos) {
        return mSong.get(pos);
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

    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.default_album_cover)
            .error(R.drawable.default_album_cover);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        Holder h = (Holder) holder;
        Song song = mSong.get(pos);

        Glide.with(h.itemView.getContext())
                .load(song.album)
                .apply(options)
                .into(h.album);

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
        public ImageView album;
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
