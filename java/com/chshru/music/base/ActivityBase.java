package com.chshru.music.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by abc on 18-10-22.
 */

public abstract class ActivityBase extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //transParentStatusBar(this);
        initialize();
    }

    private void transParentStatusBar(Activity aty) {
        aty.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        aty.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        aty.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();

}
