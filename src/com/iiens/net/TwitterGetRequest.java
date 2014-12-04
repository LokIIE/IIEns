package com.iiens.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/** TwitterGetRequest 
	Classe permettant de récupérer les tweets à partir de l'API Twitter
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

// Uses an AsyncTask to download a Twitter user's timeline
public class TwitterGetRequest extends AsyncTask<Void, Void, JSONArray> {

	private String scriptURL;
	private static Context context;

	@SuppressWarnings("static-access")
	public TwitterGetRequest(Context context, String scriptURL){
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected JSONArray doInBackground(Void ... voids) {
		JSONArray result = new JSONArray();

		result = getTweets(scriptURL);

		return result;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static JSONArray getTweets(String scriptURL) {

		JSONArray resJArray = new JSONArray();

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","twitter"));

		// Envoi de la commande http
		try {
			SchemeRegistry schemeRegistry = new SSLArise().init(context);

			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);

			HttpClient httpclient = new DefaultHttpClient(cm, params);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("twitter_get", "Error in http connection " + e.toString());
		}

		// Conversion de la requête en string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
		} catch (Exception e) {
			Log.e("twitter_get", "Error converting result " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = (JSONArray) new JSONObject(result).get("statuses");
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				JSONArray res_array = new JSONArray();
				JSONObject res_tweet = new JSONObject();
				JSONObject res_user = new JSONObject();
				res_tweet.put("created_at", json_data.getString("created_at"));
				res_tweet.put("id", json_data.getString("id"));
				res_tweet.put("text", json_data.getString("text"));
				res_tweet.put("in_reply_to_screen_name", json_data.getString("in_reply_to_screen_name"));
				res_tweet.put("in_reply_to_status_id" , json_data.getString("in_reply_to_status_id"));
				res_tweet.put("in_reply_to_user_id" , json_data.getString("in_reply_to_user_id"));
				res_user.put("screen_name" , json_data.getJSONObject("user").getString("screen_name"));
				res_user.put("name" , json_data.getJSONObject("user").getString("name"));
				res_user.put("profile_image_url" , json_data.getJSONObject("user").getString("profile_image_url"));

				res_array.put(res_tweet).put(res_user);
				resJArray.put(i, res_array);
			}
		}catch(JSONException e){
			Log.e("twitter_get", "Error parsing data " + e.toString());
		}

		return resJArray;
	}

}
