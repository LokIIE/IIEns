package com.iiens.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * EdtGetRequest
 * Classe permettant de récupérer l'emploi du temps en bdd
 */

class EdtGetRequest extends AsyncTask<Void, Void, JSONArray> {

    private static Context context;
    private final String week;
    private final String promo;

    @SuppressWarnings("static-access")
    public EdtGetRequest(Context context, String week, String promo) {
        this.context = context;
        this.week = week;
        this.promo = promo;
    }

    // Recupere une liste d'items de l'emploi du temps.
    private static JSONArray getEdtRequest(String week, String promo) {

        String result = "";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        GlobalState global = (GlobalState) context.getApplicationContext();

        // Ajout des paramètres de la requête
        nameValuePairs.add(new BasicNameValuePair("type", context.getResources().getString(R.string.apiie_edt)));
        nameValuePairs.add(new BasicNameValuePair("week", week));
        nameValuePairs.add(new BasicNameValuePair("promo", promo));

        try {
            SchemeRegistry schemeRegistry = new SSLArise().init(context);
            HttpParams params = new BasicHttpParams();
            ClientConnectionManager cm =
                    new ThreadSafeClientConnManager(params, schemeRegistry);
            HttpClient httpclient = new DefaultHttpClient(cm, params);
            HttpPost httppost = new HttpPost(global.getScriptURL());
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Envoi de la requête
            result = httpRequest(httpclient, httppost);
        } catch (UnsupportedEncodingException e) {
            Log.e("edt_get", "Error in encoding nameValuePairs (unsupported) " + e.toString());
        } catch (Exception e) {
            Log.e("edt_get", "Error in http connection " + e.toString());
        }

        if (result.length() == 0) {
            return null;
        } // if there is no data

        JSONArray resJArray = null;
        try {
            resJArray = new JSONArray(result);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return resJArray;
    }

    private static String httpRequest(HttpClient httpclient, HttpPost httppost) {
        String result = "";
        InputStream is = null;

        // Envoi de la requête au script PHP.
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) is = entity.getContent();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("edt_get", "Error in http connection " + e.toString());
        }

        // Conversion de la requête en string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("edt_get", "Error in encoding InputStreamReader (unsupported) " + e.toString());
        } catch (Exception e) {
            Log.e("edt_get", "Error converting result " + e.toString());
        }
        return result;
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        return getEdtRequest(week, promo);
    }

}
