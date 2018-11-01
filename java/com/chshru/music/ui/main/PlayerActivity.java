package com.chshru.music.ui.main;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chshru.music.R;
import com.chshru.music.base.MusicApp;
import com.chshru.music.util.ImageUtil;
import com.chshru.music.util.Song;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abc on 18-11-1.
 */

public class PlayerActivity extends Activity {

    private CircleImageView mAlbumPic;
    private ImageView mPlayingBg;
    private MusicApp mApp;

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

    private void initialize() {
        mAlbumPic = findViewById(R.id.civ_cover);
        mPlayingBg = findViewById(R.id.playingBgIv);
        mApp = (MusicApp) getApplication();
        Song song = mApp.getPlayer().getCurSong();
        if (song != null && song.albumBitmap != null) {
            mAlbumPic.setImageBitmap(song.albumBitmap);
            Drawable bg = ImageUtil.createBgFromBitmap(song.albumBitmap, 12, this);
            ImageUtil.startChangeAnimation(mPlayingBg, bg);
        }


    }
}
