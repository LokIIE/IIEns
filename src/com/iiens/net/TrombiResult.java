package com.iiens.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/** EdtResult
	Fragment affichant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiResult extends Fragment {

	private Button btnNewSearch;
	private ListView mListView;
	private Bundle bundle;
	private ArrayList<TrombiItem> trombiItemsList;
	private FragmentManager fragmentManager;

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle = this.getArguments();
		fragmentManager = getFragmentManager();
		setRetainInstance(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view =  inflater.inflate(R.layout.trombi_result, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		btnNewSearch = (Button) view.findViewById(R.id.trombi_newsearch_button);

		// action of the new search button
		btnNewSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				Fragment frag = new Trombi();
				frag.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.content, frag).commit();
			}
		});


		// make the request
		if (isOnline()){
			Toast.makeText(getActivity().getApplicationContext(), "Requete en cours", Toast.LENGTH_LONG).show();

			TrombiGetRequest getTrombi = new TrombiGetRequest(getActivity(), bundle);
			trombiItemsList = new ArrayList<TrombiItem>();
			try {						
				trombiItemsList = getTrombi.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			mListView.setAdapter(new TrombiItemsAdapter(getActivity().getApplicationContext(), trombiItemsList));
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				fragmentManager.beginTransaction().replace(R.id.content, new TrombiResultDetail(trombiItemsList.get(position))).addToBackStack(null).commit();

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
		outState.putAll(bundle);
	}

}