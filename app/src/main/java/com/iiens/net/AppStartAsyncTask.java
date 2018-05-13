package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;

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

public class AppStartAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public AppStartAsyncTask ( Context context ){

        this.context = context;
    }

    /**
     * Récupère les logos des clubs et les stocke localement
     */
    private void getAllClubLogo() throws IOException {

        // EdtSearchDao dal = new EdtSearchDao( context );
        int timeout = 5000;
        String url = context.getString(R.string.url_api ) + context.getString(R.string.api_logos );

        BufferedReader reader = null;
        StringBuilder stringBuilder;

        int count;

        try {

            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout( 15*1000 );
            connection.connect();

            // download file from the server
            InputStream input = new BufferedInputStream( connection.getInputStream() );
            FileOutputStream output = context.openFileOutput( "logos.zip", Context.MODE_PRIVATE );
            int lenghtOfFile = connection.getContentLength();

            byte data[] = new byte[1024];

            long total = 0;

            while ( ( count = input.read( data ) ) != -1 ) {

                total += count;
                output.write( data, 0, count );
            }

            output.flush();
            output.close();
            input.close();

        } catch ( Exception e ) {

            e.printStackTrace();
            throw e;

        } finally {

            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            try {

                reader.close();

            } catch ( IOException ioe ) {

                ioe.printStackTrace();
            }
        }
    }

    @Override
    protected Boolean doInBackground ( Void... voids ) {

//        try {
//            //getAllClubLogo();
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
        return false;// isAriseOnline();
    }

}
