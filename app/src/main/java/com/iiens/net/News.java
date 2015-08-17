package com.iiens.net;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iiens.net.adapter.NewsItemsAdapter;
import com.iiens.net.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * News
 * Fragment permettant l'affichage des news publiées
 */

public class News extends BaseFragment {

    private final String TAG = getClass().getName();

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiKey = getResources().getString(R.string.apiie_news);
    }

    private ArrayList<NewsItem> jArrayToArrayList(JSONArray jArray) {
        ArrayList<NewsItem> newsItemsList = new ArrayList<>();

        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                NewsItem newsItem = new NewsItem();
                newsItem.fromJsonObject(json_data);
                newsItemsList.add(newsItem);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return newsItemsList;
    }

    protected void generateView(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the JSON data for this fragment
        try {
            if (preferences.getBoolean(getResources().getString(R.string.bool_news_update_name), false) && global.isOnline()) { // If there is an update available and we are connected to the internet
                //new ApiRequest(getActivity(), this, apiKey).execute();
                preferences.edit().putBoolean(getResources().getString(R.string.bool_news_update_name), false).apply();
                Log.e(TAG, "from web with save");
            } else if (global.isOnline()) { // If the file doesn't exist yet (first launch for example), get the data and create file
                this.apiRequest(view);
                Log.e(TAG, "from web");
            } else { // If no connection or data stored, can't do anything
                Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
        }
    }

    public void displayResult(View view, JSONArray jResult) {
        // If the request was successful, save the items to save data consumption and populate listview
        if (jResult != null && jResult.length() > 0) {
            global.getBundle().putString(apiKey, jResult.toString());
            mListView.setAdapter(new NewsItemsAdapter(context, jArrayToArrayList(jResult)));
        }

        if (view != null) view.findViewById(R.id.progress_spinner).setVisibility(View.GONE);

        // In case the refresh button was triggered, stop the "animation"
        if (getView() != null) {
            getView().findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            getView().setAlpha((float) 1);
        }
    }
}