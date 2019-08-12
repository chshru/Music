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
import com.chshru.music.data.model.Song;
import com.chshru.music.ui.main.listener.OnItemClickListener;
import com.chshru.music.ui.view.RotateLoading;

import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class SearchAdapter extends RecyclerView.Adapter {

    private List<Song> mSong;
    private OnItemClickListener mClickListener;
    private FooterHolder mFooter;
    private Song mFooterSong;

    public SearchAdapter(List<Song> list) {
        mSong = list;
        mFooterSong = new Song(-1, -1, null, null, null, null, null, null);
    }

    public void addAll(List<Song> list) {
        if (mSong.isEmpty()) {
            mSong.addAll(list);
            mSong.add(mFooterSong);
        } else {
            mSong.remove(mFooterSong);
            mSong.addAll(list);
            mSong.add(mFooterSong);
        }
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
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_song, parent, false);
            final ItemHolder itemHolder = new ItemHolder(view);
            if (mClickListener != null) {
                view.setOnClickListener(v -> mClickListener.onItemClick(v));
                view.setOnLongClickListener(v -> {mClickListener.OnItemLongClick(v);return true;});
            }
            return itemHolder;
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_footer, parent, false);
            final FooterHolder footerHolder = new FooterHolder(v);
            mFooter = footerHolder;
            return footerHolder;
        }
    }

    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.default_album_cover)
            .error(R.drawable.default_album_cover);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder instanceof ItemHolder) {
            ItemHolder h = (ItemHolder) holder;
            Song song = mSong.get(pos);

            Glide.with(h.itemView.getContext())
                    .load(song.album)
                    .apply(options)
                    .into(h.album);
            h.name.setText(song.title);
            h.artist.setText(String.format("%s - %s", song.artist, song.albumName));
            h.num.setText(String.valueOf(pos + 1));
            if (song.playing) {
                h.playing.setVisibility(View.VISIBLE);
                h.name.setTextColor(0xff05b962);
                h.artist.setTextColor(0xff05b962);
                h.num.setTextColor(0xff05b962);
            } else {
                h.playing.setVisibility(View.INVISIBLE);
                h.name.setTextColor(0xff000000);
                h.artist.setTextColor(0xff9e9e9e);
                h.num.setTextColor(0xff9e9e9e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSong.size();
    }


    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private int mLoadState;

    public static final int STATE_LOADING = 0;
    public static final int STATE_NO_MORE = 1;

    public void setLoadState(int state) {
        mLoadState = state;
        if (mLoadState == STATE_NO_MORE) {
            mFooter.rotate.setVisibility(View.GONE);
            mFooter.tip.setVisibility(View.VISIBLE);
            mFooter.rotate.stop();
        } else if (mLoadState == STATE_LOADING) {
            mFooter.rotate.setVisibility(View.VISIBLE);
            mFooter.tip.setVisibility(View.GONE);
            mFooter.rotate.start();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView artist;
        public ImageView album;
        public View playing;
        public TextView num;

        public ItemHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            artist = view.findViewById(R.id.tv_artist);
            album = view.findViewById(R.id.iv_cover);
            playing = view.findViewById(R.id.v_playing);
            num = view.findViewById(R.id.tv_num);
        }

    }

    private static class FooterHolder extends RecyclerView.ViewHolder {

        public RotateLoading rotate;
        public TextView tip;

        public FooterHolder(View view) {
            super(view);
            rotate = view.findViewById(R.id.footer_rotate);
            tip = view.findViewById(R.id.tips);
        }
    }
}
