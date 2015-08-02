package com.iiens.net;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.iiens.net.model.EdtItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * EdtResult
 * Activité traitant les résultats de la recherche sur l'edt
 */

public class EdtResult extends FragmentActivity {

    private static final String[] days = {"lundi", "mardi", "mercredi", "jeudi", "vendredi"};
    private static ArrayList<EdtItem> resultLundi, resultMardi, resultMercredi, resultJeudi, resultVendredi;
    private final String[] minutes = {"00", "15", "30", "45"};
    private Bundle bundle = new Bundle();
    private ArrayList<EdtItem> edtItemsList;
    private ViewPager vpPager;

    static private boolean isInList(String groupe, String[] list) {
        for (String authorizedGroup : list) {
            if (!authorizedGroup.equals("") && (groupe.startsWith(authorizedGroup) || authorizedGroup.startsWith(groupe))) {
                return true;
            }
        }
        return false;
    }

    static private void filterItems(JSONObject json_data, ArrayList<EdtItem> edtItemsList, String[] toKeepFilter) {
        EdtItem edtItem = new EdtItem();
        edtItem.mapJsonObject(json_data);
        String groupe = edtItem.getGroupe();

        boolean filterEmpty = true;
        for (String aToKeepFilter : toKeepFilter) {
            if (aToKeepFilter.length() > 0) filterEmpty = false;
        }

        // Filtre les cours/td en groupe et n'affiche que le groupe ou le sous-groupe demandé par l'utilisateur
        if (groupe.equals("") || filterEmpty) {
            edtItemsList.add(edtItem);
        } else {
            if (isInList(groupe, toKeepFilter)) {
                edtItemsList.add(edtItem);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            bundle.putAll(savedInstanceState.getBundle("edtResultBundle"));
        }

        String apiKey = getResources().getString(R.string.apiie_edt);
        setContentView(R.layout.viewpager);
        getActionBar().setTitle(getResources().getString(R.string.edt_result_title));

        GlobalState global = (GlobalState) getApplicationContext();
        bundle = getIntent().getBundleExtra("bundle");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String requestWeek = bundle.getString("week");
        String[] requestFilter = bundle.getStringArray("filtre");
        String requestPromo = bundle.getString("promo");
        String filename = apiKey + requestPromo;

        JSONArray jResult = new JSONArray();
        try {
            if (bundle.containsKey("edtJArrayResult")) {
                jResult = new JSONArray(bundle.getString("edtJArrayResult"));
            } else if (global.isOnline()) {
                EdtGetRequest getEdt = new EdtGetRequest(this, requestWeek, requestPromo);
                try {
                    jResult = getEdt.execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Toast.makeText(global, getResources().getString(R.string.edt_no_result), Toast.LENGTH_LONG).show();
                }

                // Save the results if storage is activated
                String currentWeek = String.valueOf(Calendar.getInstance(Locale.FRENCH).get(Calendar.WEEK_OF_YEAR));
            } else {
                Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jResult == null) { // If it is the holidays for example
            Toast.makeText(global, getResources().getString(R.string.edt_no_result), Toast.LENGTH_LONG).show();
            finish();
        } else {
            bundle.putString("edtJArrayResult", jResult.toString());
            // Transform jArray in ArrayList
            edtItemsList = jArrayToArrayList(jResult, requestFilter);

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM", Locale.FRANCE);
            resultLundi = new ArrayList<>();
            resultMardi = new ArrayList<>();
            resultMercredi = new ArrayList<>();
            resultJeudi = new ArrayList<>();
            resultVendredi = new ArrayList<>();

            // Class items depending on day
            for (EdtItem item : edtItemsList) {
                String jour = "";
                try {
                    jour = formatter.format(parser.parse(item.getJour()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (jour.contains("lundi")) resultLundi.add(item);
                else if (jour.contains("mardi")) resultMardi.add(item);
                else if (jour.contains("mercredi")) resultMercredi.add(item);
                else if (jour.contains("jeudi")) resultJeudi.add(item);
                else if (jour.contains("vendredi")) resultVendredi.add(item);
            }

            Calendar myCalendar = Calendar.getInstance(Locale.FRENCH);
            myCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            myCalendar.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(requestWeek));

            for (int i = 0; i < days.length; i++) {
                days[i] = formatter.format(myCalendar.getTime());
                myCalendar.add(Calendar.DATE, 1);
            }

            vpPager = (ViewPager) findViewById(R.id.edt_pager);
            Button btnNewSearch = (Button) findViewById(R.id.edt_newsearch_button);

            // action of the new search button
            btnNewSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edt_result_menu, menu);
        return true;
    }

    /* Determines the actions to do when an action bar item is selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if an item of the right action menu was selected, handle it accordingly
        switch (item.getItemId()) {
            case R.id.export_to_calendar:
                //exportWeekToAgenda();
                dialogAlertForCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStart() {
        super.onStart();

        FragmentPagerAdapter adapterViewPager = new EdtResultPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }

    /* Action when (for ex) the screen orientation changes */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("edtResultBundle", bundle);
        super.onSaveInstanceState(outState);
    }

    private ArrayList<EdtItem> jArrayToArrayList(JSONArray jArray, String[] filtre) {
        ArrayList<EdtItem> edtItemsList = new ArrayList<>();

        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                filterItems(json_data, edtItemsList, filtre);
            }
        } catch (JSONException e) {
            Log.e("edtResult", "Error parsing data " + e.toString());
        }

        return edtItemsList;
    }

    private void dialogAlertForCalendar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle(R.string.export_to_calendar_title);

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.export_week_to_calendar_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // we don't want the user to add the timetable for all the existing groups, only his group
                        // So we check if the value of a group was input for the search and ask the user to chose one if there is none
                        if (bundle.getStringArray("filtre")[0].equals(""))
                            dialogAlertPickGroup(bundle.getStringArray("filtre"));
                        else exportWeekToCalendar(bundle.getStringArray("filtre"));
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

    // Ask the user to chose a group (we assume it is his group) to reduce the number of items to add to the agenda
    private void dialogAlertPickGroup(final String[] filtre) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.export_week_to_calendar_chose_group))
                .setItems(R.array.edt_groups, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        filtre[0] = getResources().getStringArray(R.array.edt_groupes_raw)[which];
                        exportWeekToCalendar(filtre);
                    }
                });
        alertDialogBuilder.create().show();
    }

    private boolean eventAlreadyExists(String title, long eventTBegin, long eventTEnd) {
        // determine which fields we want in our events, like before
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };

        // retrieve the ContentResolver
        ContentResolver resolver = getContentResolver();

        // get the query uri
        Uri uri = CalendarContract.Events.CONTENT_URI;

        // filter the selection, like before
        String selection = "((" // add filters
                + CalendarContract.Events.TITLE + " = ? ) AND ( "
                + CalendarContract.Events.DTSTART + " = ? ) AND ( "
                + CalendarContract.Events.DTEND + " = ? ) "
                + ")";
        String[] selectionArgs = new String[]{ // add the filters value
                title,
                String.valueOf(eventTBegin),
                String.valueOf(eventTEnd),
        };

        // resolve the query, this time also including a sort option
        Cursor eventCursor = resolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        int nEvents = eventCursor.getCount();
        eventCursor.close(); // to free resources

        return (nEvents > 0);
    }

    private void exportWeekToCalendar(String[] filtre) {
        for (EdtItem item : edtItemsList) {
            String groupe = item.getGroupe();
            if (isInList(groupe, filtre)) {
                addItemToCalendar(item);
            }
        }
        Toast.makeText(this, R.string.export_week_to_calendar_done, Toast.LENGTH_SHORT).show();
    }

    void addItemToCalendar(EdtItem item) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // Event start time
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),     // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1, // month
                Integer.valueOf(item.getJour().substring(8, 10)),    // day
                item.getHeure() / 4 - 1,                             // hour
                Integer.valueOf(minutes[item.getHeure() % 4]),       // minutes
                0                                                    // seconds
        );
        // getTimeInMillis takes a few hundred milliseconds to compute, and it is added to the wanted value of dTStart
        // To correct it, I use Math.floor that uses a double as argument, that's why I divide by 1000
        long dTStart = (long) Math.floor(cal.getTimeInMillis() / 1000) * 1000;
        // Event end time
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),                  // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1,               // month
                Integer.valueOf(item.getJour().substring(8, 10)),                  // day
                (item.getHeure() + item.getDuree()) / 4 % 24 - 1,                  // hour
                Integer.valueOf(minutes[(item.getHeure() + item.getDuree()) % 4]), // minutes
                0                                                                  // seconds
        );
        // Same as dTStart
        long dTEnd = (long) Math.floor(cal.getTimeInMillis() / 1000) * 1000;
        String titre = (item.getType().equals("assoce")) ? item.getAuteur() + " - " + item.getTitre() : item.getType() + " - " + item.getTitre();
        if (!eventAlreadyExists(titre, dTStart, dTEnd)) {
            // Construct event details
            String lieu = (item.getLieu().length() == 3) ? "ENSIIE - salle : " + item.getLieu() : item.getLieu();
            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, dTStart);
            values.put(CalendarContract.Events.DTEND, dTEnd);
            values.put(CalendarContract.Events.TITLE, titre);
            values.put(CalendarContract.Events.DESCRIPTION, lieu);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(CalendarContract.Events.CALENDAR_ID, 1); // Calendar to which the events will be added (1 is the default)
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);

            // Insert Event
            cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

    public static class EdtResultPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 5;

        public EdtResultPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EdtResultPage(resultLundi);
                case 1:
                    return new EdtResultPage(resultMardi);
                case 2:
                    return new EdtResultPage(resultMercredi);
                case 3:
                    return new EdtResultPage(resultJeudi);
                case 4:
                    return new EdtResultPage(resultVendredi);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return days[position];
        }

    }
}