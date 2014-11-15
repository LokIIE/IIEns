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

public class EdtItemsAdapter extends BaseAdapter {

	private List<EdtItem> edtItemsList = new ArrayList<EdtItem>();
	private String[] heures = {"", "9h00 - 10h45", "11h00 - 12h45", "13h00 - 14h00", "14h00 - 15h45", "16h00 - 17h45", "18h00 - 19h45"};
	private Context context;

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
			String horaire  =  heures[edtItem.getHeure()];
			String type = edtItem.getType();
			String matiere = edtItem.getMatiere();
			String salle = edtItem.getSalle();
			String prof = edtItem.getProf();
			String groupe = edtItem.getGroupe();

			if (salle.equals("2")) {salle = "amphi 2";}
			if (prof.length() > 0) {prof = " avec " + prof;}

			//if (arg1 == null) {
				if (type.equals("Cours")) arg1 = inflater.inflate(R.layout.edt_item_cours, arg2,false);
				else if (type.equals("contrôle")) arg1 = inflater.inflate(R.layout.edt_item_controle, arg2,false);
				else if (type.equals("Cours_td")) { 
					arg1 = inflater.inflate(R.layout.edt_item_courstd, arg2,false);
					type = "Cours-TD";
				}
				else if (type.equals("T.P.")) arg1 = inflater.inflate(R.layout.edt_item_tp, arg2,false);
				else if (type.equals("T.D.")) arg1 = inflater.inflate(R.layout.edt_item_td, arg2,false);
			//}

			TextView mEdtItem = (TextView) arg1.findViewById(R.id.edt_item_content);

			if (edtItem.getGroupe().length() > 0) {
				groupe = " pour " + groupe;
			}
			if (edtItem.getGroupe().startsWith("op")) {
				groupe = "";
			}

			mEdtItem.setText(Html.fromHtml("<b>" + horaire + "</b>" + " : " + type + " de " + matiere + " en " + salle + prof + groupe));
		}
		
		return arg1;

	}

}