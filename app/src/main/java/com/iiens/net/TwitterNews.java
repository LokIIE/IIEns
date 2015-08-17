package com.iiens.net;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iiens.net.adapter.TwitterItemsAdapter;
import com.iiens.net.model.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TwitterNews
 * Fragment affichant les tweets concernant l'école
 */

public class TwitterNews extends BaseFragment {

    private final String TAG = getClass().getName();

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiKey = getResources().getString(R.string.apiie_twitter);
    }

    protected void generateView(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the JSON data for this fragment
        try {
            if (global.isOnline()) {
                this.apiRequest(view);
                Log.e(TAG, "from web");
            } else {
                Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray twitterJSONArray(String result) {
        JSONArray twitterJArray = null;
        try {
            twitterJArray = (JSONArray) new JSONObject(result).get("statuses");
            for (int i = 0; i < twitterJArray.length(); i++) {
                JSONObject json_data = twitterJArray.getJSONObject(i);
                JSONArray res_array = new JSONArray();
                JSONObject res_tweet = new JSONObject();
                JSONObject res_user = new JSONObject();
                res_tweet.put("created_at", json_data.getString("created_at"));
                res_tweet.put("id", json_data.getString("id"));
                res_tweet.put("text", json_data.getString("text"));
                res_tweet.put("in_reply_to_screen_name", json_data.getString("in_reply_to_screen_name"));
                res_tweet.put("in_reply_to_status_id", json_data.getString("in_reply_to_status_id"));
                res_tweet.put("in_reply_to_user_id", json_data.getString("in_reply_to_user_id"));
                res_user.put("screen_name", json_data.getJSONObject("user").getString("screen_name"));
                res_user.put("name", json_data.getJSONObject("user").getString("name"));
                res_user.put("profile_image_url", json_data.getJSONObject("user").getString("profile_image_url"));

                res_array.put(res_tweet).put(res_user);

                twitterJArray.put(i, res_array);

            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return twitterJArray;
    }

    // Called after the data was retrieved
    public void displayResult(View view, JSONArray jResult) {
        if (jResult != null && jResult.length() > 0) {
            global.getBundle().putString(apiKey, jResult.toString());
            mListView.setAdapter(new TwitterItemsAdapter(getActivity().getApplicationContext(), jArrayToArrayList(jResult)));
        }

        if (view != null) view.findViewById(R.id.progress_spinner).setVisibility(View.GONE);

        // In case the refresh button was triggered, stop the "animation"
        if (getView() != null) {
            getView().findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            getView().setAlpha((float) 1);
        }
    }

    private ArrayList<Tweet> jArrayToArrayList(JSONArray jArray) {
        ArrayList<Tweet> tweetsList = new ArrayList<>();

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONArray tweetArray = jArray.getJSONArray(i);
                JSONObject res_tweet = tweetArray.getJSONObject(0);
                JSONObject res_user = tweetArray.getJSONObject(1);

                Tweet tweetItem = new Tweet(
                        res_tweet.getString("created_at"),
                        res_tweet.getString("id"),
                        res_tweet.getString("text"),
                        res_tweet.getString("in_reply_to_screen_name"),
                        res_tweet.getString("in_reply_to_status_id"),
                        res_tweet.getString("in_reply_to_user_id"),
                        res_user.getString("screen_name"),
                        res_user.getString("name"),
                        res_user.getString("profile_image_url")
                );
                tweetsList.add(tweetItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweetsList;
    }

    public void refreshDisplay() {
        //new ApiRequest(getActivity(), this, apiKey).execute();

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}