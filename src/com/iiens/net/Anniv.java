package com.iiens.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.content.Context;
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

	private Bundle bundle = new Bundle();
	private ArrayList<AnnivItem> result = new ArrayList<AnnivItem>();
	private String bundleKey = "anniv";

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments(); 

		// retain this fragment
		setRetainInstance(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			// Restauration des donn�es du contexte utilisateur
			bundle.putAll(savedInstanceState);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View view =  inflater.inflate(R.layout.listview, container, false);
		bundle = this.getArguments();
		final ListView mListView = (ListView) view.findViewById(R.id.listview);

		if (!bundle.containsKey(bundleKey) && isOnline()){
			AnnivGetRequest getAnniv = new AnnivGetRequest(getActivity(), bundle.getString("scriptURL"));

			try {
				result = getAnniv.execute().get();
				AnnivItemsAdapter annivAdapter = new AnnivItemsAdapter(getActivity().getApplicationContext(), result);
				mListView.setAdapter(annivAdapter);
				saveResult(result, bundle, bundleKey);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		} else if (bundle.containsKey(bundleKey)) {
			Bundle annivBundle = bundle.getBundle( bundleKey);
			result = new ArrayList<AnnivItem>();
			for (int i=0; i < annivBundle.size(); i++) {
				ArrayList<String> annivIA = annivBundle.getStringArrayList(Integer.toString(i));
				AnnivItem annivItem = new AnnivItem(annivIA.get(0), annivIA.get(1), annivIA.get(2), annivIA.get(3), annivIA.get(4));
				result.add(annivItem);
			}
			mListView.setAdapter(new AnnivItemsAdapter(getActivity().getApplicationContext(), result));
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}

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

	private void saveResult(ArrayList<AnnivItem> result, Bundle bundle, String key) {
		int i = 0;
		Bundle annivSave = new Bundle();
		for (i=0; i < result.size(); i++){
			annivSave.putStringArrayList(Integer.toString(i), result.get(i).toArrayList());
		}
		bundle.putBundle(key, annivSave);
	}
	
	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

}