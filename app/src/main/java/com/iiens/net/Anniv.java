package com.iiens.net;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.iiens.net.adapter.AnnivItemsAdapter;
import com.iiens.net.database.AnnivDb;
import com.iiens.net.model.AnnivItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Anniv
 * Fragment permettant l'affichage des anniversaires (publics) des iiens
 */

public class Anniv extends Fragment implements DisplayFragment {

    private final String TAG = getClass().getName();
    private Context context;
    private GlobalState global;
    private String apiKey;
    private ListView mListView;
    private AnnivDb dal;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        global = (GlobalState) context.getApplicationContext();
        apiKey = getResources().getString(R.string.apiie_anniv);
        dal = new AnnivDb(context);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private ArrayList<AnnivItem> getItemList(JSONArray jArray) {
        ArrayList<AnnivItem> annivItemsList = new ArrayList<>();

        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject data = jArray.getJSONObject(i);
                annivItemsList.add(AnnivItem.load(data));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return annivItemsList;
    }

    void generateView(View view) {
        Bundle bundle = global.getBundle();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the JSON data for this fragment
        try {
            if (preferences.getBoolean(getResources().getString(R.string.bool_anniv_update_name), false) && global.isOnline()) { // If there is an update available and we are connected to the internet
                new ApiRequest(context, this, apiKey).execute();
                preferences.edit().putBoolean(getResources().getString(R.string.bool_anniv_update_name), false).apply();
                Log.e(TAG, "from web with save");
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
            e.printStackTrace();
        }
    }

    public void displayResult(View view, JSONArray jResult) {
        ArrayList<AnnivItem> annivItemArrayList = getItemList(jResult);

        // If the request was successful, save the items to save data consumption and populate listview
        if (jResult != null && jResult.length() > 0) {
//            global.getBundle().putString(apiKey, jResult.toString());
            mListView.setAdapter(new AnnivItemsAdapter(context, getItemList(jResult) ));
        }

        if (view != null) view.findViewById(R.id.progress_spinner).setVisibility(View.GONE);

        // In case the refresh button was triggered, stop the "animation"
        if (getView() != null) {
            getView().findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            getView().setAlpha((float) 1);
        }

        for(AnnivItem item : annivItemArrayList) {
            dal.createItem(item);
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