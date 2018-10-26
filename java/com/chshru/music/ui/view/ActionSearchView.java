package com.chshru.music.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.SearchView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chshru.music.R;

/**
 * Created by abc on 18-10-26.
 */

public class ActionSearchView extends SearchView {

    private void initialize() {
        onActionViewExpanded();
        setSubmitButtonEnabled(true);
        setQueryHint(getContext().getString(R.string.search));
        initTextColor();
        initBottomLine();
    }

    private void initTextColor() {
        int id = getContext().getResources().getIdentifier(
                "android:id/search_src_text", null, null);
        TextView tv = findViewById(id);
        tv.setHintTextColor(getResources().getColor(R.color.background_subtext_color));
        tv.setTextColor(getResources().getColor(R.color.main_white));
    }

    private void initBottomLine() {
        int id = getContext().getResources().getIdentifier(
                "android:id/search_plate", null, null);
        View v = findViewById(id);
        v.setBackground(null);

        id = getContext().getResources().getIdentifier(
                "android:id/submit_area", null, null);
        v = findViewById(id);
        v.setBackground(null);
    }

    public ActionSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public ActionSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ActionSearchView(Context context) {
        super(context);
        initialize();
    }

    public static class OnTextChangeListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }
}
