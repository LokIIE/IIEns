package com.iiens.net;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        this.generateView(view);

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
        AnnivItem firstItem = dal.getFirstItem();
        // Get the JSON data for this fragment
        try {
            if (firstItem != null) {
                DateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.FRENCH);
                Date firstDate = dateFormat.parse(firstItem.getAnniv());
                Date today = new Date();
                if (firstDate.compareTo(today) < 0) {
                    mListView.setAdapter(new AnnivItemsAdapter(context, dal.getAllItems()));
                } else {
                    dal.deleteAll();
                    if (global.isOnline()) {
                        new ApiRequest(context, this, apiKey).execute();
                    }
                }
            } else if (global.isOnline()) {
                // Création de la table anniversaires et insertion des résultats avant affichage
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
        dal.deleteAll();
        new ApiRequest(getActivity(), this, apiKey).execute();

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}