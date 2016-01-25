package com.iiens.net;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import org.json.JSONArray;

/**
 * BaseFragment
 * Base des fragments
 */

abstract class BaseFragment extends Fragment {

    protected String apiKey;
    protected Context context;
    protected GlobalState global;
    protected ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        this.global = (GlobalState) context.getApplicationContext();
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview, container, false);
        this.mListView = (ListView) view.findViewById(R.id.listview);

        this.generateView(view);

        return view;
    }

    /**
     * Génère la page courante
     * @param view Vue à remplir
     */
    protected abstract void generateView(final View view);

    protected void apiRequest(final View view) {
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, global.getScriptURL() + apiKey, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    displayResult(view, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(global, apiKey + " : " + R.string.api_error, Toast.LENGTH_LONG).show();
                }
            });
            queue.add(request);
    }

    /**
     * Affichage des résultats d'une requête asynchrone
     * @param view Vue à remplir
     * @param result Données à afficher
     */
    protected abstract void displayResult(View view, JSONArray result);

    /**
     * Actualisation des données de la vue courante
     */
    protected void refreshDisplay() {
        this.apiRequest(getView());

        // In case the refresh button was triggered, starts an "animation"
        if (getView() != null) {
            getView().setAlpha((float) 0.3);
        }
    }
}