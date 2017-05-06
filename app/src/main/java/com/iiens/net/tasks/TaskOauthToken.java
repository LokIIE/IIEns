package com.iiens.net.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.iiens.net.GlobalState;
import com.iiens.net.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Récupération du token CSRF pour l'envoi des identifiants
 */

public class TaskOauthToken extends AsyncTask<Void, Void, String> {

    private Context context;

    public TaskOauthToken(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        int timeout = 5000;
        StringBuffer sb = new StringBuffer();

        try {

            HttpsURLConnection connection = (HttpsURLConnection) new URL( context.getResources().getString(R.string.apiie_oauth) ).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");

            CookieHandler.setDefault( GlobalState.cookieManager );

            BufferedReader in = new BufferedReader(new InputStreamReader( connection.getInputStream() ) );
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }

            Document doc = Jsoup.parse( sb.toString() );
            Elements csrf_input = doc.select("input[name='csrf_token']");

            return csrf_input.val();

        } catch (IOException e) {

            Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG );
            return "";
        }
    }
}
