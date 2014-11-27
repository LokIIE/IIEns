package com.iiens.net;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/** TwitterNews
	Fragment affichant les tweets concernant l'école
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TwitterNews extends Fragment {

	private Bundle bundle = new Bundle();
	private ArrayList<Tweet> result = new ArrayList<Tweet>();
	private String bundleKey = "twitternews";

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
			// Restauration des données du contexte utilisateur
			bundle.putAll(savedInstanceState);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view =  inflater.inflate(R.layout.fragment_listview, container, false);
		//		Bundle bundle = this.getArguments();
		super.onCreate(savedInstanceState);

		bundle = this.getArguments();
		final ListView mListView = (ListView) view.findViewById(R.id.listview);

		if (bundle.containsKey(bundleKey)) {

			Bundle tweetsBundle = bundle.getBundle(bundleKey);
			result = new ArrayList<Tweet>();

			for (int i=0; i < tweetsBundle.size(); i++) {
				ArrayList<String> twIA = tweetsBundle.getStringArrayList(Integer.toString(i));
				Tweet tweetItem = new Tweet(twIA.get(0), twIA.get(1), twIA.get(2), twIA.get(3), twIA.get(4), twIA.get(5), twIA.get(6), twIA.get(7), twIA.get(8));
				result.add(tweetItem);
			}

			mListView.setAdapter(new TwitterItemsAdapter(getActivity().getApplicationContext(), R.layout.fragment_listview, result));

		} else if (!bundle.containsKey(bundleKey) && isOnline()){

			try {
				result = new TwitterGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			mListView.setAdapter(new TwitterItemsAdapter(getActivity().getApplicationContext(), R.layout.fragment_listview, result));

			if (result.size() > 0) saveResult(result, bundle, bundleKey); // save Tweets in Bundle to avoid using data all over again

		} else {
			Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}

		return view;
	}

	/* Verifies that the app has internet access */
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void saveResult(ArrayList<Tweet> result, Bundle bundle, String key) {
		int i = 0;
		Bundle tweetSave = new Bundle();
		for (i=0; i < result.size(); i++){
			tweetSave.putStringArrayList(Integer.toString(i), result.get(i).toArrayListString());
		}
		bundle.putBundle(key, tweetSave);
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

}