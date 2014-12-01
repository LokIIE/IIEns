package com.iiens.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/** News 
	Fragment permettant l'affichage des news des iiens
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class News extends Fragment {

	private int newsNumber = 6; // number of news to show
	private String bundleKey = "news"; // file name where to save the results
	
	private Bundle bundle = new Bundle();
	private ArrayList<NewsItem> newsItemsList;
	private JSONArray jResult = null;
	private ListView mListView;
	private Context context;
	private SharedPreferences preferences;

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments();
		context = getActivity();
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
		}

		preferences = getActivity().getSharedPreferences("IIEns_prefs", Context.MODE_PRIVATE);

		// retain this fragment
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			// Restauration des données du contexte utilisateur
			bundle.putAll(savedInstanceState);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = new View(getActivity());
		view = inflater.inflate(R.layout.listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setDivider(null); // Do not show the separations between items on the screen
		newsItemsList = new ArrayList<NewsItem>();

		// Récupération des news 
		if (preferences.getBoolean("storage_option", false)) {
			if (preferences.getBoolean("news_new_update", false) && isOnline()){ // Télécharger nouvelle news et sauvegarder dans fichier
				try {
					jResult = new NewsGetRequest(getActivity(), newsNumber, bundle.getString("scriptURL")).execute().get();
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");
					newsItemsList = jArrayToArrayList(jResult);
					preferences.edit().putBoolean("news_new_update", false).apply();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // Charger depuis fichier
				Toast.makeText(getActivity().getApplicationContext(), "Mise à jour impossible (pas d'Internet)", Toast.LENGTH_LONG).show();
				try {
					newsItemsList = jArrayToArrayList(new JSONArray(readFromInternalStorage(bundleKey + ".txt")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (bundle.containsKey(bundleKey)) { // déjà chargé dans bundle
				try {
					newsItemsList = jArrayToArrayList(new JSONArray(bundle.getString(bundleKey)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (isOnline()) { // télécharger et mettre dans bundle
				try {
					jResult = new NewsGetRequest(getActivity(), newsNumber, bundle.getString("scriptURL")).execute().get();
					newsItemsList = jArrayToArrayList(jResult);
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");

					bundle.putString(bundleKey, jResult.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // Pas dans un fichier et pas de connexion ...
				Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer les news...", Toast.LENGTH_LONG).show();
			}
		}
		//
		//		if (bundle.containsKey(bundleKey)){
		//			try {
		//				newsItemsList = jArrayToArrayList(new JSONArray(bundle.getString(bundleKey)));
		//			} catch (JSONException e) {
		//				e.printStackTrace();
		//			}
		//		} else if (preferences.getBoolean("storage_option", true) && preferences.getBoolean("news_new_update", true)) {
		//			try {
		//				newsItemsList = jArrayToArrayList(new JSONArray(readFromInternalStorage(bundleKey + ".txt")));
		//			} catch (JSONException e) {
		//				e.printStackTrace();
		//			}
		//		} else if (!bundle.containsKey(bundleKey) && isOnline()){
		//			try {
		//				jResult = new NewsGetRequest(getActivity(), newsNumber, bundle.getString("scriptURL")).execute().get();
		//				if (preferences.getBoolean("storage_option", true)) writeToInternalStorage(jResult.toString(), bundleKey + ".txt");
		//				newsItemsList = jArrayToArrayList(jResult);
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		} else Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer les news...", Toast.LENGTH_LONG).show();

		// If the request was successful, save the items to save data consumption and populate listview
		if (newsItemsList.size() > 0) {
			mListView.setAdapter(new NewsItemsAdapter(getActivity().getApplicationContext(), newsItemsList));
		}

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

	private void writeToInternalStorage(String content, String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedWriter writer = null; 
		try {
			writer = 
					new BufferedWriter(new OutputStreamWriter(context.openFileOutput(fileName, 
							Context.MODE_PRIVATE)));
			writer.write(content + eol);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String readFromInternalStorage(String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		String fileString="";
		try {
			input = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
			input.close();
			fileString = buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return fileString;
	}


	// Verifies that the app has internet access
	private boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private ArrayList<NewsItem> jArrayToArrayList(JSONArray jArray) {
		ArrayList<NewsItem> newsItemsList = new ArrayList<NewsItem>();

		try{
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				NewsItem newsItem = new NewsItem();
				newsItem.fromJsonObject(json_data);
				newsItemsList.add(newsItem);
			}
		} catch(JSONException e){
			Log.e("news", "Error parsing data " + e.toString());
		}

		return newsItemsList;
	}

//	/* Save each item of the ArrayList<NewsItem> in the bundle in StringArrayList form */
//	private void saveResult(ArrayList<NewsItem> result, Bundle bundle, String key) {
//		Bundle newsSave = new Bundle();
//
//		for (int i=0; i < result.size(); i++){
//			newsSave.putStringArrayList(Integer.toString(i), result.get(i).toStringArrayList());
//		}
//		bundle.putBundle(key, newsSave);
//	}

}