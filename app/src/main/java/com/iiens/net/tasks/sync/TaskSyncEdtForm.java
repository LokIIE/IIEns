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
 * Synchronisation du formulaire de l'emploi du temps
 */

public class TaskSyncEdtForm extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public TaskSyncEdtForm(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        String url = context.getString(R.string.url_api ) + context.getString(R.string.api_edtForm );
        BufferedReader reader = null;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            /*EdtSearchDao dal = new EdtSearchDao(context);

            while ((line = reader.readLine()) != null) {

                JSONArray jArray = new JSONArray(line);
                for (int i = 0; i < jArray.length(); i++) {

                    if (!dal.createItem(new EdtSearchCategory(i, jArray.getJSONObject(i)))) {

                        break;
                    }
                }
            }*/

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
