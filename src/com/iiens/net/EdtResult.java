package com.iiens.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/** EdtResult
	Fragment traitant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtResult extends FragmentActivity {

	private Bundle bundle = new Bundle();
	private String bundleKey = "edt";
	private static ArrayList<EdtItem> resultLundi, resultMardi, resultMercredi, resultJeudi, resultVendredi;
	private JSONArray jResult;
	private ViewPager vpPager;
	private Button btnNewSearch;
	private static String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};
	private SharedPreferences preferences;
	private FragmentPagerAdapter adapterViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		bundle = getIntent().getBundleExtra("bundle");
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState.getBundle("edtResultBundle"));
		}
		
		String requestWeek = bundle.getString("week");
		String[] requestFilter = bundle.getStringArray("filtre");
		String requestPromo = bundle.getString("promo");
		
		if (bundle.containsKey("edtJArrayResult")) {
			try {
				jResult = new JSONArray(bundle.getString("edtJArrayResult"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			bundle.putString("edtJArrayResult", jResult.toString());
		} else if (preferences.getBoolean("storage_option", false) && requestWeek.equals(preferences.getString("edtWeekSaved", "0"))) {
			try {
				jResult = new JSONArray(readFromInternalStorage(bundleKey + ".txt"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (isOnline()){
			EdtGetRequest getEdt = new EdtGetRequest(this, requestWeek, requestPromo, bundle.getString("scriptURL"));
			jResult = new JSONArray();
			try {
				jResult = getEdt.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			bundle.putString("edtJArrayResult", jResult.toString());

			// Save the results if storage is activated
			String currentWeek = String.valueOf(Calendar.getInstance(Locale.FRENCH).get(Calendar.WEEK_OF_YEAR));
			if (preferences.getBoolean("storage_option", false) && requestWeek.equals(currentWeek)) {
				writeToInternalStorage(jResult.toString(), bundleKey + ".txt");
				preferences.edit().putString("edtWeekSaved", currentWeek).commit();
			}
		} else {
			Toast.makeText(getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}

		// Transform jArray in ArrayList
		ArrayList<EdtItem> edtItemsList = jArrayToArrayList(jResult, requestFilter);
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM", Locale.FRANCE);
		resultLundi = new ArrayList<EdtItem>();
		resultMardi = new ArrayList<EdtItem>();
		resultMercredi = new ArrayList<EdtItem>();
		resultJeudi = new ArrayList<EdtItem>();
		resultVendredi = new ArrayList<EdtItem>();

		// Class items depending on day
		for (EdtItem item : edtItemsList){
			String jour = "";
			try {
				jour = formatter.format(parser.parse(item.getJour()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (jour.contains("lundi")) {
				resultLundi.add(item);
				days[0] = jour;
			}
			else if (jour.contains("mardi")) {
				resultMardi.add(item);
				days[1] = jour;
			}
			else if (jour.contains("mercredi")) {
				resultMercredi.add(item);
				days[2] = jour;
			}
			else if (jour.contains("jeudi")) {
				resultJeudi.add(item);
				days[3] = jour;
			}
			else if (jour.contains("vendredi")) {
				resultVendredi.add(item);
				days[4] = jour;
			}
		}
		
		getActionBar().setTitle("Résultats de la recherche");
		vpPager = (ViewPager) findViewById(R.id.edt_pager);
		btnNewSearch = (Button) findViewById(R.id.edt_newsearch_button);

		// action of the new search button
		btnNewSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public void onStart() {
		super.onStart();
		
		adapterViewPager = new EdtResultPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
	}

	/* Action after (for ex) the screen orientation has been changed */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putBundle("edtResultBundle", bundle);
		super.onSaveInstanceState(outState);
	}

	// Verifies that the app has internet access
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	static private boolean isInList (String groupe, String[] list) {

		for (int i=0; i < list.length; i++) {
			String authorizedGroup = list[i];
			if (authorizedGroup != "" && (groupe.startsWith(authorizedGroup) || authorizedGroup.startsWith(groupe))) {
				return true;
			}
		}
		return false;
	}

	private void writeToInternalStorage(String content, String fileName) {
		String eol = System.getProperty("line.separator");
		BufferedWriter writer = null; 
		try {
			writer = 
					new BufferedWriter(new OutputStreamWriter(this.openFileOutput(fileName, 
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
			input = new BufferedReader(new InputStreamReader(this.openFileInput(fileName)));
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

	public static class EdtResultPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 5;

		public EdtResultPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new EdtResultPage(resultLundi);
			case 1:
				return new EdtResultPage(resultMardi);
			case 2:
				return new EdtResultPage(resultMercredi);
			case 3:
				return new EdtResultPage(resultJeudi);
			case 4:
				return new EdtResultPage(resultVendredi);
			default:
				return null;
			}
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			return days[position];
		}

	}

	private ArrayList<EdtItem> jArrayToArrayList(JSONArray jArray, String[] filtre) {
		ArrayList<EdtItem> edtItemsList = new ArrayList<EdtItem>();

		try{
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				filterItems(json_data, edtItemsList, filtre);
			}
		} catch(JSONException e){
			Log.e("edtResult", "Error parsing data " + e.toString());
		}

		return edtItemsList;
	}

	static private void filterItems(JSONObject json_data, ArrayList<EdtItem> edtItemsList, String[] filtre) {

		EdtItem edtItem = new EdtItem();
		edtItem.mapJsonObject(json_data);
		String groupe = edtItem.getGroupe();

		boolean filtreEmpty = true;
		for (int i = 0; i< filtre.length; i++) {if (filtre[i] != "") filtreEmpty = false;} 

		// Filtre les cours/td en groupe et n'affiche que le groupe ou le sous-groupe demandé par l'utilisateur
		if (groupe == "" || filtreEmpty) {
			edtItemsList.add(edtItem);
		}
		else {
			if (isInList(groupe, filtre)) {
				edtItemsList.add(edtItem);
			}
		}

	}

}
