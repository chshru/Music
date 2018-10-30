package com.chshru.music.ui.tab;

import android.content.Context;
import android.view.View;

import com.chshru.music.service.StatusCallback;

/**
 * Created by abc on 18-10-25.
 */

public abstract class BaseTab {

    protected int mLayoutId;
    protected int mTitleTd;
    protected StatusCallback mCallback;
    protected Context mContext;

    public BaseTab(StatusCallback callback, int resId) {
        mCallback = callback;
        mLayoutId = resId;
        mContext = callback.getApplicationContext();
        initialize();
    }

    public abstract void freshChild();

    public int getResId() {
        return mLayoutId;
    }

    public int getTitleId() {
        return mTitleTd;
    }


    protected abstract void initialize();

    public abstract void initChild(Context context, View root);
}
