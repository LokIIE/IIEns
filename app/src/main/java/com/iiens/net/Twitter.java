package com.iiens.net;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import org.json.JSONArray;


public class Twitter extends BaseFragment {

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutId = R.layout.listview;
    }

    @Override
    protected void generateView(View view) {
        this.mListView = (ListView) view.findViewById(R.id.listview);

        TwitterListTimeline listTimeline = new TwitterListTimeline.Builder()
                .id(Long.valueOf(context.getResources().getString(R.string.tw_list_id)))
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(global.getApplicationContext())
                .setTimeline(listTimeline)
                .build();

        this.mListView.setAdapter(adapter);
    }

    @Override
    protected void displayResult(View view, JSONArray result) {}

    @Override
    protected void apiRequest(final View view) {}

    @Override
    protected void refreshDisplay() {}
}
