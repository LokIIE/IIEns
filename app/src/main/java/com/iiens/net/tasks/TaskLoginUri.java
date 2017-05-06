package com.iiens.net.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iiens.net.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connexion OAuth Arise
 */

public class TaskLoginUri extends AsyncTask<Void, Void, String> {

    private Context context;
    private String login_uri = "";

    public TaskLoginUri(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(final Void... params) {

        RequestQueue queue = Volley.newRequestQueue( this.context );

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                context.getResources().getString(R.string.apiie_login_arise),
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if( response.has("redirect") ) {

                                login_uri = response.getString("redirect");
                            }
                        } catch (JSONException e) {
                            Toast.makeText( context,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        queue.getCache().clear();
        // Add the request to the RequestQueue.
        queue.add( stringRequest );
        queue.start();

        return login_uri;
    }

}
