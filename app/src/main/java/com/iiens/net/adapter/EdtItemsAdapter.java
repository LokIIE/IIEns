package com.iiens.net.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iiens.net.R;
import com.iiens.net.model.EdtItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Mise en forme des items de la recherche de l'emploi du temps pour affichage
 */

public class EdtItemsAdapter extends BaseAdapter {

    private final Context context;
    private List<EdtItem> edtItemsList = new ArrayList<>();

    public EdtItemsAdapter ( Context context, ArrayList<EdtItem> edtItemsList ) {

        this.edtItemsList = edtItemsList;
        this.context = context;
    }

    @Override
    public int getCount () {

        return edtItemsList.size();
    }

    @Override
    public EdtItem getItem ( int arg0 ) {

        return edtItemsList.get(arg0);
    }

    @Override
    public long getItemId ( int arg0 ) {

        return arg0;
    }

    @Override
    public View getView ( int arg0, View arg1, ViewGroup arg2 ) {

        arg1 = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        if ( edtItemsList.size() != 0 ) {

            EdtItem edtItem = edtItemsList.get( arg0 );
            String hDebut = edtItem.getHeureDebut();
            String hFin = edtItem.getHeureFin();

            // Depending on the type of the item, choose the appropriate layout
            String type = edtItem.getType();
            Resources res = context.getResources();

            // Set resource and chose color accordingly
            arg1 = inflater.inflate( R.layout.edt_item, arg2, false );
            TextView mEdtItemType = (TextView) arg1.findViewById( R.id.edt_item_type );

            if ( type.equals( res.getString(R.string.edtType_cours) ) ) {
                applyTxtViewStyle( arg1, R.color.coursback, R.color.courstxt );
                mEdtItemType.setText( res.getString(R.string.edtItemType_cours) );

            } else if (type.equals( res.getString(R.string.edtType_tp) ) ) {

                applyTxtViewStyle( arg1, R.color.tpback, R.color.tptxt );
                mEdtItemType.setText( res.getString(R.string.edtItemType_tp) );

            } else if ( type.equals(res.getString(R.string.edtType_td)) ) {

                applyTxtViewStyle( arg1, R.color.tdback, R.color.tdtxt );
                mEdtItemType.setText( res.getString(R.string.edtItemType_td) );

            } else if (type.equals( res.getString(R.string.edtType_assoce) ) ) {

                applyTxtViewStyle( arg1, R.color.clubback, R.color.clubtxt );
                mEdtItemType.setText( res.getString(R.string.edtItemType_assoce) );

            } else if ( type.equals( res.getString(R.string.edtType_controle) ) ) {

                applyTxtViewStyle( arg1, R.color.red, R.color.white );
                mEdtItemType.setText( res.getString(R.string.edtItemType_controle) );

            } else {

                applyTxtViewStyle( arg1, R.color.courstdback, R.color.courstdtxt );
                mEdtItemType.setText( res.getString(R.string.edtItemType_cours_td) );
            }

            // Set groupe
            String groupe = edtItem.getGroupe();
            if ( groupe.length() > 0 ) {

                if ( edtItem.getGroupe().startsWith( "op" ) ) {

                    groupe = "";

                } else {

                    groupe = " pour " + groupe;
                }
            }

            // Set salle and take into account special cases
            String lieu = edtItem.getLieu();
            if ( lieu.length() > 0 && lieu.equals( "2" ) ) lieu = "Amphi 2";

            // Set prof
            String auteur = edtItem.getAuteur();
            if ( auteur.length() > 0 ) {

                if ( type.equals( res.getString(R.string.edtType_assoce) ) ) {

                    if ( auteur.equals( "aeiie" ) ) auteur = "BdE";
                    else if ( auteur.equals( "manga" ) ) auteur = "Bakaclub";
                    auteur = " par " + auteur;

                } else auteur = " avec " + auteur;
            }

            // Set titre and take into account special cases
            String titre = edtItem.getTitre();
            if ( titre.length() > 0 ) {

                if ( titre.equals( "Conférences" ) ) {

                    titre = "Cycle de conférences";
                    auteur = "";
                    type = "";
                }
            }

            TextView mEdtItem = (TextView) arg1.findViewById( R.id.edt_item_content );
            Spanned itemText;
            if ( !type.equals( "assoce" ) ) {

                itemText = Html.fromHtml(
                        String.format(
                                res.getString(R.string.edtItem_full_format),
                                hDebut,
                                hFin,
                                titre,
                                auteur,
                                groupe,
                                lieu
                        ));

            } else {

                itemText = Html.fromHtml(
                        String.format(
                                res.getString(R.string.edtItem_full_format),
                                hDebut,
                                hFin,
                                titre,
                                auteur,
                                "",
                                lieu
                        ));
            }

            mEdtItem.setText( itemText );
        }

        return arg1;
    }

    /**
     * Applique les couleurs à l'élément de l'emploi du temps
     * @param view Textview à modifier
     * @param backgroundColor Couleur de fond
     * @param textColor Couleur du texte
     */
    private void applyTxtViewStyle ( View view, int backgroundColor, int textColor ) {

        TextView txtView = (TextView) view.findViewById( R.id.edt_item_content );
        TextView typeView = (TextView) view.findViewById( R.id.edt_item_type );

        Resources colorRes = context.getResources();

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            txtView.setBackgroundColor( colorRes.getColor( backgroundColor, context.getTheme() ) );
            txtView.setTextColor( colorRes.getColor( textColor, context.getTheme() ) );

            typeView.setBackgroundColor( colorRes.getColor(textColor, context.getTheme() ) );
            typeView.setTextColor( colorRes.getColor(backgroundColor, context.getTheme() ) );

        } else {

            txtView.setBackgroundColor( colorRes.getColor( backgroundColor ) );
            txtView.setTextColor( colorRes.getColor( textColor ) );

            typeView.setBackgroundColor( colorRes.getColor( textColor ) );
            typeView.setTextColor( colorRes.getColor( backgroundColor ) );
        }
    }
}