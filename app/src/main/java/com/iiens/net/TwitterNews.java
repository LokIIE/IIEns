package com.iiens.net;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

public class TwitterNews extends Fragment implements DisplayFragment {

    private final String TAG = getClass().getName();
    private Context context;
    private GlobalState global;
    private String apiKey;
    private ListView mListView;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        global = (GlobalState) context.getApplicationContext();
        apiKey = getResources().getString(R.string.apiie_twitter);

        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);

        generateView(view);

        return view;
    }

    void generateView(View view) {
        Bundle bundle = global.getBundle();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the JSON data for this fragment
        try {
            if (bundle.containsKey(apiKey)) {
                displayResult(view, new JSONArray(bundle.getString(apiKey)));
                Log.e(TAG, "from bundle");
            } else if (global.isOnline()) {
                new ApiRequest(getActivity(), this, apiKey).execute();
                Log.e(TAG, "from web");
            } else if (preferences.getBoolean(getResources().getString(R.string.bool_storage_option_name), false) && global.fileExists(apiKey)) {
                displayResult(view, new JSONArray(global.readFromInternalStorage(apiKey)));
                Log.e(TAG, "from file");
            } else {
                Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new ApiRequest(getActivity(), this, apiKey).execute();

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}