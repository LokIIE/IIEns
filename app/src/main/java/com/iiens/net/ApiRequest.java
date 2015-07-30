package com.iiens.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * ApiRequest
 * Classe générale permettant de faire des requêtes à l'API
 * Permet également de filtrer les éléments JSON suivant le type de la requête
 */

class ApiRequest extends AsyncTask<Void, Void, JSONArray> {

    private static Context context;
    private DisplayFragment caller;
    private String type = "";

    public ApiRequest() {
    }

    @SuppressWarnings("static-access")
    public ApiRequest(Context context, DisplayFragment frag, String type) {
        this.context = context;
        this.caller = frag;
        this.type = type;
    }

    // Gets the result of the request depending on the field "type"
    private static JSONArray getJSONResult(String type) {
        InputStream is = null;
        String result = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        GlobalState global = (GlobalState) context.getApplicationContext();

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("type", type));

        // HTTP Request
        try {
            SchemeRegistry schemeRegistry = new SSLArise().init(context); // for SSL
            HttpParams params = new BasicHttpParams();
            ClientConnectionManager cm =
                    new ThreadSafeClientConnManager(params, schemeRegistry);

            HttpClient httpclient = new DefaultHttpClient(cm, params);
            HttpPost httppost = new HttpPost(global.getScriptURL());
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Request_for" + type, "Error in http connection " + e.toString());
        }

        if (is == null) return null;

        // Convert the result in String
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("Request_for" + type, "Error converting result " + e.toString());
        }

        JSONArray resJArray = null;
        if (type.equals(context.getResources().getString(R.string.apiie_twitter))) {
            resJArray = twitterJSONArray(result);
        } else {
            try {
                resJArray = new JSONArray(result);
            } catch (JSONException e) {
                Log.e("Request_for_" + type, "Error parsing data " + e.toString());
            }
        }

//        if (preferences.getBoolean(context.getString(R.string.bool_storage_option_name), false) && resJArray != null) { // If the user accepts to store data
//            global.writeToInternalStorage(resJArray.toString(), type);
//        }

        return resJArray;
    }

    private static JSONArray twitterJSONArray(String result) {
        JSONArray twitterJArray = null;
        try {
            twitterJArray = (JSONArray) new JSONObject(result).get("statuses");
            for (int i = 0; i < twitterJArray.length(); i++) {
                JSONObject json_data = twitterJArray.getJSONObject(i);
                JSONArray res_array = new JSONArray();
                JSONObject res_tweet = new JSONObject();
                JSONObject res_user = new JSONObject();
                res_tweet.put("created_at", json_data.getString("created_at"));
                res_tweet.put("id", json_data.getString("id"));
                res_tweet.put("text", json_data.getString("text"));
                res_tweet.put("in_reply_to_screen_name", json_data.getString("in_reply_to_screen_name"));
                res_tweet.put("in_reply_to_status_id", json_data.getString("in_reply_to_status_id"));
                res_tweet.put("in_reply_to_user_id", json_data.getString("in_reply_to_user_id"));
                res_user.put("screen_name", json_data.getJSONObject("user").getString("screen_name"));
                res_user.put("name", json_data.getJSONObject("user").getString("name"));
                res_user.put("profile_image_url", json_data.getJSONObject("user").getString("profile_image_url"));

                res_array.put(res_tweet).put(res_user);

                twitterJArray.put(i, res_array);

            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return twitterJArray;
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        return getJSONResult(type);
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        caller.displayResult(null, result);
    }
}
