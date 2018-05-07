package com.iiens.net.tasks.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.iiens.net.GlobalState;
import com.iiens.net.R;
import com.iiens.net.database.AnnivDao;
import com.iiens.net.database.AppDb;
import com.iiens.net.model.AnnivItem;
import com.iiens.net.tasks.OnAsyncTaskCompleted;

import org.json.JSONException;

public class TaskSyncBirthdays extends AsyncTask<Void, Void, Boolean> {

    private GlobalState context;
    private String url;
    private AnnivDao dal;
    private OnAsyncTaskCompleted post;

    public TaskSyncBirthdays ( GlobalState context, OnAsyncTaskCompleted post ) {

        this.context = context;
        this.post = post;
    }

    public void onPreExecute () {

        this.url = this.context.getApiURL( R.string.api_anniv );
        this.dal = AppDb.getAppDb( context ).annivDao();
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

                        Log.d( "REPONSE", response.toString());
                        try {

                            dal.deleteAll();

                            for ( int i = 0; i < response.length(); i++ ) {

                                dal.insert( AnnivItem.from( response.getJSONObject( i ) ) );
                            }

                        } catch ( JSONException e ) {

                            Log.e( "SYNC BIRTHDAYS", "Error parsing data " );
                        }
                    },
                    error -> Log.e( "SYNC BIRTHDAYS", "API FAIL" )
            );

            queue.add(request);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute (Boolean result) {

        this.post.then();
    }
}
