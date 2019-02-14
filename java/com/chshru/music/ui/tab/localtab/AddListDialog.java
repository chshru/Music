package com.chshru.music.ui.tab.localtab;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.util.ListQueryHandler;
import com.chshru.music.util.QQMusicApi;

/**
 * Created by abc on 19-1-25.
 */

public class AddListDialog {

    private Context mContext;
    private AlertDialog mDialog;
    private ListQueryHandler mHandler;
    private EditText mInput;
    private TextView mTitle;

    public AddListDialog(Context context, ListQueryHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        clearTips();
        mInput.setText("");
        mDialog.dismiss();
    }

    public String getInput() {
        return mInput.getText().toString().trim();
    }

    public void setErrorTips(int tips) {
        mTitle.setText(tips);
        mTitle.setTextColor(mContext.getResources().getColor(R.color.app_red));
        mHandler.postDelayed(this::clearTips, 2500);
    }

    public void setPassTips(int tips) {
        mTitle.setText(tips);
        mTitle.setTextColor(mContext.getResources().getColor(R.color.app_green));
        mHandler.postDelayed(this::dismiss, 1000);
    }

    public void clearTips() {
        mTitle.setText(R.string.input_song_list_id);
        mTitle.setTextColor(mContext.getResources().getColor(R.color.gray));
    }

    public AddListDialog create() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_search_list, null);
        mInput = view.findViewById(R.id.song_list_id);
        mTitle = view.findViewById(R.id.title);
        builder.setView(view);
        mDialog = builder.create();
        mDialog.setCancelable(false);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.dialog_pass).setOnClickListener(v -> new Thread(() ->
                QQMusicApi.getSongListContent(getInput(), mHandler)).start());
        return this;
    }

}
