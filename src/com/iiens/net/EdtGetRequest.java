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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/** EdtGetRequest : classe permettant de récupérer l'emploi du temps à partir des serveurs d'Arise et de filtrer les résultats
	suivant les choix de l'utilisateur
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
	Modifications par : --
 **/

public class EdtGetRequest extends AsyncTask<Void, Void, ArrayList<EdtItem>> {

	private ArrayList<EdtItem> edtItemsList = new ArrayList<EdtItem>();
	private String date;
	private String promo;
	static private String[] filtre;
	private Context context;
	private ProgressDialog dialog;
	private String scriptURL;

	public EdtGetRequest(Context context, String date, String promo, String[] groupFiltre, String scriptURL){
		this.date = date;
		this.promo = promo;
		filtre = groupFiltre;
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage("Please wait");
		dialog.show();
	}

	@Override
	protected ArrayList<EdtItem> doInBackground(Void... voids) {
		edtItemsList = getEdtRequest(date, promo, scriptURL);
		return edtItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<EdtItem> getEdtRequest(String date, String promo, String scriptURL) {

		ArrayList<EdtItem> edtItemsList = new ArrayList<EdtItem>();

		InputStream is = null;
		String result = "";
		// Ajout des paramètres de la requête
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","edt"));
		nameValuePairs.add(new BasicNameValuePair("date", date));
		nameValuePairs.add(new BasicNameValuePair("promo", promo));

		// Envoi de la requête au script PHP.
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
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
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = new JSONArray(result);
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				filterItems(json_data, edtItemsList, filtre);
			}
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return edtItemsList;
	}

	@Override
	protected void onPostExecute(ArrayList<EdtItem> edtItemList) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	static private void filterItems(JSONObject json_data, ArrayList<EdtItem> edtItemsList, String[] filtre) {

		EdtItem edtItem = new EdtItem();
		edtItem.mapJsonObject(json_data);
		String groupe = edtItem.getGroupe();

		boolean filtreEmpty = true;
		for (int i = 0; i< filtre.length; i++) {if (filtre[i] != "") filtreEmpty = false;} 

		// Filtre les cours/td en groupe et n'affiche que le groupe ou le sous-groupe demandé par l'utilisateur
		if (groupe == "" || filtreEmpty) {
			edtItemsList.add(edtItem);
		}
		else {
			if (isInList(groupe, filtre)) {
				edtItemsList.add(edtItem);
			}
		}

	}

	static private boolean isInList (String groupe, String[] list) {

		for (int i=0; i < list.length; i++) {
			String authorizedGroup = list[i];
			if (authorizedGroup != "" && (groupe.startsWith(authorizedGroup) || authorizedGroup.startsWith(groupe))) {
				return true;
			}
		}
		return false;
	}
}
