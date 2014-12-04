package com.iiens.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

/** EdtGetRequest 
	Classe permettant de r�cup�rer l'emploi du temps en bdd et de filtrer les r�sultats suivant les choix de l'utilisateur
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtGetRequest extends AsyncTask<Void, Void, JSONArray> {

	private JSONArray edtJArray;
	private String week;
	private String promo;
	private String scriptURL;
	private static Context context;

	@SuppressWarnings("static-access")
	public EdtGetRequest(Context context, String week, String promo, String scriptURL){
		this.edtJArray = new JSONArray();
		this.week = week;
		this.promo = promo;
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected JSONArray doInBackground(Void... voids) {
		edtJArray = getEdtRequest(week, promo, scriptURL);
		return edtJArray;
	}

	// R�cup�re une liste d'items de l'emploi du temps.
	public static JSONArray getEdtRequest(String week, String promo, String scriptURL) {

		String result = "";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// Ajout des param�tres de la requ�te
		nameValuePairs.add(new BasicNameValuePair("type","edt"));
		nameValuePairs.add(new BasicNameValuePair("week", week));
		nameValuePairs.add(new BasicNameValuePair("promo", promo));

		try {
			SchemeRegistry schemeRegistry = new SSLArise().init(context);
			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);
			HttpClient httpclient = new DefaultHttpClient(cm, params);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Envoi de la requ�te
			result = httpRequest(httpclient, httppost);
		} catch (UnsupportedEncodingException e) {
			Log.e("edt_get", "Error in encoding nameValuePairs (unsupported) " + e.toString());
		} catch (Exception e) {
			Log.e("edt_get", "Error in http connection " + e.toString());
		}

		JSONArray resJArray = null;		
		try{
			resJArray = new JSONArray(result);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return resJArray;
	}

	private static String httpRequest(HttpClient httpclient, HttpPost httppost) {
		String result = "";
		InputStream is = null;

		// Envoi de la requ�te au script PHP.
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) is = entity.getContent();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("edt_get", "Error in http connection " + e.toString());
		}

		// Conversion de la requ�te en string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
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

}
