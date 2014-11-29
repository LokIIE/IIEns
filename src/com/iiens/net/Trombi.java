package com.iiens.net;

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
	private Spinner mSpinNom, mSpinPrenom, mSpinPseudo, mSpinTel, mSpinPromo, mSpinGroupe, mSpinLogement, mSpinClub;
	private EditText mNom, mPrenom, mPseudo, mTel;
	private CheckBox mMasc, mFem, mEvry, mStras;
	private Bundle requeteParams;
	private Fragment frag;
	private FragmentManager fragmentManager;

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
		view = inflater.inflate(R.layout.fragment_trombi, container, false);
		
		requeteParams = new Bundle();
		
		mFormulaire = (LinearLayout) view.findViewById(R.id.trombi_formulaire);
		mProgressSpinner = (LinearLayout) view.findViewById(R.id.spinner_layout);

		mProgressSpinner = (LinearLayout) view.findViewById(R.id.spinner_layout);
		mSearch = (Button) view.findViewById(R.id.Rechercher);
		mSpinNom = (Spinner) view.findViewById(R.id.option_nom);
		mNom = (EditText) view.findViewById(R.id.nom);
		mSpinPrenom = (Spinner) view.findViewById(R.id.option_prenom);
		mPrenom = (EditText) view.findViewById(R.id.prenom);
		mSpinPseudo = (Spinner) view.findViewById(R.id.option_pseudo);
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
				int type_req_nom = mSpinNom.getSelectedItemPosition() + 1;
				String nom = mNom.getText().toString();
				int type_req_prenom = mSpinPrenom.getSelectedItemPosition() + 1;
				String prenom = mPrenom.getText().toString();
				int type_req_pseudo = mSpinPseudo.getSelectedItemPosition() + 1;
				String pseudo = mPseudo.getText().toString() ;
				int type_req_tel = mSpinTel.getSelectedItemPosition() + 1;
				String tel =  mTel.getText().toString();
				int sexe_fem = (mFem.isChecked() ? 1 : 0);
				int sexe_masc = (mMasc.isChecked() ? 1 : 0);
				
				requeteParams.putString("type_req_nom", String.valueOf(type_req_nom));
				requeteParams.putString("nom", (nom != null) ? nom : "");
				requeteParams.putString("type_req_prenom", String.valueOf(type_req_prenom));
				requeteParams.putString("prenom", (prenom != null) ? prenom : "");
				requeteParams.putString("type_req_pseudo", String.valueOf(type_req_pseudo));
				requeteParams.putString("pseudo", (pseudo != null) ? pseudo : "");
				requeteParams.putString("type_req_tel", String.valueOf(type_req_tel));
				requeteParams.putString("tel", (tel != null) ? tel : "");
				requeteParams.putString("sexe_fem", String.valueOf(sexe_fem));
				requeteParams.putString("sexe_masc", String.valueOf(sexe_masc));
				requeteParams.putString("antenne_evry", String.valueOf((mEvry.isChecked() ? 1 : 0)));
				requeteParams.putString("antenne_stras", String.valueOf((mStras.isChecked() ? 1 : 0)));
				requeteParams.putString("promo", (String) mSpinPromo.getSelectedItem());
				requeteParams.putString("groupe", (String) mSpinGroupe.getSelectedItem());
				requeteParams.putString("logement", (String) mSpinLogement.getSelectedItem());
				requeteParams.putString("club", (String) mSpinClub.getSelectedItem());

				bundle.putBundle("requete", requeteParams);
				// make the request
				if (isOnline()){
					fragmentManager = getFragmentManager();
					frag = new TrombiResult();

					frag.setArguments(bundle);

					mProgressSpinner.setVisibility(View.VISIBLE);
					mFormulaire.setVisibility(View.GONE);				

					fragmentManager.beginTransaction().replace(R.id.content, frag).commit();
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