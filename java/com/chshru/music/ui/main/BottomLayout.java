package com.chshru.music.ui.main;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.view.PlayPauseButton;
import com.chshru.music.util.Song;

/**
 * Created by abc on 18-10-25.
 */

public class BottomLayout {

    private PlayPauseButton mPause;
    private ImageView mAlbum;
    private TextView mTitle;
    private TextView mArtist;
    private ImageButton mNext;
    private StatusCallback mCallback;
    private View mRoot;


    public BottomLayout(View root, StatusCallback callback) {
        mCallback = callback;
        mPause = root.findViewById(R.id.play_pause);
        mNext = root.findViewById(R.id.play_next);
        mTitle = root.findViewById(R.id.bottom_title);
        mArtist = root.findViewById(R.id.bottom_artist);
        mAlbum = root.findViewById(R.id.bottom_album);
        mPause.setOnClickListener(v -> mCallback.togglePlayer());
        mRoot = root;

    }


    public void freshLayout(boolean playing, Song song) {
        if (song != null) {
            mTitle.setText(song.title);
            mArtist.setText(song.artist);
        }
        if (playing && !mPause.isPlaying()) {
            mPause.play();
        } else if (!playing && mPause.isPlaying()) {
            mPause.pause();
        }
    }

}
