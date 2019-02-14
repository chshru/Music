package com.chshru.music.ui.tab.localtab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout mOnlinePart;
    private List<CardView> mOnlineCard;
    private AddListDialog mAddListDialog;
    private Button mFreshOnlineList;

    public LocalTab(StatusCallback callback, int resId) {
        super(callback, resId);
        mHandler = new Handler(mCallback.getMainLooper());
        mOnlineCard = new ArrayList<>();
        mListQueryHandler = new ListQueryHandler(mCallback.getMainLooper(), this::onListQueryFinish);
    }

    private void onListQueryFinish(String result) {
        boolean isIdAvail = false;
        boolean isIdRepeat = false;
        if (result != null && !result.isEmpty()) {
            OnlineList newList = QQMusicApi.getOnlineListFromResult(result);
            if (newList != null) {
                isIdAvail = true;
                for (OnlineList online : app.getListData().getOnlineList()) {
                    if (newList.id.equals(online.id)) {
                        isIdRepeat = true;
                        break;
                    }
                }
                if (!isIdRepeat) {
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
        }
        if (!isIdAvail) {
            mAddListDialog.setErrorTips(R.string.fresh_fail);
        } else {
            if (isIdRepeat) {
                mAddListDialog.setPassTips(R.string.id_repeat);
            } else {
                mAddListDialog.setPassTips(R.string.add_seccuss);
            }
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
        mOnlineCard.clear();
        List<OnlineList> listList = app.getListData().getOnlineList();
        for (OnlineList list : listList) {
            View view = View.inflate((Context) mCallback, R.layout.item_online_list, null);
            LinearLayout root = view.findViewById(R.id.online_list_root);
            CardView listCard = view.findViewById(R.id.online_list);
            ImageView listLogo = listCard.findViewById(R.id.iv_icon);
            TextView listName = listCard.findViewById(R.id.tv_name);
            TextView listNum = listCard.findViewById(R.id.tv_desc);
            Glide.with((Context) mCallback).load(list.logo).into(listLogo);
            listName.setText(list.name);
            listNum.setText(mContext.getString(R.string.shou, Integer.valueOf(list.songnum)));
            root.removeView(listCard);
            mOnlinePart.addView(listCard);
            mOnlineCard.add(listCard);
            listCard.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ListActivity.class);
                intent.putExtra(STARY_TYPE, list.id);
                intent.putExtra(STARY_TITLE, list.name);
                mContext.startActivity(intent);
            });
        }
    }

    private void onFreshQueryFinish(String result) {
        boolean isIdAvail = false;
        if (result != null && !result.isEmpty()) {
            OnlineList newList = QQMusicApi.getOnlineListFromResult(result);
            if (newList != null) {
                isIdAvail = true;
                for (OnlineList online : app.getListData().getOnlineList()) {
                    if (newList.id.equals(online.id)) {
                        online.copyFrom(newList);
                        break;
                    }
                }
                List<Song> list = QQMusicApi.getOnlineSongForListFromResult(result);
                app.getListData().addOnlineList(newList.id, list);
                app.getHelper().getOnlineList().delete(newList.id);
                app.getHelper().getOnlineList().insert(newList);
                app.getHelper().getOnlineSong().delete(newList.id);
                for (Song song : list) {
                    app.getHelper().getOnlineSong().insert(song, newList.id);
                }
                initOnlineLists();
            }
        }
        mFreshOnlineListThread.setStatus(isIdAvail ? 1 : 0);
    }

    private void freshAllOnlineList() {
        List<OnlineList> oldList = app.getListData().getOnlineList();

        if (!oldList.isEmpty()) {
            if (mFreshOnlineListThread != null) {
                if (mFreshOnlineListThread.isAlive() || !mFreshOnlineListThread.isInterrupted()) {
                    mFreshOnlineListThread.interrupt();
                    mFreshOnlineListThread = null;
                }
            }
            if (mFreshOnlineListThread == null) {
                mFreshOnlineListThread = new FreshOnlineListThread(
                        new ListQueryHandler(
                                mCallback.getMainLooper(),
                                this::onFreshQueryFinish
                        ),
                        oldList
                );
            }
            mFreshOnlineListThread.start();
        }
    }

    private FreshOnlineListThread mFreshOnlineListThread;

    private class FreshOnlineListThread extends Thread {

        public static final int STATUS_WAIT = -1;
        public static final int STATUS_SECCUSS = 1;
        public static final int STATUS_FAIL = 0;

        private ListQueryHandler mHandler;
        private List<OnlineList> mList;
        private int mStatus;

        public void setStatus(int status) {
            mStatus = status;
        }

        public FreshOnlineListThread(ListQueryHandler handler, List<OnlineList> list) {
            super();
            mHandler = handler;
            mList = list;
        }

        @Override
        public void run() {
            mStatus = STATUS_WAIT;
            mHandler.postDelayed(() -> mFreshOnlineList.setClickable(false), 0);
            mHandler.postDelayed(() -> mFreshOnlineList.setText(R.string.freshing), 0);
            for (int i = 0; i < mList.size(); i++) {
                QQMusicApi.getSongListContent(mList.get(i).id, mHandler);
            }
            mHandler.postDelayed(() -> mFreshOnlineList.setClickable(true), 0);
            mHandler.postDelayed(() -> mFreshOnlineList.setText(R.string.fresh_all), 0);
            long start = System.currentTimeMillis();
            while (mStatus == STATUS_WAIT) {
                if (System.currentTimeMillis() - start >= 3000) {
                    break;
                }
            }
            if (mStatus == STATUS_SECCUSS) {
                mHandler.postDelayed(() -> Toast.makeText(mContext, R.string.fresh_seccuss, Toast.LENGTH_SHORT).show(), 0);
            } else {
                mHandler.postDelayed(() -> Toast.makeText(mContext, R.string.fresh_fail, Toast.LENGTH_SHORT).show(), 0);
            }
        }
    }

    private void initOtherViews(View root) {
        mOnlinePart = root.findViewById(R.id.local_tab_online_part);
        mHandler.postDelayed(mFreshRunnable, 2000);
        mAddListDialog = new AddListDialog((Context) mCallback, mListQueryHandler).create();
        root.findViewById(R.id.add_list).setOnClickListener(
                v -> mHandler.postDelayed(() -> mAddListDialog.show(), 100));
        mFreshOnlineList = root.findViewById(R.id.fresh_list);
        mFreshOnlineList.setOnClickListener(v -> freshAllOnlineList());
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
            intent.putExtra(STARY_TITLE,
                    mContext.getResources().getString(R.string.my_love));
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
            intent.putExtra(STARY_TITLE,
                    mContext.getResources().getString(R.string.history));
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
            intent.putExtra(STARY_TITLE,
                    mContext.getResources().getString(R.string.local_song));
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
