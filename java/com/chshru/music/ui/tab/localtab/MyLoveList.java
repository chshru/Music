package com.chshru.music.ui.tab.localtab;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.util.Song;

import java.util.List;

public class MyLoveList {

    private Context mContext;
    private View mRoot;
    private List<Song> mList;
    private TextView mCount;

    MyLoveList(Context context, View root, List<Song> list) {
        mContext = context;
        mRoot = root;
        mList = list;
        initialize();
    }

    private void initialize() {
        TextView listName = mRoot.findViewById(R.id.tv_name);
        listName.setText(R.string.my_love);
        mCount = mRoot.findViewById(R.id.tv_desc);
        mCount.setVisibility(View.INVISIBLE);

    }

    public void freshCount() {
        mCount.setText(mContext.getString(R.string.shou, mList.size()));
        mCount.setVisibility(View.VISIBLE);
    }
}
