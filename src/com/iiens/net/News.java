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

public class News extends Fragment {

	private int newsNumber = 6; // number of news to show
	private Bundle bundle = new Bundle();
	// private Context context = new GlobalState();
	private ArrayList<NewsItem> result = new ArrayList<NewsItem>();
	private ListView mListView;

	public Bundle getBundle(){
		return bundle;
	}

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
	public void onResume(){
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_listview, container, false);
		bundle = this.getArguments(); 
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setDivider(null);

		super.onCreate(savedInstanceState);

		// Récupération des news
		if (!bundle.containsKey("news") && isOnline()){
			NewsGetRequest getNews = new NewsGetRequest(newsNumber, bundle.getString("scriptURL"));
			result = new ArrayList<NewsItem>();
			try {
				result = getNews.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			NewsItemsAdapter newsAdapter = new NewsItemsAdapter(getActivity().getApplicationContext(), result, newsNumber);
			mListView.setAdapter(newsAdapter);
			saveResult(result, bundle, "news");
		} else {
			Bundle newsBundle = bundle.getBundle("news");
			result = new ArrayList<NewsItem>();
			for (int i=0; i < newsBundle.size(); i++) {
				ArrayList<String> newsItemArray = newsBundle.getStringArrayList(Integer.toString(i)); 
				NewsItem newsItem = new NewsItem(newsItemArray.get(0), newsItemArray.get(1), newsItemArray.get(2), newsItemArray.get(3));
				result.add(newsItem);
			}
			NewsItemsAdapter newsAdapter = new NewsItemsAdapter(getActivity().getApplicationContext(), result, newsNumber);
			mListView.setAdapter(newsAdapter);
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
		// Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		//		case R.id.action_refresh:
		//			Toast.makeText(getActivity().getApplicationContext(), "Action refresh selected", Toast.LENGTH_LONG).show();
		//			break;
		case R.id.action_settings:
			Toast.makeText(getActivity().getApplicationContext(), "Action Settings selected", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}

		return true;
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
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

}