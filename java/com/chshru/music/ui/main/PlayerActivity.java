package com.chshru.music.ui.main;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chshru.music.R;
import com.chshru.music.base.MusicApp;
import com.chshru.music.manager.DataManager;
import com.chshru.music.service.MusicPlayer;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.view.PlayPauseButton;
import com.chshru.music.ui.view.visualizer.VisualizerView;
import com.chshru.music.ui.view.visualizer.renderer.BarGraphRenderer;
import com.chshru.music.ui.view.visualizer.renderer.CircleBarRenderer;
import com.chshru.music.ui.view.visualizer.renderer.CircleRenderer;
import com.chshru.music.ui.view.visualizer.renderer.LineRenderer;
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
    private SeekBar mSeekBar;
    private ImageButton mRepeat;

    private int[] RAND_PIC = {
            R.drawable.music_album_1,
            R.drawable.music_album_2,
            R.drawable.music_album_3,
            R.drawable.music_album_4,
            R.drawable.music_album_5,
            R.drawable.music_album_6,
            R.drawable.music_album_7,
            R.drawable.music_album_8,
            R.drawable.music_album_9,
    };

    private int[] REPEAT_PIC = {
            R.drawable.icon_repeat_all,
            R.drawable.icon_repeat_one,
            R.drawable.icon_repeat_rand,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transParentStatusBar();
        setContentView(R.layout.activity_player);
        overridePendingTransition(R.xml.bottom_in_ani, 0);
        initialize();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.xml.bottom_out_ani);
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
        mRepeat = findViewById(R.id.iv_repeat);
        mSeekBar = findViewById(R.id.progressSb);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mLove.setOnClickListener(view -> onLoveClick());
        mPause.setOnClickListener(view -> togglePlayer(true));
        mApp = (MusicApp) getApplication();
        mLoveTable = mApp.getLoveTable();
        mLoveList = mApp.getListData().getList(ListData.P_LOVE);
        mPlayer = mApp.getPlayer();
        mRepeat.setImageResource(REPEAT_PIC[mApp.getListData().getCurMode()]);
        mRepeat.setOnClickListener(view -> onRepeatClick());
        findViewById(R.id.prevPlayIv).setOnClickListener(view -> mPlayer.prev());
        findViewById(R.id.nextvPlayIv).setOnClickListener(view -> mPlayer.next());
        mPlayingAlbumBox = findViewById(R.id.playing_album);
        findViewById(R.id.playing_album).setOnTouchListener((v, event) -> {
            v.performClick();
            return onAlbumTouch(v, event);
        });
        createAnimator();
        int curPlayType = mApp.getDataManager().getAlbumType();
        if (curPlayType == DataManager.PLAYING_ALBUM_VIS) {
            initVisualizerAndEqualizer();
            mPlayingAlbumBox.removeView(mAlbumPic);
            mPlayingAlbumBox.addView(mVisualizerView);
        }
    }

    private void onRepeatClick() {
        int curMode = mApp.getListData().getCurMode();
        curMode = (curMode + 1) % ListData.MODE_COUNT;
        mApp.getListData().setCurMode(curMode);
        mApp.getDataManager().setMode(curMode);
        mRepeat.setImageResource(REPEAT_PIC[curMode]);
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        private boolean mPlaying;
        private int mProgress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!mPlayer.hasPrepare()) {
                return;
            }
            if (!fromUser) {
                return;
            }
            seekBar.setProgress(progress);
            mProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (!mPlayer.hasPrepare()) {
                return;
            }
            mProgress = -1;
            mHandler.removeMessages(MSG_FRESH_SEEKBAR);
            mPlaying = mPlayer.isPlaying();
            if (mPlaying) {
                mPlayer.togglePause();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!mPlayer.hasPrepare()) {
                return;
            }
            if (mProgress != -1) {
                mPlayer.seekTo(mProgress);
            }
            if (mPlaying && !mPlayer.isPlaying()) {
                mPlayer.togglePause();
            }
            mHandler.sendEmptyMessage(MSG_FRESH_SEEKBAR);
        }
    };

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
            ScaleAnimation sa = new ScaleAnimation(
                    1f, 1.3f, 1f, 1.3f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            sa.setDuration(300);
            mLove.setAnimation(null);
            mCurIsLove = true;
            mLoveTable.insert(song);
            mLove.setImageResource(R.drawable.icon_favorite_red);
            mLoveList.add(song);
            mLove.startAnimation(sa);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(MSG_FRESH_SEEKBAR);
        mPlayer.rmCallback(this);
        if (isFinishing()) {
            releaseVisualizerAndEqualizer();
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateUI(true);
    }

    @Override
    public void togglePlayer(boolean real) {
        if (!real) {
            updateUI(false);
            return;
        }
        mPlayer.togglePause();
    }

    private static final int MSG_FRESH_SEEKBAR = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_FRESH_SEEKBAR) {
                mSeekBar.setMax(mPlayer.getDuration());
                mSeekBar.setProgress(mPlayer.getCurDuration());
                sendEmptyMessageDelayed(MSG_FRESH_SEEKBAR, 500);
            }
        }
    };

    private RequestListener<Drawable> mLoadListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                    Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                       DataSource dataSource, boolean isFirstResource) {
            Drawable bg = ImageUtil.createBgFromBitmap(
                    ((BitmapDrawable) resource).getBitmap()
                    , 12, PlayerActivity.this
            );
            ImageUtil.startChangeAnimation(mPlayingBg, bg);
            return true;
        }
    };

    private void updateUI(boolean b) {
        Song song = mPlayer.getCurSong();
        if (song != null && b) {
            mHandler.removeMessages(MSG_FRESH_SEEKBAR);
            mSeekBar.setMax(mPlayer.getDuration());
            mSeekBar.setProgress(mPlayer.getCurDuration());
            mHandler.sendEmptyMessage(MSG_FRESH_SEEKBAR);
            if (song.album != null) {
                Glide.with(this).load(song.album).into(mAlbumPic);
                Glide.with(this).load(song.album).listener(mLoadListener).into(mPlayingBg);
            } else {
                int rand = (int) (Math.random() * 8);
                Glide.with(this).load(RAND_PIC[rand]).into(mAlbumPic);
                Glide.with(this).load(RAND_PIC[rand]).listener(mLoadListener).into(mPlayingBg);
            }
            mAnimator.end();
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
            mHandler.sendEmptyMessage(MSG_FRESH_SEEKBAR);
            if (!mAnimator.isStarted()) {
                mAnimator.start();
            }
            if (mAnimator.isPaused()) {
                mAnimator.resume();
            }
            if (!mPause.isPlaying()) {
                mPause.play();
            }
            if (mVisualizerView != null && !mVisualizerView.hasRenders()) {
                mVisualizerView.addRenderer(mBarGraphRenderer);
                mVisualizerView.addRenderer(mCircleBarRenderer);
                mVisualizerView.addRenderer(mCircleRenderer);
                mVisualizerView.addRenderer(mLineRenderer);
            }
        } else {
            mHandler.removeMessages(MSG_FRESH_SEEKBAR);
            if (mAnimator.isRunning()) {
                mAnimator.pause();
            }
            if (mPause.isPlaying()) {
                mPause.pause();
            }
            if (mVisualizerView != null && mVisualizerView.hasRenders()) {
                mVisualizerView.clearRenderers();
            }
        }
    }

    private void createAnimator() {
        mAnimator = ObjectAnimator.ofFloat(mAlbumPic, "rotation", 0f, 360f);
        mAnimator.setDuration(20 * 1000);
        mAnimator.setCurrentPlayTime(mPlayer.getCurDuration() % (20 * 1000));
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
    }


    private long mLastDownTime;

    private boolean onAlbumTouch(View v, MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - mLastDownTime <= 200) {
                switchPlayingAlbum();
                mLastDownTime = 0;
            }
            mLastDownTime = System.currentTimeMillis();
        }
        return true;
    }

    private void switchPlayingAlbum() {
        int curPlayType = mApp.getDataManager().getAlbumType();
        if (curPlayType == DataManager.PLAYING_ALBUM_PIC) {
            initVisualizerAndEqualizer();
            mApp.getDataManager().setAlbumType(DataManager.PLAYING_ALBUM_VIS);
            animRemoveAdd(mPlayingAlbumBox, mVisualizerView, mAlbumPic, null);
        } else {
            mApp.getDataManager().setAlbumType(DataManager.PLAYING_ALBUM_PIC);
            animRemoveAdd(mPlayingAlbumBox, mAlbumPic, mVisualizerView, this::releaseVisualizerAndEqualizer);
        }
    }

    private void animRemoveAdd(RelativeLayout father, View add, View remove, Runnable run) {
        AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
        aa.setDuration(500);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                father.removeView(remove);
                if (run != null) {
                    run.run();
                }
                AlphaAnimation aa1 = new AlphaAnimation(0.0f, 1.0f);
                aa1.setDuration(300);
                father.addView(add);
                add.startAnimation(aa1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        remove.startAnimation(aa);

    }

    ViewGroup.LayoutParams mVL;
    private RelativeLayout mPlayingAlbumBox;
    private Equalizer mEqualizer;
    private VisualizerView mVisualizerView;

    private BarGraphRenderer mBarGraphRenderer;
    private CircleBarRenderer mCircleBarRenderer;
    private CircleRenderer mCircleRenderer;
    private LineRenderer mLineRenderer;

    private void releaseVisualizerAndEqualizer() {
        if (mEqualizer != null) {
            mEqualizer.release();
        }
        if (mVisualizerView != null) {
            mVisualizerView.release();
        }
    }

    private void initVisualizerAndEqualizer() {
        mApp = (MusicApp) getApplication();
        //setupEqualizerFxAndUI();
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.create(mApp.getPlayer().getAudioSessionId());
        mVL = new ViewGroup.LayoutParams(-1, -1);
        mVisualizerView.setLayoutParams(mVL);
        if (mBarGraphRenderer == null) {
            mBarGraphRenderer = new BarGraphRenderer();
        }
        if (mCircleBarRenderer == null) {
            mCircleBarRenderer = new CircleBarRenderer();
        }
        if (mCircleRenderer == null) {
            mCircleRenderer = new CircleRenderer();
        }
        if (mLineRenderer == null) {
            mLineRenderer = new LineRenderer();
        }
        if (mVisualizerView != null && !mVisualizerView.hasRenders()) {
            mVisualizerView.addRenderer(mBarGraphRenderer);
            mVisualizerView.addRenderer(mCircleBarRenderer);
            mVisualizerView.addRenderer(mCircleRenderer);
            mVisualizerView.addRenderer(mLineRenderer);
        }
    }

    private void setupEqualizerFxAndUI() {
        mEqualizer = new Equalizer(0, mApp.getPlayer().getAudioSessionId());
        mEqualizer.setEnabled(true);
    }
}
