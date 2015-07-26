package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AppStartAsyncTask
 * Vérification à l'ouverture de l'application
 */

public class AppStartAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public AppStartAsyncTask(Context context){
        this.context = context;
    }

    /**
     * isAriseOnline
     * Ping des serveurs d'Arise
     * @return True si les serveurs sont joignables, False sinon
     */
    boolean isAriseOnline() {
        int timeout = 5000;
        String url = context.getResources().getString(R.string.url_iiens);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return isAriseOnline();
    }

}
