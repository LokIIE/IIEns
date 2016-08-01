package com.iiens.net;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.iiens.net.database.EdtOptDb;
import com.iiens.net.model.EdtItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Requête et traitement des résultats de la recherche de l'emploi du temps pour affichage
 */
public class EdtResult extends BaseFragment{

    /**
     * Bundle contenant les paramètres et/ou les résultats de la recherche
     */
    private Bundle bundle = new Bundle();

    /**
     * Liste des items à afficher
     */
    private ArrayList<EdtItem> edtItemsList;

    /**
     * Paramètres de recherche
     */
    private ArrayList<String> searchParams;

    /**
     * Semaine à rechercher
     */
    private String requestWeek;

    /**
     * Promotion à rechercher
     */
    private String requestPromo;

    /**
     * Parseur du format de la date d'un événement enregistré
     */
    private static SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    /**
     * Parseur du format de la date d'un événement affiché
     */
    private static SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMMM", Locale.FRANCE);

    /**
     * Clé des résultats dans le bundle
     */
    private static final String bundleKey = "edtResult";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey("edtResultBundle")) {
                this.bundle = savedInstanceState.getBundle("edtResultBunle");
        } else {
            this.bundle = getArguments();
        }
        this.layoutId = R.layout.viewpager;
    }

    @Override
    protected void generateView(View view) {
        // Ajout de l'action du bouton de nouvelle recherche
        Button btnNewSearch = (Button) view.findViewById(R.id.edt_newsearch_button);
        btnNewSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        // Récupération des paramètres de la recherche
        searchParams = bundle.getStringArrayList("edtParams");
        assert searchParams != null;
        requestWeek = searchParams.get(0);
        requestPromo = searchParams.get(1);

        // Gestion de la pagination des fragments
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.edt_pager);
        vpPager.setAdapter(new EdtResultPagerAdapter(getActivity().getFragmentManager(), requestWeek));

        JSONArray jEdtItems;
        try {
            if (bundle.containsKey(bundleKey)) {
                jEdtItems = new JSONArray(bundle.getString(bundleKey));
            } else {
                EdtGetRequest getEdt = new EdtGetRequest(this.context, requestWeek, requestPromo);
                jEdtItems = getEdt.execute().get();
            }

            if (jEdtItems == null || jEdtItems.length() == 0) {
                throw new NullPointerException();
            } else {
                bundle.putString(bundleKey, jEdtItems.toString());
                // Transforme JSONArray en ArrayList
                edtItemsList = jArrayToArrayList(jEdtItems, searchParams);

                // Range les événements suivant le jour
                for (EdtItem item : edtItemsList) {
                    ((EdtResultPagerAdapter) vpPager.getAdapter()).addItem(item);
                }
            }

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(global, getResources().getString(R.string.edtForm_noResult), Toast.LENGTH_LONG).show();
        }}

    @Override
    protected void displayResult(View view, JSONArray result) {}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        context.getMenuInflater().inflate(R.menu.edt_result_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if an item of the right action menu was selected, handle it accordingly
        switch (item.getItemId()) {
            case R.id.export_to_calendar:
                // exportWeekToAgenda();
                dialogAlertForCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("edtResultBundle", bundle);
        super.onSaveInstanceState(outState);
    }

    private ArrayList<EdtItem> jArrayToArrayList(JSONArray jArray, ArrayList<String> searchParams) {
        ArrayList<EdtItem> edtItemsList = new ArrayList<>();
        ArrayList<String> groupFilter = new ArrayList<>(searchParams.subList(2, searchParams.size()));

        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                EdtItem item = EdtItem.mapJsonObject(json_data);
                if (item.getGroupe().equals("") || filterGroup(item, groupFilter)) {
                    edtItemsList.add(item);
                }
            }
        } catch (JSONException e) {
            Log.e("edtResult", "Error parsing data " + e.toString());
        }

        return edtItemsList;
    }


    // Filtre les cours/td en groupe et n'affiche que le groupe ou le sous-groupe demandé par l'utilisateur
    private boolean filterGroup(EdtItem edtItem, ArrayList<String> filter) {
        String groupe = edtItem.getGroupe();
        boolean filterEmpty = true;
        for (String groupFilter : filter) {
            if (groupFilter != null) {
                filterEmpty = false;

                if (!groupe.equals("") && (groupe.startsWith(groupFilter) || groupFilter.startsWith(groupe))) {
                    return true;
                }
            }
        }

        return filterEmpty;
    }

    private void dialogAlertForCalendar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

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
                        if (bundle.getStringArray("edtParams")[0].equals(""))
                            dialogAlertPickGroup(bundle.getStringArrayList("edtParams"));
                        else exportWeekToCalendar(bundle.getStringArrayList("edtParams"));
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

    /**
     * Fenêtre permettant à l'utilisateur de filtrer les événements à exporter selon le groupe
     * @param filtre Paramètres de la recherche
     */
    private void dialogAlertPickGroup(final ArrayList<String> filtre) {
        EdtOptDb dal = new EdtOptDb(this.context);
        final CharSequence[] groupList = (CharSequence[]) dal.getSpinnerItems(0).toArray();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        alertDialogBuilder.setTitle(getResources().getString(R.string.export_week_to_calendar_chose_group))
                .setItems(groupList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        filtre.set(0, groupList[which].toString());
                        exportWeekToCalendar(filtre);
                    }
                });
        alertDialogBuilder.create().show();
    }

    /**
     * Export d'une semaine vers le calendrier
     * @param filtre Paramètres de la recherche
     */
    private void exportWeekToCalendar(ArrayList<String> filtre) {
        CalendarExport calExport = new CalendarExport(this.context);

        for (EdtItem item : edtItemsList) {
            if (filterGroup(item, filtre)) {
                calExport.addItemToCalendar(item);
            }
        }

        // Toast de confirmation de l'export
        Toast.makeText(this.context, R.string.export_week_to_calendar_done, Toast.LENGTH_SHORT).show();
    }

    /**
     * Classe gérant la pagination des fragments
     */
    protected static class EdtResultPagerAdapter extends FragmentPagerAdapter {
        /**
         * Nombre de fragments / pages
         */
        private static final int NUM_ITEMS = 5;

        /**
         * Liste des fragments / pages
         */
        private List<EdtResultPage> edtPageList = new ArrayList<>();

        /**
         * Liste des jours servant d'en-tête aux fragments
         */
        private static String[] days = new String[5];

        /**
         * Constructeur
         * @param fragmentManager FragmentManager
         * @param requestWeek Semaine choisie
         */
        public EdtResultPagerAdapter(FragmentManager fragmentManager, String requestWeek) {
            super(fragmentManager);

            for (int i = 0; i < NUM_ITEMS; i++) {
                edtPageList.add(new EdtResultPage());
            }

            Calendar myCalendar = Calendar.getInstance(Locale.FRENCH);
            myCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            myCalendar.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(requestWeek));

            for (int i = 0; i < days.length; i++) {
                days[i] = formatter.format(myCalendar.getTime());
                myCalendar.add(Calendar.DATE, 1);
            }
        }

        /**
         * Retourne le nombre de pages
         * @return Nombre de pages
         */
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        /**
         * Retourne un fragment selon la position
         * @param position Position du fragment
         * @return Fragment correspondant à la position
         */
        @Override
        public Fragment getItem(int position) {
            return edtPageList.get(position);
        }

        /**
         * Ajout d'un événement dans le fragment correspondant
         * @param item Item à ajouter
         */
        public void addItem (EdtItem item) {
            try {
                String jour = formatter.format(parser.parse(item.getJour()));
                String[] joursFr = {"lundi", "mardi", "mercredi", "jeudi", "vendredi"};

                for (int i = 0; i < joursFr.length; i++) {
                    if (jour.contains(joursFr[i])) {
                        edtPageList.get(i).addItem(item);
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * Retourne le titre d'un fragment selon la position
         * @param position Position du fragment
         * @return Titre du fragment
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return days[position];
        }
    }
}