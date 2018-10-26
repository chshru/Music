package com.chshru.music.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SearchView;
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
        int id = getContext().getResources().getIdentifier(
                "android:id/search_src_text", null, null);
        TextView tv = findViewById(id);
        tv.setHintTextColor(getResources().getColor(R.color.background_subtext_color));
        tv.setTextColor(getResources().getColor(R.color.main_white));
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

    public static class OnTextChangeListener implements SearchView.OnQueryTextListener{

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
