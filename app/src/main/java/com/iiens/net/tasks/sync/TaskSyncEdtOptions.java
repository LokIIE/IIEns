package com.iiens.net.tasks.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.iiens.net.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Mise à jour des options de l'emploi du temps
 */

public class TaskSyncEdtOptions extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public TaskSyncEdtOptions(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_edtOptions);
        BufferedReader reader = null;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
//            EdtOptDb dal = new EdtOptDb(context);
//            while ((line = reader.readLine()) != null) {
//                JSONArray jArray = new JSONArray(line);
//                for (int i = 0; i < jArray.length(); i++) {
//                    dal.createItem(new EdtSearchOption(jArray.getJSONObject(i)));
//                }
//            }

        } catch ( Exception e ) {

            e.printStackTrace();

        } finally {

            if (reader != null) {
                try {

                    reader.close();

                } catch ( IOException ioe ) {

                    ioe.printStackTrace();
                }
            }
        }

        return false;
    }

}
