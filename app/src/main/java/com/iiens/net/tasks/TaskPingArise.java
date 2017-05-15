package com.iiens.net.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iiens.net.R;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Vérification de la connexion
 */

public class TaskPingArise extends AsyncTask<Void, Void, Object> {

    private Context context;

    public TaskPingArise ( Context context ) {

        this.context = context;
    }

    @Override
    protected Object doInBackground ( Void... voids ) {

        try {

            int timeout = 5000;
            String url = context.getResources().getString(R.string.url_iiens);

            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();
            connection.setConnectTimeout( timeout );
            connection.setReadTimeout( timeout );
            connection.setRequestMethod( "GET" );

            int responseCode = connection.getResponseCode();
            return ( 200 <= responseCode && responseCode <= 399 );

        } catch ( Exception e ) {

           return e;
        }
    }

    @Override
    protected void onPostExecute ( Object result ) {

        if( result instanceof Exception ) {

            Toast.makeText( context, context.getResources().getString( R.string.arise_unavailable ), Toast.LENGTH_LONG )
                    .show();
        }
    }
}
