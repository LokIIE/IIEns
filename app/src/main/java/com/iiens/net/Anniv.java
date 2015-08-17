package com.iiens.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Anniv extends BaseFragment {

    private final String TAG = getClass().getName();
    private AnnivDb dal;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.apiKey = getResources().getString(R.string.apiie_anniv);
        this.dal = new AnnivDb(context);
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

    protected void generateView(final View view) {
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
                        this.apiRequest(view);
                    }
                }
            } else if (global.isOnline()) {
                this.apiRequest(view);
                Log.e(TAG, "from web");
            } else { // If no connection or data stored, can't do anything
                Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void displayResult(View view, JSONArray jResult) {
        ArrayList<AnnivItem> annivItemArrayList = getItemList(jResult);

        // If the request was successful, save the items to save data consumption and populate listview
        if (jResult != null && jResult.length() > 0) {
            mListView.setAdapter(new AnnivItemsAdapter(context, getItemList(jResult) ));
        }

        // In case the refresh button was triggered, stop the "animation"
        if (getView() != null) {
            getView().findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            getView().setAlpha((float) 1);
        }

        for(AnnivItem item : annivItemArrayList) {
            dal.createItem(item);
        }
    }
}