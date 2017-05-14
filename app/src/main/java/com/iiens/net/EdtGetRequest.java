package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Tâche asynchrone exécutant la recherche de l'emploi du temps
 */
public class EdtGetRequest extends AsyncTask<Void, Void, JSONArray> {

    private static Context context;
    private final String week;
    private final String promo;

    @SuppressWarnings("static-access")
    public EdtGetRequest ( Context context, String week, String promo ) {

        this.context = context;
        this.week = week;
        this.promo = promo;
    }

    // Recupere une liste d'items de l'emploi du temps.
    private static JSONArray getEdtRequest ( String week, String promo ) {

        BufferedReader reader = null;
        JSONArray resJArray = null;
        GlobalState global = (GlobalState) context.getApplicationContext();

        if ( !global.isOnline() ) {

            Toast.makeText( global, context.getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
        }

        // URL avec paramètres de la requête
        String url = global.getScriptURL() + context.getString(R.string.apiie_edt) + "/" + promo + "/" + week;

        try {

            // create the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) new URL( url ).openConnection();
            // just want to do an HTTP GET here
            connection.setRequestMethod( "GET" );

            // give it 5 seconds to respond
            connection.setReadTimeout( 5000 );
            connection.connect();

            // read the output from the server
            String result;

            // Conversion de la requête en string
            reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            StringBuilder sb = new StringBuilder();
            String line;
            while ( ( line = reader.readLine() ) != null ) {

                sb.append( line ).append("\n");
            }
            result = sb.toString();

            if ( result.length() == 0 ) return null;

            resJArray = new JSONArray( result );

        } catch ( JSONException | IOException e ) {

            e.printStackTrace();

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

        return resJArray;
    }

    @Override
    protected JSONArray doInBackground( Void... voids ) {
        return getEdtRequest( week, promo );
    }
}
