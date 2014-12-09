package com.iiens.net;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/** Trombi 
	Fragment permettant la recherche dans le trombinoscope
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class Trombi extends Fragment {

	private Bundle bundle = new Bundle();
	private LinearLayout mProgressSpinner, mFormulaire;
	private Button mSearch;
	private Spinner mSpinTel, mSpinPromo, mSpinGroupe, mSpinLogement, mSpinClub;
	private EditText mNom, mPrenom, mPseudo, mTel;
	private CheckBox mMasc, mFem, mEvry, mStras;
	private Bundle requeteParams;

	private String[] groupes = {"", "1", "1.1", "1.2", "2", "2.1", "2.2", "3", "3.1", "3.2", "4", "4.1", "4.2", "5", "6", "FIPA"};

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments();
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
		}

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

		View view = new View(getActivity());
		view = inflater.inflate(R.layout.trombi_formulaire, container, false);

		requeteParams = new Bundle();

		mFormulaire = (LinearLayout) view.findViewById(R.id.trombi_formulaire);
		mProgressSpinner = (LinearLayout) view.findViewById(R.id.spinner_layout);

		mProgressSpinner = (LinearLayout) view.findViewById(R.id.spinner_layout);
		mSearch = (Button) view.findViewById(R.id.Rechercher);
		mNom = (EditText) view.findViewById(R.id.nom);
		mPrenom = (EditText) view.findViewById(R.id.prenom);
		mPseudo = (EditText) view.findViewById(R.id.pseudo);
		mSpinTel = (Spinner) view.findViewById(R.id.option_tel);
		mTel = (EditText) view.findViewById(R.id.tel);
		mMasc = (CheckBox) view.findViewById(R.id.sexe_m);
		mFem = (CheckBox) view.findViewById(R.id.sexe_f);
		mSpinPromo = (Spinner) view.findViewById(R.id.promo);
		mEvry = (CheckBox) view.findViewById(R.id.antenne_evry);
		mStras = (CheckBox) view.findViewById(R.id.antenne_stras);
		mSpinGroupe = (Spinner) view.findViewById(R.id.option_groupe);
		mSpinLogement = (Spinner) view.findViewById(R.id.option_logement);
		mSpinClub = (Spinner) view.findViewById(R.id.option_club_ancienBdE);

		return view;
	}

	@Override
	public void onStart(){
		super.onStart();

		mSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String nom = mNom.getText().toString();
				String prenom = mPrenom.getText().toString();
				String pseudo = mPseudo.getText().toString() ;
				String tel =  mTel.getText().toString();

				requeteParams.putString("nom", (nom != null) ? nom : "");
				requeteParams.putString("prenom", (prenom != null) ? prenom : "");
				requeteParams.putString("pseudo", (pseudo != null) ? pseudo : "");
				requeteParams.putString("type_req_tel", String.valueOf(mSpinTel.getSelectedItemPosition() + 1));
				requeteParams.putString("tel", (tel != null) ? tel : "");
				requeteParams.putBoolean("sexe_fem", mFem.isChecked());
				requeteParams.putBoolean("sexe_masc", mMasc.isChecked());
				requeteParams.putBoolean("antenne_evry", mEvry.isChecked());
				requeteParams.putBoolean("antenne_stras", mStras.isChecked());
				requeteParams.putString("promo", mSpinPromo.getSelectedItem().toString());
				requeteParams.putString("groupe", groupes[mSpinGroupe.getSelectedItemPosition()]);
				requeteParams.putString("logement", mSpinLogement.getSelectedItem().toString());
				requeteParams.putString("club", (String) mSpinClub.getSelectedItem());

				bundle.putBundle("requete", requeteParams);
				// make the request
				if (isOnline()){
					mProgressSpinner.setVisibility(View.VISIBLE);
					mFormulaire.setVisibility(View.GONE);	
					
					Intent i = new Intent(getActivity(), TrombiResult.class);
					i.putExtra("bundle", bundle);
					startActivity(i);
					
					mProgressSpinner.setVisibility(View.GONE);
					mFormulaire.setVisibility(View.VISIBLE);	
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
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

}