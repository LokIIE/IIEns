package com.iiens.net;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;

public class Twitter extends BaseFragment {

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutId = R.layout.home_listview;
    }

    @Override
    protected void generateView(View view) {

        this.mListView = view.findViewById(R.id.home_listview );
        this.mListView.setAdapter( ((GlobalState) getActivity().getApplicationContext()).getTwListAdapter() );
    }

    @Override
    protected void displayResult(View view, JSONArray result) {}

    @Override
    protected void apiRequest(final View view) {}
}
