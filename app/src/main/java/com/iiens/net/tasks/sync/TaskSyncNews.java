package com.iiens.net.tasks.sync;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.iiens.net.GlobalState;
import com.iiens.net.R;
import com.iiens.net.database.AppDb;
import com.iiens.net.database.NewsDao;
import com.iiens.net.model.NewsItem;
import com.iiens.net.tasks.OnAsyncTaskCompleted;

import org.json.JSONException;

public class TaskSyncNews extends AsyncTask<Void, Void, Boolean> {

    private GlobalState context;
    private String url;
    private NewsDao dal;
    private OnAsyncTaskCompleted post;

    public TaskSyncNews ( GlobalState context, OnAsyncTaskCompleted post ) {

        this.context = context;
        this.post = post;
    }

    public void onPreExecute () {

        this.url = this.context.getApiURL( R.string.api_news );
        this.dal = AppDb.getAppDb( context ).newsDao();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    this.url,
                    null,
                    response -> {

                        Log.d( "REPONSE", response.toString() );
                        try {

                            dal.deleteAll();

                            for ( int i = 0; i < response.length(); i++ ) {

                                dal.insert( NewsItem.from( response.getJSONObject( i ) ) );
                            }

                            SharedPreferences prefs = context.getPreferences();
                            String newsUpdate = context.getResources().getString( R.string.bool_news_update_name );

                            prefs.edit()
                                .putBoolean( newsUpdate, false )
                                .apply();

                        } catch ( JSONException e ) {

                            Log.e( "SYNC NEWS", "Error parsing data " );
                        }
                    },
                    error -> Log.e( "SYNC NEWS", "API FAIL" )
            );

            queue.add(request);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute (Boolean result) {

        if( this.post != null ) {

            this.post.then();
        }
    }
}