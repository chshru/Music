package com.chshru.music.ui.main;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.chshru.music.R;
import com.chshru.music.base.ActivityBase;

/**
 * Created by abc on 18-10-26.
 */

public class SearchActivity extends ActivityBase {

    private SearchView mSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initialize() {
        mSearch = findViewById(R.id.sv_saerch_aty);
        mSearch.onActionViewExpanded();
        mSearch.setSubmitButtonEnabled(true);
        mSearch.setIconifiedByDefault(true);
        mSearch.setOnSearchClickListener(v -> System.err.println("setOnSearchClickListener"));
        mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String queryText) {

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String queryText) {

                if (mSearch != null) {
                    InputMethodManager imm;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
                        }
                    }
                    mSearch.clearFocus();
                }
                System.err.println(queryText);
                return true;
            }
        });
    }
}
