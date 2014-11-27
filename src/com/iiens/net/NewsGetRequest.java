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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/** NewsGetRequest
	Classe permettant de récupérer les news de la bdd
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class NewsGetRequest extends AsyncTask<Void, Void, JSONArray> {

	private static JSONArray newsJArray;
	private String scriptURL;
	private String newsNumber;
	private static Context context;

	@SuppressWarnings("static-access")
	public NewsGetRequest(Context context, int newsNumber, String scriptURL){
		newsJArray = new JSONArray();
		this.context = context;
		this.newsNumber = Integer.toString(newsNumber);
		this.scriptURL = scriptURL;
	}

	@Override
	protected JSONArray doInBackground(Void... voids) {
		newsJArray = getNewsRequest(newsNumber, scriptURL);
		
		return newsJArray;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static JSONArray getNewsRequest(String newsNumber, String scriptURL) {

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","news"));
		nameValuePairs.add(new BasicNameValuePair("newsnumber", newsNumber));

		// Envoi de la commande http
		try {
			SchemeRegistry schemeRegistry = new SSLArise().init(context);
			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);

			//			// Set the timeout in milliseconds until a connection is established.
			//			// The default value is zero, that means the timeout is not used. 
			//			int timeoutConnection = 5000;
			//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			//			// Set the default socket timeout (SO_TIMEOUT) 
			//			// in milliseconds which is the timeout for waiting for data.
			//			int timeoutSocket = 5000;
			//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(cm, params);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("news_get", "Error in http connection " + e.toString());
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
			Log.e("news_get", "Error converting result " + e.toString());
		}

		JSONArray resJArray = null;
		try {
			resJArray = new JSONArray(result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return resJArray;
	}

}
