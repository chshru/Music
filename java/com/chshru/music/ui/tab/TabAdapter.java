package com.chshru.music.ui.tab;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by abc on 18-10-25.
 */

public class TabAdapter extends PagerAdapter {

    private BaseTab[] mPage;
    private Context mContext;

    public TabAdapter(Context context, BaseTab[] pages) {
        mContext = context;
        mPage = pages;

    }

    @Override
    public int getCount() {
        return mPage.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int p) {
        if (!mPage[p].hasInit()) {
            View child = View.inflate(
                    mContext,
                    mPage[p].getResId(),
                    null
            );
            mPage[p].setView(child);
            mPage[p].initChild(child);
        }
        parent.addView(mPage[p].getView());
        return mPage[p].getView();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup parent, int pos, @NonNull Object obj) {
        parent.removeView(mPage[pos].getView());
    }

    @Override
    public CharSequence getPageTitle(int p) {
        int id = mPage[p].getTitleId();
        return mContext.getResources().getString(id);
    }
}
