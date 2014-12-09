package com.iiens.net;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/** EdtResult
	Fragment affichant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiResult extends FragmentActivity {

	private Button btnNewSearch;
	private Bundle bundle;
	private FragmentManager fragmentManager;
	
	public TrombiResult() {}

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trombi_result);
		getActionBar().setTitle("Résultats de la recherche");
		
		bundle = getIntent().getBundleExtra("bundle");

		fragmentManager = getFragmentManager();

		btnNewSearch = (Button) findViewById(R.id.trombi_newsearch_button);

		// action of the new search button
		btnNewSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Fragment frag = new TrombiResultList();
		frag.setArguments(bundle);
		fragmentManager.beginTransaction().replace(R.id.trombi_res_content, frag).commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putAll(bundle);
	}

}