package com.iiens.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * EdtItemsAdapter
 * Classe adaptant les items pour l'affichage de l'edt
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

@SuppressLint("DefaultLocale")
class EdtItemsAdapter extends BaseAdapter {

    private final Context context;
    private final String[] minutes = {"00", "15", "30", "45"};
    private List<EdtItem> edtItemsList = new ArrayList<>();

    public EdtItemsAdapter(Context context, ArrayList<EdtItem> edtItemsList) {
        this.edtItemsList = edtItemsList;
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

        arg1 = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (edtItemsList.size() != 0) {
            EdtItem edtItem = edtItemsList.get(arg0);
            String hDebut = String.valueOf(edtItem.getHeure() / 4) + "h" + minutes[edtItem.getHeure() % 4];
            String hFin = String.valueOf((edtItem.getDuree() + edtItem.getHeure()) / 4 % 24) + "h" + minutes[(edtItem.getDuree() + edtItem.getHeure()) % 4];

            // Depending on the type of the item, choose the appropriate layout
            String type = edtItem.getType();
            switch (type) {
                case "Cours":
                    arg1 = inflater.inflate(R.layout.edt_item_cours, arg2, false);
                    break;
                case "contrôle":
                    arg1 = inflater.inflate(R.layout.edt_item_controle, arg2, false);
                    break;
                case "Cours_td":
                    arg1 = inflater.inflate(R.layout.edt_item_courstd, arg2, false);
                    type = "Cours-TD";
                    break;
                case "T.P.":
                    arg1 = inflater.inflate(R.layout.edt_item_tp, arg2, false);
                    break;
                case "T.D.":
                    arg1 = inflater.inflate(R.layout.edt_item_td, arg2, false);
                    break;
                case "assoce":
                    arg1 = inflater.inflate(R.layout.edt_item_club, arg2, false);
                    break;
            }

            // Set groupe
            String groupe = edtItem.getGroupe();
            if (edtItem.getGroupe().length() > 0) {
                groupe = " pour " + groupe;
            }
            if (edtItem.getGroupe().startsWith("op")) {
                groupe = "";
            }

            // Set salle and take into account special cases
            String lieu = edtItem.getLieu();
            if (lieu.length() > 0) {
                if (type.equals("assoce") && !lieu.matches("[0-9]*")) lieu = " au " + lieu;
                else if (lieu.equals("2")) lieu = " en amphi 2";
                else lieu = " en " + lieu;
            }

            // Set prof
            String auteur = edtItem.getAuteur();
            if (auteur.length() > 0) {
                if (type.equals("assoce")) {
                    if (auteur.equals("aeiie")) auteur = "BdE";
                    else if (auteur.equals("manga")) auteur = "Bakaclub";
                    auteur = " par " + auteur;
                } else auteur = " avec " + auteur;
            }

            // Set titre and take into account special cases
            String titre = edtItem.getTitre();
            if (titre.length() > 0) {
                if (titre.equals("Conférences")) {
                    titre = "Cycle de conférences";
                    auteur = "";
                    type = "";
                } else if (!type.equals("assoce")) titre = " de " + titre;
            }

            TextView mEdtItem = (TextView) arg1.findViewById(R.id.edt_item_content);
            if (type.equals("assoce")) {
                mEdtItem.setText(Html.fromHtml("<b>" + hDebut + "-" + hFin + "</b>" + " : " + titre + lieu + auteur));
            } else
                mEdtItem.setText(Html.fromHtml("<b>" + hDebut + "-" + hFin + "</b>" + " : " + type + titre + lieu + auteur + groupe));
        }

        return arg1;

    }
}