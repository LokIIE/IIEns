package com.iiens.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private ArrayList<NewsItem> result = new ArrayList<NewsItem>();
	private ListView mListView;
	SharedPreferences SP;

	// News constructor for creating fragment with arguments
	public static News newInstance(Bundle mainBundle) {
		News fragment = new News();
		fragment.setArguments(mainBundle);
		return fragment;
	}

	public Bundle getBundle(){
		return bundle;
	}

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments();
		SP = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
	public void onResume(){
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setDivider(null);

		// Toast.makeText(getActivity().getApplicationContext(), SP.getString("last_title", ""), Toast.LENGTH_LONG).show();

		super.onCreate(savedInstanceState);

		// Récupération des news
		if (!bundle.containsKey("news") && isOnline()){
			result = new ArrayList<NewsItem>();
			NewsGetRequest getNews = new NewsGetRequest(getActivity(), newsNumber, bundle.getString("scriptURL"));
			try {
				result = getNews.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			NewsItemsAdapter newsAdapter = new NewsItemsAdapter(getActivity().getApplicationContext(), result, newsNumber);
			mListView.setAdapter(newsAdapter);
			if (result.size() > 0) {
				saveResult(result, bundle, "news");
				Editor edit = SP.edit();
				edit.putString("last_title", result.get(0).getTitle());
				edit.apply();
			}
		} else if (bundle.containsKey("news")){
			Bundle newsBundle = bundle.getBundle("news");
			result = new ArrayList<NewsItem>();
			for (int i=0; i < newsBundle.size(); i++) {
				ArrayList<String> newsItemArray = newsBundle.getStringArrayList(Integer.toString(i)); 
				NewsItem newsItem = new NewsItem(newsItemArray.get(0), newsItemArray.get(1), newsItemArray.get(2), newsItemArray.get(3));
				result.add(newsItem);
			}
			NewsItemsAdapter newsAdapter = new NewsItemsAdapter(getActivity().getApplicationContext(), result, newsNumber);
			mListView.setAdapter(newsAdapter);
		} else Toast.makeText(getActivity().getApplicationContext(), "Recupérer les news : IMPOSSIBRU ! Try again", Toast.LENGTH_LONG).show();

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	private void saveResult(ArrayList<NewsItem> result, Bundle bundle, String key) {
		int i = 0;
		Bundle newsSave = new Bundle();
		for (i=0; i < result.size(); i++){
			newsSave.putStringArrayList(Integer.toString(i), result.get(i).toArrayList());
		}
		bundle.putBundle(key, newsSave);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putAll(bundle);
	}

}