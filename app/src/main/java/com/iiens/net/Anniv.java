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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
    private ListView mListView;
    private AnnivDb dal;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        global = (GlobalState) context.getApplicationContext();
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

    void generateView(final View view) {
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

    private void apiRequest(final View view) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, global.getScriptURL() + "/anniv", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                displayResult(view, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(global, R.string.api_error, Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    public void displayResult(View view, JSONArray jResult) {
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

    public void refreshDisplay() {
        dal.deleteAll();
        //new ApiRequest(getActivity(), this, apiKey).execute();

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}