package com.chshru.music.ui.tab.localtab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chshru.music.R;
import com.chshru.music.base.MusicApp;
import com.chshru.music.data.model.OnlineList;
import com.chshru.music.service.StatusCallback;
import com.chshru.music.ui.main.list.ListActivity;
import com.chshru.music.ui.main.list.ListData;
import com.chshru.music.ui.tab.BaseTab;
import com.chshru.music.data.model.Song;
import com.chshru.music.util.ListQueryHandler;
import com.chshru.music.util.QQMusicApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 18-10-25.
 */

public class LocalTab extends BaseTab {

    public static final String STARY_TYPE = "start_type";
    public static final String STARY_TITLE = "start_title";

    private LocalSongList mLocalSong;
    private HistoryList mHistory;
    private MyLoveList mLoveList;
    private Handler mHandler;
    private MusicApp app;
    private ListQueryHandler mListQueryHandler;
    private AlertDialog mAddListDialog;
    private EditText mSongListIdInput;
    private LinearLayout mOnlinePart;
    private List<CardView> mOnlineCard;

    public LocalTab(StatusCallback callback, int resId) {
        super(callback, resId);
        mHandler = new Handler(mCallback.getMainLooper());
        mOnlineCard = new ArrayList<>();
        mListQueryHandler = new ListQueryHandler(mCallback.getMainLooper(), mListQueryListener);
    }

    private ListQueryHandler.OnFinishListener mListQueryListener = this::onListQueryFinish;

    private void onListQueryFinish(String result) {
        if (result != null && !result.isEmpty()) {
            OnlineList newList = QQMusicApi.getOnlineListFromResult(result);
            List<Song> list = QQMusicApi.getOnlineSongForListFromResult(result);
            app.getListData().addOnlineList(newList.id, list);
            app.getListData().getOnlineList().add(newList);
            app.getHelper().getOnlineList().insert(newList);
            for (Song song : list) {
                app.getHelper().getOnlineSong().insert(song, newList.id);
            }
            initOnlineLists();
        }
    }

    @Override
    public void freshChild() {
        mHandler.post(mFreshRunnable);
    }

    @Override
    protected void initialize() {
        mTitleTd = R.string.local_tab_title;
    }

    @Override
    public void initChild(View root) {
        initLocalList(root);
        initHistoryList(root);
        initMyLoveList(root);
        initOtherViews(root);
        initOnlineLists();
    }

    private void initOnlineLists() {
        for (CardView card : mOnlineCard) {
            mOnlinePart.removeView(card);
        }
        List<OnlineList> listList = app.getListData().getOnlineList();
        for (OnlineList list : listList) {
            View view = View.inflate((Context) mCallback, R.layout.item_online_list, null);
            LinearLayout root = view.findViewById(R.id.online_list_root);
            CardView listCard = view.findViewById(R.id.online_list);
            ImageView listLogo = listCard.findViewById(R.id.iv_icon);
            TextView listName = listCard.findViewById(R.id.tv_name);
            Glide.with((Context) mCallback).load(list.logo).into(listLogo);
            listName.setText(list.name);
            root.removeView(listCard);
            mOnlinePart.addView(listCard);
            mOnlineCard.add(listCard);
            listCard.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ListActivity.class);
                intent.putExtra(STARY_TYPE, list.id);
                intent.putExtra(STARY_TITLE, R.string.history);
                mContext.startActivity(intent);
            });
        }
    }

    private void initOtherViews(View root) {
        mOnlinePart = root.findViewById(R.id.local_tab_online_part);
        createAddListDailog();
        mHandler.postDelayed(mFreshRunnable, 2000);
        root.findViewById(R.id.add_list).setOnClickListener(
                v -> mHandler.postDelayed(() -> mAddListDialog.show(), 100));
    }

    private void createAddListDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) mCallback);
        View view = View.inflate((Context) mCallback, R.layout.dialog_search_list, null);
        ImageButton cancel = view.findViewById(R.id.dialog_cancel);
        ImageButton pass = view.findViewById(R.id.dialog_pass);
        mSongListIdInput = view.findViewById(R.id.song_list_id);

        builder.setView(view);
        mAddListDialog = builder.create();
        mAddListDialog.setCancelable(false);
        cancel.setOnClickListener(v -> mAddListDialog.dismiss());
        pass.setOnClickListener(v -> {
            mAddListDialog.dismiss();
            String id = mSongListIdInput.getText().toString().trim();
            for (OnlineList list : app.getListData().getOnlineList()) {
                if (list.id.equals(id)) {
                    return;
                }
            }
            new Thread(() -> QQMusicApi.getSongListContent(id, mListQueryHandler)).start();
        });
    }

    private void initMyLoveList(View root) {
        View myLove = root.findViewById(R.id.list_my_love);
        app = (MusicApp) mCallback.getApplication();
        List<Song> loveList = app.getListData().getList(ListData.P_LOVE);
        mLoveList = new MyLoveList(mContext, myLove, loveList);
        mLoveList.freshCount();
        myLove.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_LOVE);
            intent.putExtra(STARY_TITLE, R.string.my_love);
            mContext.startActivity(intent);
        });
    }

    private void initHistoryList(View root) {
        View history = root.findViewById(R.id.list_history);
        app = (MusicApp) mCallback.getApplication();
        List<Song> historyList = app.getListData().getList(ListData.P_HISTORY);
        mHistory = new HistoryList(mContext, history, historyList);
        mHistory.freshCount();
        history.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_HISTORY);
            intent.putExtra(STARY_TITLE, R.string.history);
            mContext.startActivity(intent);
        });
    }

    private void initLocalList(View root) {
        View localSong = root.findViewById(R.id.list_local_song);
        app = (MusicApp) mCallback.getApplication();
        List<Song> localList = app.getListData().getList(ListData.P_LOCAL);
        mLocalSong = new LocalSongList(mContext, localSong, localList);
        mLocalSong.freshCount();
        localSong.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ListActivity.class);
            intent.putExtra(STARY_TYPE, ListData.P_LOCAL);
            intent.putExtra(STARY_TITLE, R.string.local_song);
            mContext.startActivity(intent);
        });
    }

    private Runnable mFreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLocalSong != null) {
                mLocalSong.freshCount();
            }
            if (mHistory != null) {
                mHistory.freshCount();
            }

            if (mLoveList != null) {
                mLoveList.freshCount();
            }
        }
    };
}
