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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * News
 * Fragment permettant l'affichage des news publiées
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

public class News extends Fragment implements DisplayFragment {

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
        apiKey = getResources().getString(R.string.apiie_news);

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

    void generateView(View view) {
        Bundle bundle = global.getBundle();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the JSON data for this fragment
        try {
            if (preferences.getBoolean(getResources().getString(R.string.bool_news_update_name), false) && global.isOnline()) { // If there is an update available and we are connected to the internet
                new ApiRequest(getActivity(), this, apiKey).execute();
                preferences.edit().putBoolean(getResources().getString(R.string.bool_news_update_name), false).apply();
                Log.e(TAG, "from web with save");
            } else if (bundle.containsKey(apiKey)) { // If data already loaded, retrieve it
                displayResult(view, new JSONArray(bundle.getString(apiKey)));
                Log.e(TAG, "from bundle");
            } else if (global.fileExists(apiKey)) { // If a file with the data exists, load from it
                displayResult(view, new JSONArray(global.readFromInternalStorage(apiKey)));
                Log.e(TAG, "from file");
            } else if (global.isOnline()) { // If the file doesn't exist yet (first launch for example), get the data and create file
                new ApiRequest(context, this, apiKey).execute();
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

    public void refreshDisplay() {
        new ApiRequest(getActivity(), this, apiKey).execute();

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}