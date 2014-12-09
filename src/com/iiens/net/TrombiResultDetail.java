package com.iiens.net;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** EdtResult
	Fragment affichant les résultats de la recherche sur l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiResultDetail extends Fragment {

	private Button btnReturn;
	private ArrayList<TrombiItem> trombiItemsList;
	private TrombiItem trombiItem;
	private TextView mNom, mPromo, mOrigine, mFiliere, mNaissance, mTelFixe, mTelPort, mMailIIE, mMailPerso, mAntenne, mGroupe, mAssoces;
	private ImageView mPhoto;

	public TrombiResultDetail(ArrayList<TrombiItem> trombiItemsList, int position) {
		this.trombiItemsList = trombiItemsList;
		this.trombiItem = trombiItemsList.get(position);
	}

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view =  inflater.inflate(R.layout.trombi_item_detail, container, false);
		btnReturn = (Button) view.findViewById(R.id.trombi_detail_return);
		mNom = (TextView) view.findViewById(R.id.item_nom);
		mPromo = (TextView) view.findViewById(R.id.item_promo);
		mPhoto = (ImageView) view.findViewById(R.id.item_image);
		mOrigine = (TextView) view.findViewById(R.id.item_origine);
		mFiliere = (TextView) view.findViewById(R.id.item_filiere);
		mNaissance = (TextView) view.findViewById(R.id.item_naissance);
		mTelFixe = (TextView) view.findViewById(R.id.item_tel_fixe);
		mTelFixe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse(trombiItem.getTelFixe()));
				startActivity(intent);
			}
		});
		mTelPort = (TextView) view.findViewById(R.id.item_tel_portable);
		mTelPort.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + trombiItem.getTelPortable()));
				startActivity(intent);
			}
		});
		mMailIIE = (TextView) view.findViewById(R.id.item_mailiie);
		mMailPerso = (TextView) view.findViewById(R.id.item_mailperso);
		mAntenne = (TextView) view.findViewById(R.id.item_antenne);
		mGroupe = (TextView) view.findViewById(R.id.item_groupe);
		mAssoces = (TextView) view.findViewById(R.id.item_assoces);

		mNom.setText(trombiItem.getNom());
		mPromo.setText(trombiItem.getPromo());
		mPhoto.setImageBitmap(trombiItem.getPhoto());
		if (trombiItem.getOrigine().length() > 0) {
			mOrigine.setText("Établissement d'origine : " + trombiItem.getOrigine());
			mOrigine.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getFiliere().length() > 0) {
			mFiliere.setText("Filière : " + trombiItem.getFiliere());
			mFiliere.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getNaissance().length() > 0) {
			mNaissance.setText("Date de naissance : " + trombiItem.getNaissance());
			mNaissance.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getTelFixe().length() > 0) {
			mTelFixe.setText("Téléphone (fixe) : " + trombiItem.getTelFixe());
			mTelFixe.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getTelPortable().length() > 0) {
			mTelPort.setText("Téléphone (port) : " + trombiItem.getTelPortable());
			mTelPort.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getMailEnsiie().length() > 0) {
			mMailIIE.setText("Mail ensiie: " + trombiItem.getMailEnsiie());
			mMailIIE.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getMailPerso().length() > 0) {
			mMailPerso.setText("Mail perso : " + trombiItem.getMailPerso());
			mMailPerso.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getAntenne().length() > 0) {
			mAntenne.setText("Etudie à : " + trombiItem.getAntenne());
			mAntenne.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getGroupe().length() > 0) {
			mGroupe.setText("Groupe : " + trombiItem.getGroupe());
			mGroupe.setVisibility(View.VISIBLE);
		}
		if (trombiItem.getAssoces().length() > 0) {
			mAssoces.setText(Html.fromHtml(trombiItem.getAssoces()));
			mAssoces.setVisibility(View.VISIBLE);
		}

		// action of the new search button
		btnReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment frag = new TrombiResultList(trombiItemsList);
				frag.setArguments(getArguments());
				getFragmentManager().beginTransaction().replace(R.id.trombi_res_content, frag).commit();
			}
		});

		return view;
	}

}