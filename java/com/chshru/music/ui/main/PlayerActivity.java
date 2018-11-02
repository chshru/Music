package com.chshru.music.ui.main;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.base.MusicApp;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.view.PlayPauseButton;
import com.chshru.music.util.ImageUtil;
import com.chshru.music.util.LoveTable;
import com.chshru.music.util.Song;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abc on 18-11-1.
 */

public class PlayerActivity extends Activity implements StatusCallback {

    private CircleImageView mAlbumPic;
    private ImageView mPlayingBg;
    private MusicApp mApp;
    private ObjectAnimator mAnimator;
    private MusicPlayer mPlayer;
    private PlayPauseButton mPause;
    private TextView mTitle;
    private TextView mArtist;
    private ImageButton mLove;
    private List<Song> mLoveList;
    private boolean mCurIsLove;
    private LoveTable mLoveTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transParentStatusBar();
        setContentView(R.layout.activity_player);
        initialize();
    }

    private void transParentStatusBar() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    private boolean isLove(Song song) {
        for (Song s : mLoveList) {
            if (s.equals(song)) {
                return true;
            }
        }
        return false;
    }

    private void initialize() {
        mAlbumPic = findViewById(R.id.civ_cover);
        mPlayingBg = findViewById(R.id.playingBgIv);
        mPause = findViewById(R.id.playPauseIv);
        mTitle = findViewById(R.id.tv_title);
        mArtist = findViewById(R.id.tv_artist);
        mLove = findViewById(R.id.iv_love);
        mLove.setOnClickListener(view -> onLoveClick());
        mPause.setOnClickListener(view -> togglePlayer(true));
        mApp = (MusicApp) getApplication();
        mLoveTable = mApp.getLoveTable();
        mLoveList = mApp.getListData().getList(ListData.P_LOVE);
        mPlayer = mApp.getPlayer();
        createAnimator();
        mPlayer.addCallback(this);
    }

    private void onLoveClick() {
        Song song = mPlayer.getCurSong();
        if (song == null) {
            return;
        }
        if (mCurIsLove) {
            mCurIsLove = false;
            mLoveTable.delete(song);
            mLove.setImageResource(R.drawable.icon_favorite_white);
            for (Song s : mLoveList) {
                if (s.equals(song)) {
                    mLoveList.remove(s);
                    break;
                }
            }
        } else {
            mCurIsLove = true;
            mLoveTable.insert(song);
            mLove.setImageResource(R.drawable.icon_favorite_red);
            mLoveList.add(song);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateUI();
    }

    @Override
    public void togglePlayer(boolean real) {
        if (!real) {
            updateUI();
            return;
        }
        mPlayer.togglePause();
    }

    private void updateUI() {
        Song song = mPlayer.getCurSong();
        if (song != null && song.albumBitmap != null) {
            mAlbumPic.setImageBitmap(song.albumBitmap);
            Drawable bg = ImageUtil.createBgFromBitmap(song.albumBitmap, 12, this);
            ImageUtil.startChangeAnimation(mPlayingBg, bg);
        }
        if (song != null) {
            mTitle.setText(song.title);
            mArtist.setText(song.artist);
            if (isLove(song)) {
                mCurIsLove = true;
                mLove.setImageResource(R.drawable.icon_favorite_red);
            } else {
                mCurIsLove = false;
                mLove.setImageResource(R.drawable.icon_favorite_white);
            }
        }
        if (mPlayer.isPlaying()) {

            if (!mAnimator.isStarted()) {
                mAnimator.start();
            }
            if (mAnimator.isPaused()) {
                mAnimator.resume();
            }
            mPause.play();
        } else {
            if (mAnimator.isRunning()) {
                mAnimator.pause();
            }
            mPause.pause();
        }
    }

    private void createAnimator() {
        mAnimator = ObjectAnimator.ofFloat(mAlbumPic, "rotation", 0F, 359F);
        mAnimator.setDuration(20 * 1000);
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

}
