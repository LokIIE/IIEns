package com.iiens.net;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** EdtItemsAdapter
	Classe adaptant les items pour l'affichage de l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtItemsAdapter extends BaseAdapter {

	private List<EdtItem> edtItemsList = new ArrayList<EdtItem>();
	private Context context;
	private String[] minutes = {"00", "15", "30", "45"};

	public EdtItemsAdapter(Context context, ArrayList<EdtItem> getEdt) {
		this.edtItemsList = getEdt;
		this.context = context;
	}

	@Override
	public int getCount() {
		return edtItemsList.size();
	}

	@Override
	public EdtItem getItem(int arg0) {
		return edtItemsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		// Cette ligne permet de résoudre le problème suivant :
		// lorsque l'edt s'affiche la 1ère fois (arg1 est null), tout va bien mais à CHAQUE FOIS qu'on scrolle et qu'on revient (arg1 != null), la ligne est recrée mais le layout ne correspond pas toujours
		// Raison supposée : les adresses des layouts de chaque item de la listview se mélangent lorsqu'ils sont recrées
		arg1 = null;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//		ListView mListView = (ListView) arg2.findViewById(R.id.listview);
		//		LinearLayout mSpinner = (LinearLayout) arg2.findViewById(R.id.spinner_layout);

		if (edtItemsList.size() != 0) {
			EdtItem edtItem = edtItemsList.get(arg0);
			String hDebut  =  String.valueOf(edtItem.getHeure()/4) + "h" + minutes[edtItem.getHeure()%4];
			String hFin  =  String.valueOf((edtItem.getDuree() + edtItem.getHeure())/4 % 24) + "h" + minutes[(edtItem.getDuree() + edtItem.getHeure())%4];

			// En fonction du type, on change la ressource affichée
			String type = edtItem.getType();
			if (type.equals("Cours")) arg1 = inflater.inflate(R.layout.edt_item_cours, arg2, false);
			else if (type.equals("contrôle")) arg1 = inflater.inflate(R.layout.edt_item_controle, arg2, false);
			else if (type.equals("Cours_td")) { 
				arg1 = inflater.inflate(R.layout.edt_item_courstd, arg2,false);
				type = "Cours-TD";
			}
			else if (type.equals("T.P.")) arg1 = inflater.inflate(R.layout.edt_item_tp, arg2, false);
			else if (type.equals("T.D.")) arg1 = inflater.inflate(R.layout.edt_item_td, arg2, false);
			else if (type.equals("assoce")) arg1 = inflater.inflate(R.layout.edt_item_club, arg2, false);

			// Set titre and take into account special cases
			String titre = edtItem.getTitre();
			if (titre.length() > 0) {
				if (titre.equals("Conférences")) titre = "Cycle de conférences";
				else if (!type.equals("assoce")) titre = " de " + titre;
			}

			// Set salle and take into account special cases
			String lieu = edtItem.getLieu();
			if (lieu.length() > 0) {
				if (type.equals("assoce") && !lieu.equals("2")) lieu = " au " + lieu;
				else if (lieu.equals("2")) lieu = " en amphi 2";
				else lieu = " en " + lieu;
			}

			// Set prof
			String auteur = edtItem.getAuteur();
			if (auteur.length() > 0) {
				if (type.equals("assoce")) {
					if (auteur.equals("aeiie")) auteur = "BdE";
					auteur = " par " + auteur;
				}
				else auteur = " avec " + auteur;
			}

			// Set groupe
			String groupe = edtItem.getGroupe();
			if (edtItem.getGroupe().length() > 0) {groupe = " pour " + groupe;}
			if (edtItem.getGroupe().startsWith("op")) {groupe = "";}

			TextView mEdtItem = (TextView) arg1.findViewById(R.id.edt_item_content);
			if (type.equals("assoce")) { mEdtItem.setText(Html.fromHtml("<b>" + hDebut + "-" + hFin + "</b>" + " : " + titre + lieu + auteur)); 
			} else mEdtItem.setText(Html.fromHtml("<b>" + hDebut + "-" + hFin + "</b>" + " : " + type + titre + lieu + auteur + groupe));
		}

		return arg1;

	}

}