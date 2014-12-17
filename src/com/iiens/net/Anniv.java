package com.iiens.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/** Anniv
	Fragment permettant l'affichage des anniversaires (publics) des iiens
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class Anniv extends Fragment {

	private String bundleKey = "anniv";

	private Bundle bundle = new Bundle();
	private ArrayList<AnnivItem> annivItemsList;
	private JSONArray jResult = null;
	private ListView mListView;
	private Context context;
	private SharedPreferences preferences;

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments();
		context = getActivity();

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
		super.onCreate(savedInstanceState);

		final View view =  inflater.inflate(R.layout.listview, container, false);
		bundle = this.getArguments();
		mListView = (ListView) view.findViewById(R.id.listview);
		annivItemsList = new ArrayList<AnnivItem>();

		// Récupération des anniv 
		if (preferences.getBoolean("storage_option", false)) { // If the user accepts to store data
			if (preferences.getBoolean("anniv_new_update", false) && isOnline()){ // If there is a new birthday coming up, update the file
				try {
					jResult = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");
					annivItemsList = jArrayToArrayList(jResult);
					preferences.edit().putBoolean("anniv_new_update", false).apply();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (fileExists(bundleKey + ".txt")){ // Retrieve the data from the file (happens more often)
				try {
					annivItemsList = jArrayToArrayList(new JSONArray(readFromInternalStorage(bundleKey + ".txt")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (isOnline()) { // if the file doesn't exist yet (first launch for example), get the data and create file
				try {
					jResult = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
					annivItemsList = jArrayToArrayList(jResult);
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");

					bundle.putString(bundleKey, jResult.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}				
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Connexion à Internet requise", Toast.LENGTH_LONG).show();
			} 
		} else { // If the user does not accept to store data
			if (fileExists(bundleKey + ".txt")) {context.getFileStreamPath(bundleKey + ".txt").delete();} // if he changed minds for ex.
			
			if (bundle.containsKey(bundleKey)) { // If data already loaded, retrieve it
				try {
					annivItemsList = jArrayToArrayList(new JSONArray(bundle.getString(bundleKey)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (isOnline()) { // If we have an internet connection, get the data
				try {
					jResult = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
					annivItemsList = jArrayToArrayList(jResult);

					bundle.putString(bundleKey, jResult.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // If no connection, can't do anything
				Toast.makeText(getActivity().getApplicationContext(), "Connexion à Internet requise", Toast.LENGTH_LONG).show();
			}
		}

		mListView.setAdapter(new AnnivItemsAdapter(getActivity().getApplicationContext(), annivItemsList));

		return view;
	}

	// Verifies that the app has internet access
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

	private ArrayList<AnnivItem> jArrayToArrayList(JSONArray jArray) {
		ArrayList<AnnivItem> annivItemsList = new ArrayList<AnnivItem>();

		try {
			for(int i=0; i<jArray.length(); i++){
				JSONObject json_data = jArray.getJSONObject(i);
				AnnivItem AnnivItem = new AnnivItem();
				AnnivItem.mapJsonObject(json_data);
				annivItemsList.add(AnnivItem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return annivItemsList;
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

		if (fileExists(fileName)){
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
		}

		return fileString;
	}

	public boolean fileExists(String fname){
		File file = context.getFileStreamPath(fname);
		return file.exists();
	}

}