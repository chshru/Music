package com.chshru.music.ui.main;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    private BottomController mController;


    public BottomLayout(View root, StatusCallback callback) {
        mCallback = callback;
        mPause = root.findViewById(R.id.play_pause);
        mNext = root.findViewById(R.id.play_next);
        mTitle = root.findViewById(R.id.bottom_title);
        mArtist = root.findViewById(R.id.bottom_artist);
        mAlbum = root.findViewById(R.id.bottom_album);
        root.findViewById(R.id.bottom_linear).setOnClickListener(
                v -> mCallback.getApplicationContext().startActivity(
                        new Intent(mCallback.getApplicationContext(),
                                PlayerActivity.class)
                )
        );
        mRoot = root;
    }

    public void setController(BottomController controller) {
        mController = controller;
        mPause.setOnClickListener(v -> mController.onPauseClick());
        mRoot.findViewById(R.id.play_next).setOnClickListener(
                view -> mController.onNextClick());
    }

    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.default_album_cover)
            .error(R.drawable.default_album_cover);

    public void freshLayout(boolean playing, Song song) {
        if (song != null) {
            mTitle.setText(song.title);
            mArtist.setText(song.artist);
            Glide.with(mCallback
                    .getApplicationContext())
                    .load(song.album)
                    .apply(options)
                    .into(mAlbum);
        }
        if (playing && !mPause.isPlaying()) {
            mPause.play();
        } else if (!playing && mPause.isPlaying()) {
            mPause.pause();
        }
    }

    public interface BottomController {
        void onPauseClick();

        void onNextClick();
    }

}
