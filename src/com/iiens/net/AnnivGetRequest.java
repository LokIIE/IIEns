package com.iiens.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/** AnnivGetRequest
	Classe permettant de récupérer les annivs en bdd
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class AnnivGetRequest extends AsyncTask<Void, Void, ArrayList<AnnivItem>> {

	private static ArrayList<AnnivItem> AnnivItemsList = new ArrayList<AnnivItem>();
	private String scriptURL = null;
	private static Context context;

	@SuppressWarnings("static-access")
	public AnnivGetRequest(Context context, String scriptURL){
		this.context = context;
		this.scriptURL = scriptURL;
	}

	@Override
	protected ArrayList<AnnivItem> doInBackground(Void... voids) {
		AnnivItemsList = getAnnivRequest(scriptURL);

		return AnnivItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<AnnivItem> getAnnivRequest(String scriptURL) {

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type","anniv"));

		// Envoi de la commande http
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
			//Log.d("sdfdsf", "result " + result);
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
				AnnivItemsList.add(AnnivItem);
			}
		}catch(JSONException e){
			Log.e("anniv_get", "Error parsing data " + e.toString());
		}

		return AnnivItemsList;
	}

	//	@Override
	//	protected void onPostExecute() {
	//		// Rien à faire
	//		super.onPostExecute(null);
	//	}

}
