package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;

import com.iiens.net.database.EdtFormDb;
import com.iiens.net.database.EdtOptDb;
import com.iiens.net.model.EdtFormItem;
import com.iiens.net.model.EdtOptItem;

import org.json.JSONArray;
import org.json.JSONException;

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

    public AppStartAsyncTask ( Context context ){

        this.context = context;
    }

    /**
     * Synchronisation du formulaire de l'emploi du temps
     */
    private void syncEdtForm () throws IOException, JSONException {

        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_edtForm);
        BufferedReader reader = null;

        try {

            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod( "GET" );

            // give it 5 seconds to respond
            connection.setReadTimeout( 5000 );
            connection.connect();

            // read the output from the server
            reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;

            // put each item in database
            EdtFormDb dal = new EdtFormDb( context );

            while ( ( line = reader.readLine() ) != null ) {

                JSONArray jArray = new JSONArray( line );
                for ( int i = 0; i < jArray.length(); i++ ) {

                    if ( !dal.createItem( new EdtFormItem( i, jArray.getJSONObject( i ) ) ) ) {
                        break;
                    }
                }
            }

        } catch ( Exception e ) {

            e.printStackTrace();
            throw e;

        } finally {

            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if ( reader != null ) {

                try {

                    reader.close();

                } catch ( IOException ioe ) {

                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * Synchronisation du formulaire de l'emploi du temps
     */
    private void syncEdtOptions () throws IOException, JSONException {

        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_edtOptions);
        BufferedReader reader = null;

        try {

            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod( "GET" );

            // give it 5 seconds to respond
            connection.setReadTimeout( 5000 );
            connection.connect();

            // read the output from the server
            reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

            String line;

            // put each item in database
            EdtOptDb dal = new EdtOptDb( context );

            while ( ( line = reader.readLine() ) != null ) {

                JSONArray jArray = new JSONArray( line );
                for ( int i = 0; i < jArray.length(); i++ ) {

                    dal.createItem( new EdtOptItem( jArray.getJSONObject( i ) ) );
                }
            }

        } catch ( Exception e ) {

            e.printStackTrace();
            throw e;

        } finally {

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

        EdtFormDb dal = new EdtFormDb( context );
        int timeout = 5000;
        String url = context.getString(R.string.url_apiie) + context.getString(R.string.apiie_logos);

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
//            new DatabaseHelper(context).createDb(null);
//            //syncEdtForm();
//            //syncEdtOptions();
//            //getAllClubLogo();
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
        return false;// isAriseOnline();
    }

}
