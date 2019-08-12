package com.chshru.music.ui.main.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chshru.music.R;
import com.chshru.music.data.model.Song;
import com.chshru.music.ui.main.listener.OnItemClickListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class DelAdapter extends RecyclerView.Adapter {

    private List<Song> mSong;
    private List<Song> mSelect;
    private OnItemClickListener mClickListener;

    public DelAdapter(List<Song> list) {
        mSong = list;
        mSelect = new LinkedList<>();
        for (int i = 0; i < mSong.size(); i++) {
            mSelect.add(new Song(mSong.get(i)));
            mSelect.get(i).playing = false;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_edit, parent, false);
        final Holder holder = new Holder(view);
        view.setOnClickListener(v -> mClickListener.onItemClick(v));
        return holder;
    }

    public boolean isCheck(int pos) {
        return mSelect.get(pos).playing;
    }

    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.default_album_cover)
            .error(R.drawable.default_album_cover);

    public List<Song> getSelected() {
        return mSelect;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        Holder h = (Holder) holder;
        Song song = mSong.get(pos);

        Glide.with(h.itemView.getContext())
                .load(song.album)
                .apply(options)
                .into(h.album);
        h.name.setText(song.title);
        h.artist.setText(String.format("%s - %s", song.artist, song.albumName));
        if (song.playing) {
            h.name.setTextColor(0xff05b962);
            h.artist.setTextColor(0xff05b962);
        } else {
            h.name.setTextColor(0xff000000);
            h.artist.setTextColor(0xff9e9e9e);
        }
        h.check.setChecked(mSelect.get(pos).playing);
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    private static class Holder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView artist;
        public ImageView album;
        public CheckBox check;

        public Holder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            artist = view.findViewById(R.id.tv_artist);
            album = view.findViewById(R.id.iv_cover);
            check = view.findViewById(R.id.cb_select);
        }

    }
}

