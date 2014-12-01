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
		super.onCreate(savedInstanceState);

		final View view =  inflater.inflate(R.layout.listview, container, false);
		bundle = this.getArguments();
		mListView = (ListView) view.findViewById(R.id.listview);
		annivItemsList = new ArrayList<AnnivItem>();

		// Récupération des anniv 
		if (preferences.getBoolean("storage_option", false)) {
			if (preferences.getBoolean("anniv_new_update", false) && isOnline()){ // Télécharger nouvelle news et sauvegarder dans fichier
				try {
					jResult = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");
					annivItemsList = jArrayToArrayList(jResult);
					preferences.edit().putBoolean("anniv_new_update", false).apply();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // Charger depuis fichier
				Toast.makeText(getActivity().getApplicationContext(), "Mise à jour impossible (pas d'Internet)", Toast.LENGTH_LONG).show();
				try {
					annivItemsList = jArrayToArrayList(new JSONArray(readFromInternalStorage(bundleKey + ".txt")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (bundle.containsKey(bundleKey)) { // déjà chargé dans bundle
				try {
					annivItemsList = jArrayToArrayList(new JSONArray(bundle.getString(bundleKey)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (isOnline()) { // télécharger et mettre dans bundle
				try {
					jResult = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
					annivItemsList = jArrayToArrayList(jResult);
					writeToInternalStorage(jResult.toString(), bundleKey + ".txt");

					bundle.putString(bundleKey, jResult.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { // Pas dans un fichier et pas de connexion ...
				Toast.makeText(getActivity().getApplicationContext(), "Impossible de récupérer les annivs...", Toast.LENGTH_LONG).show();
			}
		}
		
		mListView.setAdapter(new AnnivItemsAdapter(getActivity().getApplicationContext(), annivItemsList));

		//		if (!bundle.containsKey(bundleKey) && isOnline()){
		//			AnnivGetRequest getAnniv = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL"));
		//
		//			try {
		//				jResult = getAnniv.execute().get();
		//				AnnivItemsAdapter annivAdapter = new AnnivItemsAdapter(getActivity().getApplicationContext(), jResult);
		//				mListView.setAdapter(annivAdapter);
		//				saveResult(jResult, bundle, bundleKey);
		//			} catch (InterruptedException e) {
		//				e.printStackTrace();
		//			} catch (ExecutionException e) {
		//				e.printStackTrace();
		//			}
		//
		//		} else if (bundle.containsKey(bundleKey)) {
		//			Bundle annivBundle = bundle.getBundle( bundleKey);
		//			jResult = new ArrayList<AnnivItem>();
		//			for (int i=0; i < annivBundle.size(); i++) {
		//				ArrayList<String> annivIA = annivBundle.getStringArrayList(Integer.toString(i));
		//				AnnivItem annivItem = new AnnivItem(annivIA.get(0), annivIA.get(1), annivIA.get(2), annivIA.get(3), annivIA.get(4));
		//				jResult.add(annivItem);
		//			}
		//			mListView.setAdapter(new AnnivItemsAdapter(getActivity().getApplicationContext(), jResult));
		//		} else {
		//			Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		//		}

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

//	private void saveResult(ArrayList<AnnivItem> result, Bundle bundle, String key) {
//		int i = 0;
//		Bundle annivSave = new Bundle();
//		for (i=0; i < result.size(); i++){
//			annivSave.putStringArrayList(Integer.toString(i), result.get(i).toArrayList());
//		}
//		bundle.putBundle(key, annivSave);
//	}

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


}