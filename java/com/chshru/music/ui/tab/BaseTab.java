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
    private View mView;

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

    public void setView(View view) {
        mView = view;
    }

    public boolean hasInit() {
        return mView != null;
    }

    public View getView() {
        return mView;
    }

    protected abstract void initialize();

    public abstract void initChild(View root);
}
