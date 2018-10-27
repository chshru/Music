package com.chshru.music.ui.tab.localtab;


import android.content.Context;
import android.view.View;

import com.chshru.music.R;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.tab.BaseTab;

/**
 * Created by abc on 18-10-25.
 */

public class LocalTab extends BaseTab {



    public LocalTab(StatusCallback callback, int resId) {
        super(callback, resId);
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.local_tab_title;
    }

    @Override
    public void initChild(Context context, View root) {

    }
}
