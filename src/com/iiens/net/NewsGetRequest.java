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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/** NewsGetRequest
	Classe permettant de récupérer les news de la bdd
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class NewsGetRequest extends AsyncTask<Void, Void, ArrayList<NewsItem>> {

	private static ArrayList<NewsItem> newsItemsList = new ArrayList<NewsItem>();
	private String scriptURL;
	private String newsNumber;

	public NewsGetRequest(int newsNumber, String scriptURL){
		this.newsNumber = Integer.toString(newsNumber);
		this.scriptURL = scriptURL;
	}

	@Override
	protected ArrayList<NewsItem> doInBackground(Void... voids) {
		newsItemsList = getNewsRequest(newsNumber, scriptURL);
		return newsItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<NewsItem> getNewsRequest(String newsNumber, String scriptURL) {

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","news"));
		nameValuePairs.add(new BasicNameValuePair("newsnumber", newsNumber));

		// Envoi de la commande http
		try {
			HttpParams httpParameters = new BasicHttpParams();

			//			// Set the timeout in milliseconds until a connection is established.
			//			// The default value is zero, that means the timeout is not used. 
			//			int timeoutConnection = 5000;
			//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			//			// Set the default socket timeout (SO_TIMEOUT) 
			//			// in milliseconds which is the timeout for waiting for data.
			//			int timeoutSocket = 5000;
			//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (NullPointerException e) {
			Log.e("news_get", "Null Pointer Exception " + e.toString());
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

		// Parse les données JSON
		try{
			JSONArray jArray = new JSONArray(result);
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				NewsItem newsItem = new NewsItem();
				newsItem.mapJsonObject(json_data);
				newsItemsList.add(newsItem);
			}
		} catch(JSONException e){
			Log.e("news_get", "Error parsing data " + e.toString());
		}

		return newsItemsList;
	}

}
