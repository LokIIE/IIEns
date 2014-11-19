package com.iiens.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
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

/** EdtGetRequest 
	Classe permettant de récupérer l'emploi du temps en bdd et de filtrer les résultats suivant les choix de l'utilisateur
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtGetRequest extends AsyncTask<Void, Void, ArrayList<EdtItem>> {

	private ArrayList<EdtItem> edtItemsList = new ArrayList<EdtItem>();
	private String date;
	private String promo;
	static private String[] filtre;
	private String scriptURL;
	private static Context context;

	@SuppressWarnings("static-access")
	public EdtGetRequest(Context context, String date, String promo, String[] groupFiltre, String scriptURL){
		this.date = date;
		this.promo = promo;
		this.context = context;
		filtre = groupFiltre;
		this.scriptURL = scriptURL;
	}

	@Override
	protected ArrayList<EdtItem> doInBackground(Void... voids) {
		edtItemsList = getEdtRequest(date, promo, scriptURL);
		return edtItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<EdtItem> getEdtRequest(String date, String promo, String scriptURL) {

		ArrayList<EdtItem> edtItemsList = new ArrayList<EdtItem>();

		//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		//		System.out.println("startdate: " + sdf.format(myCalendar.getTime()));
		//	    Date[] minMaxD = calcDateRangeWeek(myCalendar, Calendar.DAY_OF_WEEK);
		//	    Log.d("Min date", minMaxD[0].toString());
		//	    Log.d("Min date", minMaxD[1].toString());
		//	    
		//		public Date[] calcDateRangeWeek(Calendar c, int day) {
		//		    Date[] dr = new Date[2];
		//		    // setMin
		//		    c.set(day, Calendar.MONDAY);
		//		    dr[0] = c.getTime();
		//		    // setMax
		//		    c.set(day, Calendar.SUNDAY);
		//		    dr[1] = c.getTime();
		//		    return dr;
		//		}

		String result = "";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// Ajout des paramètres de la requête
		nameValuePairs.add(new BasicNameValuePair("type","edt"));
		nameValuePairs.add(new BasicNameValuePair("date", date));
		nameValuePairs.add(new BasicNameValuePair("promo", promo));

		try {
			// Load CA from an InputStream (CA would be saved in Raw file,
			// and loaded as a raw resource)
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream in = context.getResources().openRawResource(R.raw.cacert);
			Certificate ca;
			try {
				ca = cf.generateCertificate(in);
			} finally {
				in.close();
			}

			// Create a KeyStore containing our trusted CAs
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca); 

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tmf.getTrustManagers(), null);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory(keyStore);
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));

			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);
			HttpClient httpclient = new DefaultHttpClient(cm, params);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Envoi de la requête
			result = httpRequest(httpclient, httppost);
		} catch (UnsupportedEncodingException e) {
			Log.e("edt_get", "Error in encoding nameValuePairs (unsupported) " + e.toString());
		} catch (Exception e) {
			Log.e("edt_get", "Error in http connection " + e.toString());
		}

		// Parse les données JSON
		try{
			JSONArray jArray = new JSONArray(result);
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				filterItems(json_data, edtItemsList, filtre);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}

		return edtItemsList;
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
