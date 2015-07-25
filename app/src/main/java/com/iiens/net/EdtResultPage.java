package com.iiens.net;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * EdtResultPage
 * Fragment affichant les résultats de la recherche sur l'edt pour un jour donné
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

public class EdtResultPage extends Fragment {

    private final String[] minutes = {"00", "15", "30", "45"};
    private ArrayList<EdtItem> edtItemsList;

    // newInstance constructor for creating fragment with arguments
    public EdtResultPage() {
    }

    @SuppressLint("ValidFragment")
    public EdtResultPage(ArrayList<EdtItem> dayItems) {
        this.edtItemsList = dayItems;
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

        View view = inflater.inflate(R.layout.edt_result_page, container, false);
        ListView mListView = (ListView) view.findViewById(R.id.listview);
        view.findViewById(R.id.progress_spinner).setVisibility(View.GONE);

        mListView.setAdapter(new EdtItemsAdapter(getActivity().getApplicationContext(), edtItemsList));
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                dialogAlertForCalendar(position);
            }
        });

        return view;
    }

    private void dialogAlertForCalendar(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle(R.string.export_to_calendar_title);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.export_item_to_calendar_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exportToCalendar(edtItemsList.get(position));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void exportToCalendar(EdtItem item) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.Events.TITLE, item.getTitre());
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),     // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1, // month
                Integer.valueOf(item.getJour().substring(9, 10)),    // day
                item.getHeure() / 4 - 1,                              // hour
                Integer.valueOf(minutes[item.getHeure() % 4])         // minutes
        );
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTime().getTime());
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),                 // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1,             // month
                Integer.valueOf(item.getJour().substring(9, 10)),                // day
                (item.getHeure() + item.getDuree()) / 4 % 24 - 1,                   // hour
                Integer.valueOf(minutes[(item.getHeure() + item.getDuree()) % 4])   // minutes
        );
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTime().getTime());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "ENSIIE - " + item.getLieu());
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        startActivity(intent);
    }
}