package com.iiens.net;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** TrombiItemsAdapter
	Classe permettant d'adapter les résulats de la recherche sur le trombi
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiItemsAdapter extends BaseAdapter {

	private ArrayList<TrombiItem> trombiItemsList;
	private Context context;

	public TrombiItemsAdapter(Context context, ArrayList<TrombiItem> result) {
		this.trombiItemsList = result;		
		this.context = context;
	}

	@Override
	public int getCount() {
		return trombiItemsList.size();
	}

	@Override
	public TrombiItem getItem(int arg0) {
		return trombiItemsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if(arg1==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.trombi_item, arg2,false);
		}
		
		TextView mNom = (TextView) arg1.findViewById(R.id.item_nom);
		TextView mPromo = (TextView) arg1.findViewById(R.id.item_promo);
		ImageView mPhoto = (ImageView) arg1.findViewById(R.id.item_image);
//		TextView mOrigine = (TextView) arg1.findViewById(R.id.trombi_origine);
//		TextView mFiliere = (TextView) arg1.findViewById(R.id.trombi_filiere);
//		TextView mNaissance = (TextView) arg1.findViewById(R.id.trombi_naissance);
//		TextView mTelFixe = (TextView) arg1.findViewById(R.id.trombi_tel_fixe);
//		TextView mTelPortable = (TextView) arg1.findViewById(R.id.trombi_tel_portable);
//		TextView mMailIIE = (TextView) arg1.findViewById(R.id.trombi_mailiie);
//		TextView mMailPerso = (TextView) arg1.findViewById(R.id.trombi_mailperso);
//		TextView mAntenne = (TextView) arg1.findViewById(R.id.trombi_antenne);
//		TextView mGroupe = (TextView) arg1.findViewById(R.id.trombi_groupe);

		if (trombiItemsList.size() > 0) {
			TrombiItem trombiItem = trombiItemsList.get(arg0);
			mNom.setText(trombiItem.getNom());
			mPromo.setText(trombiItem.getPromo());
			mPhoto.setImageBitmap(trombiItem.getPhoto());
//			mOrigine.setText(trombiItem.getOrigine());
//			mFiliere.setText(trombiItem.getFiliere());
//			mNaissance.setText(trombiItem.getNaissance());
//			mTelFixe.setText(trombiItem.getTelFixe());
//			mTelPortable.setText(trombiItem.getTelPortable());
//			mMailIIE.setText(trombiItem.getMailEnsiie());
//			mMailPerso.setText(trombiItem.getMailPerso());
//			mAntenne.setText(trombiItem.getAntenne());
//			mGroupe.setText(trombiItem.getGroupe());
		}

		return arg1;
	}

}