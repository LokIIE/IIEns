package com.iiens.net;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import org.json.JSONArray;

public class TwitterNews extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        this.mListView = (ListView) view.findViewById(R.id.listview);

        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("#ENSIIE OR @ENSIIE OR @A3IE")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this.context)
                .setTimeline(searchTimeline)
                .build();
        this.mListView.setAdapter(adapter);

        return view;
    }

    @Override
    protected void generateView(View view) {}

    @Override
    protected void displayResult(View view, JSONArray result) {}

    @Override
    protected void apiRequest(final View view) {}

    @Override
    protected void refreshDisplay() {}
}
