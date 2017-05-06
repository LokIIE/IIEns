package com.iiens.net.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.iiens.net.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Vérification de la connexion et mise à jour des ressources
 */

public class TaskSyncLogos extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public TaskSyncLogos(Context context){
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        int timeout = 5000;
        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_logos);

        BufferedReader reader = null;
        StringBuilder stringBuilder;

        int count;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15*1000);
            connection.connect();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            FileOutputStream output = context.openFileOutput("logos.zip", Context.MODE_PRIVATE);
            int lenghtOfFile = connection.getContentLength();

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                reader.close();

            } catch ( IOException ioe ) {

                ioe.printStackTrace();
            }
        }
        return false;
    }

}
