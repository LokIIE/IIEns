package com.iiens.net;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

import org.json.JSONArray;

import io.fabric.sdk.android.Fabric;


public class Twitter extends BaseFragment {

    private ListView mListView;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutId = R.layout.listview;

        this.prefs = ((GlobalState) getActivity().getApplicationContext()).getPreferences();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.tw_key),
                getString(R.string.tw_secret));
        Fabric.with( getActivity().getApplicationContext(), new com.twitter.sdk.android.Twitter(authConfig));
    }

    @Override
    protected void generateView(View view) {
        this.mListView = (ListView) view.findViewById(R.id.listview);

        TwitterListTimeline listTimeline = new TwitterListTimeline.Builder()
                .id( Long.valueOf( context.getResources().getString( R.string.tw_list_id ) ) )
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder( global.getApplicationContext() )
                .setTimeline(listTimeline)
                .setViewStyle( prefs.getBoolean( getString( R.string.pref_mode_nuit_key ), false ) ? R.style.TweetDarkStyle : R.style.TweetLightStyle )
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
