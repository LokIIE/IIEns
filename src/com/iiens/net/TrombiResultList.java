package com.iiens.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/** EdtResult
	Fragment affichant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiResultList extends Fragment {

	private ListView mListView;
	private Bundle bundle;
	private ArrayList<TrombiItem> trombiItemsList;
	private FragmentManager fragmentManager;

	public TrombiResultList() {}

	public TrombiResultList(ArrayList<TrombiItem> trombiItemsList) {
		this.trombiItemsList = trombiItemsList;
	}

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
			JSONArray jArray = null;
			try {
				jArray = new JSONArray(bundle.getString("results"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			for (int i=0; i<jArray.length(); i++) {
				try {
					trombiItemsList.add(new TrombiItem().fromJSONObject(jArray.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = new View(getActivity());
		view = inflater.inflate(R.layout.listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);

		bundle = getArguments();

		fragmentManager = getFragmentManager();

		// make the request
		if (trombiItemsList != null) {}
		else if (isOnline()){
			TrombiGetRequest getTrombi = new TrombiGetRequest(getActivity(), bundle);
			trombiItemsList = new ArrayList<TrombiItem>();
			try {						
				trombiItemsList = getTrombi.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			if (trombiItemsList == null) {
				Toast.makeText(getActivity(), "Plus de 50 résultats, affiner la recherche", Toast.LENGTH_LONG).show();
				FragmentManager fragmentManager = getFragmentManager();
				Fragment frag = new Trombi();
				frag.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.content, frag).commit();
			}
		} else {
			Toast.makeText(getActivity(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}

		if (trombiItemsList != null) {
			JSONArray jArray = new JSONArray();
			for(TrombiItem item : trombiItemsList) {
				jArray.put(item.toJSONObject());
			}
			bundle.putString("results", jArray.toString());
			mListView.setAdapter(new TrombiItemsAdapter(getActivity(), trombiItemsList));
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Fragment frag = new TrombiResultDetail(trombiItemsList, position);
				frag.setArguments(bundle);

				fragmentManager.beginTransaction().replace(R.id.trombi_res_content, frag).commit();

			}
		}); 

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
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

}