package com.iiens.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/** NewsGetRequest
	Classe permettant de récupérer les news de la bdd
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiGetRequest extends AsyncTask<Void, Void, ArrayList<TrombiItem>> {

	private static ArrayList<TrombiItem> trombiItemsList;
	private String scriptURL = "https://www.iiens.net/etudiants/trombi.php";
	private static Context context;
	private Bundle requete;
	private static String login;
	private static String pass;
	private static SchemeRegistry schemeRegistry;

	@SuppressWarnings("static-access")
	public TrombiGetRequest(Context context, Bundle mainBundle){
		this.trombiItemsList = new ArrayList<TrombiItem>();
		this.context = context;
		this.requete = mainBundle.getBundle("requete");
		this.login = mainBundle.getString("login");
		this.pass = mainBundle.getString("pass");
	}

	@Override
	protected ArrayList<TrombiItem> doInBackground(Void... voids) {
		trombiItemsList = getTrombiRequest(scriptURL, requete);

		return trombiItemsList;
	}

	// Récupère une liste d'items de l'emploi du temps.
	public static ArrayList<TrombiItem> getTrombiRequest(String scriptURL, Bundle requete) {

		DefaultHttpClient httpclient = null;
		HttpResponse response;
		InputStream is = null;
		String result = "";
		CookieStore cStore = new BasicCookieStore();

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", login));
		nameValuePairs.add(new BasicNameValuePair("pass", pass));
		nameValuePairs.add(new BasicNameValuePair("slp", "Connexion"));
		nameValuePairs.add(new BasicNameValuePair("type_req[nom]", "2"));
		nameValuePairs.add(new BasicNameValuePair("nom", requete.getString("nom")));
		nameValuePairs.add(new BasicNameValuePair("type_req[prenom]", "2"));
		nameValuePairs.add(new BasicNameValuePair("prenom", requete.getString("prenom")));
		nameValuePairs.add(new BasicNameValuePair("type_req[surnom]", "2"));
		nameValuePairs.add(new BasicNameValuePair("surnom", requete.getString("surnom")));
		nameValuePairs.add(new BasicNameValuePair("type_tel", "2"));
		nameValuePairs.add(new BasicNameValuePair("telephone", requete.getString("tel")));
		nameValuePairs.add(new BasicNameValuePair("sexe[fem]", requete.getString("sexe_fem")));
		nameValuePairs.add(new BasicNameValuePair("sexe[masc]", requete.getString("sexe_masc")));
		nameValuePairs.add(new BasicNameValuePair("promoAnnee[]", "2015"));
		nameValuePairs.add(new BasicNameValuePair("promoAnnee[]", "2016"));
		nameValuePairs.add(new BasicNameValuePair("promoAnnee[]", "2013"));
		nameValuePairs.add(new BasicNameValuePair("promoAnnee[]", "2014"));
		nameValuePairs.add(new BasicNameValuePair("localisation[evry]", requete.getString("antenne_evry")));
		nameValuePairs.add(new BasicNameValuePair("localisation[strasbourg]", requete.getString("antenne_stras")));
		nameValuePairs.add(new BasicNameValuePair("assocesEtOu", "et"));
		nameValuePairs.add(new BasicNameValuePair("tsub", "Rechercher"));

		// Envoi de la commande http
		try {

			schemeRegistry = new SSLArise().init(context);
			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);
			httpclient = new DefaultHttpClient(cm, params);
			httpclient.setCookieStore(cStore);
			HttpPost httppost = new HttpPost(scriptURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("trombi_get", "Error in http connection " + e.toString());
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
			//writeToInternalStorage(result, "trombi.html");
		} catch (Exception e) {
			Log.e("trombi_get", "Error converting result " + e.toString());
		}
		// Parsing the response to extract data

		Document doc = Jsoup.parse(result);
		Elements elements = doc.select("div.page");
		Cookie cookie = cStore.getCookies().get(0);
		for (Element element : elements) {
			TrombiItem item = new TrombiItem();
			item.setNom(element.select("td.nom").text());
			item.setPromo(element.select("td.promo").text());
			for(Element row : element.select("td.infos tr")) {
				if (row.select("td.key").text().equals("Établissement d'origine :")) item.setOrigine(row.select("td.val").text());
				if (row.select("td.key").text().equals("Filière :")) item.setFiliere(row.select("td.val").text());
				if (row.select("td.key").text().equals("Date de naissance :")) item.setNaissance(row.select("td.val").text());
				if (row.select("td.key").text().equals("Téléphone (fixe) :")) item.setTelFixe(row.select("td.val").text());
				if (row.select("td.key").text().equals("Téléphone (port) :")) item.setTelPortable(row.select("td.val").text());
				if (row.select("td.key").text().equals("Mail ensiie:")) item.setMailEnsiie(row.select("td.val").text());
				if (row.select("td.key").text().equals("Mail perso :")) item.setMailPerso(row.select("td.val").text());
				if (row.select("td.key").text().equals("Etudie à :")) item.setAntenne(row.select("td.val").text());
				if (row.select("td.key").text().equals("Groupe :")) item.setGroupe(row.select("td.val").text());
				if (row.select("td.key").text().equals("Associations :")) item.setAssoces(
						row.select("td.val").html()
						.replace("<ul>", "<br/>")
						.replace("<li>", "&emsp; &#8226; &emsp;")
						.replace("</li>", "<br/>")
						.replace("</ul>", "")
						);
			}

			// Each time an image is retrieved, the cookie is deleted, so we force the httpclient to keep it in memory
			cStore = new BasicCookieStore();
			cStore.addCookie(cookie);
			httpclient.setCookieStore(cStore);
			try {
				URI imageURI = new URI("https://www.iiens.net" + element.select("td.photo").select("img").attr("src").toString());
				response = httpclient.execute(new HttpGet(imageURI));
				Bitmap imageBitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
				item.setPhoto(imageBitmap);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			trombiItemsList.add(item);
		}

		httpclient.getConnectionManager().shutdown();

		return trombiItemsList;
	}
}
