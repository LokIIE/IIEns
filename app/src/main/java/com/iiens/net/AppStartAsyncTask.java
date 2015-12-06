package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iiens.net.database.FormEdtDb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Vérification de la connexion et mise à jour des ressources
 */

public class AppStartAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public AppStartAsyncTask(Context context){
        this.context = context;
    }

    /**
     * Ping des serveurs d'Arise
     * @return True si les serveurs sont joignables, False sinon
     */
    private boolean isAriseOnline() {
        int timeout = 5000;
        String url = context.getResources().getString(R.string.url_iiens);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * Création de la bdd des options du formulaire de l'emploi du temps
     */
    private void getFormEdtOptions() throws IOException {
        FormEdtDb dal = new FormEdtDb(context);
        int timeout = 5000;
        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_edtOptions);

        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            Log.d("TEST", stringBuilder.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * Récupère les logos des clubs et les stocke localement
     */
    private void getAllClubLogo() throws IOException {
        FormEdtDb dal = new FormEdtDb(context);
        int timeout = 5000;
        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_logos);

        BufferedReader reader = null;
        StringBuilder stringBuilder;

        int count;

        try
        {
            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // download file from the server
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
//        finally
//        {
//            // close the reader; this can throw an exception too, so
//            // wrap it in another try/catch block.
//            try
//            {
//                reader.close();
//            }
//            catch (IOException ioe)
//            {
//                ioe.printStackTrace();
//            }
//        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            getFormEdtOptions();
            //getAllClubLogo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isAriseOnline();
    }

}
