package com.iiens.net;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.iiens.net.adapter.EdtItemsAdapter;
import com.iiens.net.model.EdtItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Fragment d'affichage des événements d'une journée de l'emploi du temps
 */

public class EdtResultPage extends Fragment {

    /**
     * Liste des items affichés sur la page
     */
    private ArrayList<EdtItem> edtItemsList;

    /**
     * Constructeur
     */
    public EdtResultPage () {
        this.edtItemsList = new ArrayList<>();
    }

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setRetainInstance( true );
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        View view = inflater.inflate( R.layout.edt_result_page, container, false );
        ListView mListView = (ListView) view.findViewById( R.id.listview );

        mListView.setAdapter( new EdtItemsAdapter( getActivity().getApplicationContext(), edtItemsList ) );
        mListView.setClickable( true );
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                dialogAlertForCalendar( position );
            }
        });

        return view;
    }

    /**
     * Ajout d'un EdtItem à la liste des items de la page
     * @param item EdtItem à ajouter
     */
    public void addItem ( EdtItem item ) {

        this.edtItemsList.add( item );
    }

    /**
     * Affichage d'une fenêtre pour l'ajout de l'événement sélectionné à l'agenda
     * @param position Position de l'item dans la liste
     */
    private void dialogAlertForCalendar ( final int position ) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );

        // Titre de la fenêtre
        alertDialogBuilder.setTitle( R.string.export_to_calendar_title );

        // Message de la fenêtre et action des boutons
        alertDialogBuilder
                .setMessage( R.string.export_item_to_calendar_msg )
                .setCancelable( false )
                .setPositiveButton( R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exportToCalendar( edtItemsList.get( position ) );
                    }
                })
                .setNegativeButton( R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // Création et affichage de la fenêtre
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Export de l'item dans le calendrier
     * @param item Item à exporter
     */
    private void exportToCalendar ( EdtItem item ) {

        Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
        Intent intent = new Intent( Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI );
        String[] hDebut = item.getHeureDebut().split( "h" );
        String[] hFin = item.getHeureFin().split( "h" );

        intent.putExtra( CalendarContract.Events.TITLE, item.getTitre() );
        cal.set(
                Integer.valueOf( item.getJour().substring( 0, 4 ) ),     // year
                Integer.valueOf( item.getJour().substring( 5, 7 ) ) - 1, // month
                Integer.valueOf( item.getJour().substring( 9, 10 ) ),    // day
                Integer.valueOf( hDebut[0] ) - 1,                      // hour
                Integer.valueOf( hDebut[1] )                           // minutes
        );
        intent.putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTime().getTime() );
        cal.set(
                Integer.valueOf( item.getJour().substring( 0, 4 ) ),     // year
                Integer.valueOf( item.getJour().substring( 5, 7 ) ) - 1, // month
                Integer.valueOf( item.getJour().substring( 9, 10 ) ),    // day
                Integer.valueOf( hFin[0] ) - 1,                        // hour
                Integer.valueOf( hFin[1] )                             // minutes
        );
        intent.putExtra( CalendarContract.EXTRA_EVENT_END_TIME, cal.getTime().getTime() );
        intent.putExtra( CalendarContract.Events.EVENT_LOCATION, "ENSIIE - " + item.getLieu() );
        intent.putExtra( CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE );
        startActivity( intent );
    }
}