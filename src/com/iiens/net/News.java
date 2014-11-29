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
import android.content.SharedPreferences.Editor;
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
	private Bundle bundle = new Bundle();
	private ArrayList<NewsItem> newsItemsList;
	private ListView mListView;
	private Context context;
	SharedPreferences SP;
	NewsItemsAdapter newsAdapter;
	private String bundleKey = "news";

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments();
		context = getActivity();
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
		}

		SP = getActivity().getSharedPreferences("IIEns_prefs", Context.MODE_PRIVATE);

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
		view = inflater.inflate(R.layout.fragment_listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setDivider(null); // Do not show the separations between items on the screen

		// Toast.makeText(getActivity().getApplicationContext(), SP.getString("last_title", ""), Toast.LENGTH_LONG).show();

		// Récupération des news 
		if (bundle.containsKey(bundleKey)){
			newsItemsList = new ArrayList<NewsItem>();
			try {
				newsItemsList = jArrayToArrayList(new JSONArray(bundle.getString(bundleKey)));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			mListView.setAdapter(new NewsItemsAdapter(getActivity().getApplicationContext(), newsItemsList));	
		} else if (!bundle.containsKey(bundleKey) && isOnline()){
			newsItemsList = new ArrayList<NewsItem>();

			JSONArray jResult = null;			
			try {
				jResult = new NewsGetRequest(getActivity(), newsNumber, bundle.getString("scriptURL")).execute().get();
				// writeToInternalStorage(jResult.toString(), bundleKey+".txt");
				// readFromInternalStorage(bundleKey+".txt");
				newsItemsList = jArrayToArrayList(jResult);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mListView.setAdapter(new NewsItemsAdapter(getActivity().getApplicationContext(), newsItemsList));

			// If the request was successful, save the items to save data consumption
			if (newsItemsList.size() > 0) {
				bundle.putString(bundleKey, jResult.toString());
				Editor edit = SP.edit();
				edit.putString("news_last_update", newsItemsList.get(0).getDate());
				edit.apply();
			}
		} else Toast.makeText(getActivity().getApplicationContext(), "Recupérer les news : IMPOSSIBRU ! Try again", Toast.LENGTH_LONG).show();

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

	private void readFromInternalStorage(String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

	/* Save each item of the ArrayList<NewsItem> in the bundle in StringArrayList form */
	private void saveResult(ArrayList<NewsItem> result, Bundle bundle, String key) {
		Bundle newsSave = new Bundle();

		for (int i=0; i < result.size(); i++){
			newsSave.putStringArrayList(Integer.toString(i), result.get(i).toStringArrayList());
		}
		bundle.putBundle(key, newsSave);
	}

}