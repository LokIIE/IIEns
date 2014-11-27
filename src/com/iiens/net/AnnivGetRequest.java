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

/** AnnivGetRequest
	Classe permettant de récupérer les annivs en bdd
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class AnnivGetRequest extends AsyncTask<Void, Void, ArrayList<AnnivItem>> {

	private static ArrayList<AnnivItem> annivItemsList;
	private String scriptURL = null;
	private static Context context;

	@SuppressWarnings("static-access")
	public AnnivGetRequest(Context context, String scriptURL){
		annivItemsList = new ArrayList<AnnivItem>();
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected ArrayList<AnnivItem> doInBackground(Void... voids) {
		annivItemsList = getAnnivRequest(scriptURL);

		return annivItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<AnnivItem> getAnnivRequest(String scriptURL) {

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","anniv"));

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
			Log.e("anniv_get", "Error in http connection " + e.toString());
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
			Log.e("anniv_get", "Error converting result " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = new JSONArray(result);
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				AnnivItem AnnivItem = new AnnivItem();
				AnnivItem.mapJsonObject(json_data);
				annivItemsList.add(AnnivItem);
			}
		}catch(JSONException e){
			Log.e("anniv_get", "Error parsing data " + e.toString());
		}

		return annivItemsList;
	}

	//	@Override
	//	protected void onPostExecute() {
	//		// Rien à faire
	//		super.onPostExecute(null);
	//	}

}
