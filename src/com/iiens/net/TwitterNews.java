package com.iiens.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
	private JSONArray resJArray = new JSONArray();
	private ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
	private String bundleKey = "twitternews";
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

		final View view =  inflater.inflate(R.layout.listview, container, false);
		super.onCreate(savedInstanceState);

		bundle = this.getArguments();
		final ListView mListView = (ListView) view.findViewById(R.id.listview);

		if (preferences.getBoolean("storage_option", false)) { // If the user allows the app to store data
			if (bundle.containsKey(bundleKey)) {
				try {
					resJArray = new JSONArray(bundle.getString(bundleKey));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (fileExists(bundleKey + ".txt")) {
				try {
					resJArray = new JSONArray(readFromInternalStorage(bundleKey + ".txt"));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (resJArray.length() > 0) {
					bundle.putString(bundleKey, resJArray.toString());
				}
			} else if (isOnline()){
				try {
					resJArray = new TwitterGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				if (resJArray.length() > 0) {
					writeToInternalStorage(resJArray.toString(), bundleKey + ".txt");
					bundle.putString(bundleKey, resJArray.toString());
				}
			} 
		} else { // If the user doesn't want to store data
			if (bundle.containsKey(bundleKey)) {
				try {
					resJArray = new JSONArray(bundle.getString(bundleKey));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (isOnline()){
				try {
					resJArray = new TwitterGetRequest(getActivity(), bundle.getString("scriptURL")).execute().get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				if (resJArray.length() > 0) {
					bundle.putString(bundleKey, resJArray.toString());
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
			}
		}

		for (int i=0; i < resJArray.length(); i++) {
			try {
				JSONArray tweetArray = resJArray.getJSONArray(i);
				JSONObject res_tweet = tweetArray.getJSONObject(0);
				JSONObject res_user = tweetArray.getJSONObject(1);

				Tweet tweetItem = new Tweet(
						res_tweet.getString("created_at"),
						res_tweet.getString("id"),
						res_tweet.getString("text"),
						res_tweet.getString("in_reply_to_screen_name"),
						res_tweet.getString("in_reply_to_status_id"),
						res_tweet.getString("in_reply_to_user_id"),
						res_user.getString("screen_name"),
						res_user.getString("name"),
						res_user.getString("profile_image_url")
						);
				tweetsList.add(tweetItem);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		if (tweetsList.size() > 0) {
			mListView.setAdapter(new TwitterItemsAdapter(getActivity().getApplicationContext(), R.layout.listview, tweetsList));
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

	/* Action when (for ex) the screen orientation changes */
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

	public boolean fileExists(String fname){
		File file = context.getFileStreamPath(fname);
		return file.exists();
	}
}