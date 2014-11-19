package com.iiens.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/** EdtResult
	Fragment affichant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtResult extends Fragment {

	private TextView mEdtDate;
	private Button btnNewSearch;
	private ListView mListView;
	private Bundle bundle = new Bundle();
	private SimpleDateFormat dateFrFormater = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH);
	private SimpleDateFormat rqDateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
	private String date;
	private String promo;
	private String[] filtre;

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		Log.d("EdtResult", "onCreate called");
		super.onCreate(savedInstanceState);

		bundle = this.getArguments(); 
		this.date = bundle.getString("date");
		this.promo = bundle.getString("promo");
		this.filtre = bundle.getStringArray("filtre");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("EdtResult", "onCreateView called");
		super.onCreate(savedInstanceState);

		View view =  inflater.inflate(R.layout.edt_result, container, false);
		mEdtDate = (TextView) view.findViewById(R.id.edt_date);
		mListView = (ListView) view.findViewById(R.id.listview);
		btnNewSearch = (Button) view.findViewById(R.id.edt_newsearch_button);

		try {
			mEdtDate.setText(dateFrFormater.format(rqDateFormater.parse(date.toString())).toString());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// action of the new search button
		btnNewSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				Fragment frag = new Edt();
				frag.setArguments(bundle);
				fragmentManager.beginTransaction().replace(R.id.content, frag).commit();
			}
		});


		// make the request
		if (isOnline()){

			EdtGetRequest getEdt = new EdtGetRequest(getActivity(), date, promo, filtre, bundle.getString("scriptURL"));
			ArrayList<EdtItem> result = new ArrayList<EdtItem>();
			try {						
				result = getEdt.execute((Void) null).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			mListView.setAdapter(new EdtItemsAdapter(getActivity().getApplicationContext(), result));
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

}