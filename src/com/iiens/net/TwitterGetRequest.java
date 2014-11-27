package com.iiens.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/** TwitterGetRequest 
	Classe permettant de récupérer les tweets à partir de l'API Twitter
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

// Uses an AsyncTask to download a Twitter user's timeline
public class TwitterGetRequest extends AsyncTask<Void, Void, ArrayList<Tweet>> {

	private String scriptURL;
	private static Context context;

	@SuppressWarnings("static-access")
	public TwitterGetRequest(Context context, String scriptURL){
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected ArrayList<Tweet> doInBackground(Void ... voids) {
		ArrayList<Tweet> result = new ArrayList<Tweet>();

		result = getTweets(scriptURL);

		return result;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<Tweet> getTweets(String scriptURL) {

		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

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
			//Log.d("sdfdsf", "result " + result);
		} catch (Exception e) {
			Log.e("twitter_get", "Error converting result " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = (JSONArray) new JSONObject(result).get("statuses");
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				Tweet tweet = new Tweet(
						json_data.getString("created_at"),
						json_data.getString("id"),
						json_data.getString("text"),
						json_data.getString("in_reply_to_screen_name"),
						json_data.getString("in_reply_to_status_id"),
						json_data.getString("in_reply_to_user_id"),
						json_data.getJSONObject("user").getString("screen_name"),
						json_data.getJSONObject("user").getString("name"),
						json_data.getJSONObject("user").getString("profile_image_url_https")
						);
				tweetList.add(tweet);
			}
		}catch(JSONException e){
			Log.e("twitter_get", "Error parsing data " + e.toString());
		}

		return tweetList;
	}


	public Bitmap getBitmap(String bitmapUrl) {
		try {
			URL url = new URL(bitmapUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			return null;
		}
	}

}
