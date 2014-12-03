package com.iiens.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/** EdtResult
	Fragment traitant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtResult extends FragmentActivity {

	private Bundle bundle;
	private static ArrayList<EdtItem> result, resultLundi, resultMardi, resultMercredi, resultJeudi, resultVendredi;
	private ViewPager vpPager;
	private Button btnNewSearch;
	private static String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

	FragmentPagerAdapter adapterViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager);
		
		bundle = getIntent().getBundleExtra("bundle");
		
		// make the request
		if (isOnline()){
			EdtGetRequest getEdt = new EdtGetRequest(this, bundle.getString("week"), bundle.getString("promo"), bundle.getStringArray("filtre"), bundle.getString("scriptURL"));
			result = new ArrayList<EdtItem>();
			try {						
				result = getEdt.execute().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
		}
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM", Locale.FRANCE);
		resultLundi = new ArrayList<EdtItem>();
		resultMardi = new ArrayList<EdtItem>();
		resultMercredi = new ArrayList<EdtItem>();
		resultJeudi = new ArrayList<EdtItem>();
		resultVendredi = new ArrayList<EdtItem>();

		for (EdtItem item : result){
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
		
		adapterViewPager = new EdtResultPagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
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

}
