package com.iiens.net;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.iiens.net.model.EdtFormItemv2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Formulaire de recherche de l'emploi du temps
 */
public class Edtv2 extends BaseFragment {

    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mComm, mLangue, mOptLayout;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner;
    private ArrayAdapter<EdtFormItemv2> mGroupAdapter, mCommAdapter, mLangAdapter;
    private int currentWeekNumber = 1;
    private Bundle bundle;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        bundle = global.getBundle();

        this.apiKey = "edtFormScrapper";
        this.layoutId = R.layout.edt_formulaire_v2;
    }

    protected void generateView ( View view ) {

        // Affichage des semaines dans le spinner
        mEdtWeekSpinner = (Spinner) view.findViewById(R.id.edt_week);
        mEdtWeekSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                generateWeeks()));
        mEdtWeekSpinner.setSelection(2);

        // Références des layouts
        mOptLayout = (LinearLayout) view.findViewById( R.id.edt_options );
        mComm = (LinearLayout) view.findViewById( R.id.edt_comm_layout );
        mLangue = (LinearLayout) view.findViewById( R.id.edt_langue_layout );

        // Références des spinners
        mGroupSpinner = (Spinner) view.findViewById( R.id.edt_groupe );
        mCommSpinner = (Spinner) view.findViewById( R.id.edt_comm );
        mLangSpinner = (Spinner) view.findViewById( R.id.edt_lang );

        // Initialialisation des spinners
        this.initializeAdapters();
        this.initializeSpinners();

        this.apiRequest( view );

        // Références des radio boutons
        /*radioPromoGroup = (RadioGroup) view.findViewById( R.id.chk_promo );
        radio1A = (RadioButton) view.findViewById( R.id.chk_1A );
        radio2A = (RadioButton) view.findViewById( R.id.chk_2A );
        radio3A = (RadioButton) view.findViewById( R.id.chk_3A );

        // Affichage ou non des layouts ou spinners suivant le radio bouton choisi
        radio1A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });

        radio2A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });

        radio3A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // Action du bouton de recherche
        view.findViewById( R.id.edt_search_button ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération de tous les paramètres de recherche saisis
                EdtForm edtForm = new EdtForm();

                // Promotion, obligatoire sinon annulation
                int selectedRadioId = radioPromoGroup.getCheckedRadioButtonId();
                if ( selectedRadioId > 0 ) {

                    if ( selectedRadioId == radio1A.getId() ) {

                        edtForm.promoNumber = 1;

                    } else if ( selectedRadioId == radio2A.getId() ) {

                        edtForm.promoNumber = 2;

                    } else if ( selectedRadioId == radio3A.getId() ) {

                        edtForm.promoNumber = 3;
                    }

                } else {

                    Toast.makeText( global, R.string.edtForm_choseGroup, Toast.LENGTH_LONG ).show();
                    return;
                }

                // Transition d'activité pour effectuer la recherche et afficher les résultats
                if ( global.isOnline() ) {

                    bundle.putStringArrayList( "edtParams", edtForm.toArrayList() );
                    executeSearch();

                } else {

                    Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
                }
            }

        });*/
    }

    /**
     * Création du dynamique du formulaire de l'emploi du temps
     * @param view Vue à remplir
     * @param result Données à afficher
     */
    @Override
    protected void displayResult ( View view, JSONArray result ) {

        try {
            JSONArray grpElements = (JSONArray) result.getJSONObject( 0 ).getJSONArray( "gp[]" ).get(0);
            Log.d( "DATA", grpElements.toString() + " - " + grpElements.length() );
            for ( int i = 0; i < grpElements.length(); i++ ) {

                JSONObject option = grpElements.getJSONObject( i );
                mGroupAdapter.add( new EdtFormItemv2( "gp[]", option.getString( "text" ), option.getString( "value" ) ) );
            }

            mGroupAdapter.notifyDataSetChanged();

        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Génération de la liste des libellés des semaines du spinner du formulaire
     * @return Liste des libellés des semaines
     */
    private List<String> generateWeeks () {

        List<String> spinnerItems = new ArrayList<>();
        Calendar myCalendar = Calendar.getInstance( Locale.FRENCH );
        myCalendar.setFirstDayOfWeek( Calendar.MONDAY );
        currentWeekNumber = myCalendar.get( Calendar.WEEK_OF_YEAR );
        myCalendar.add( Calendar.WEEK_OF_YEAR, -2 );
        SimpleDateFormat monthName = new SimpleDateFormat( "MMM", Locale.FRENCH );

        for (int i = 0; i < 11; i++) {

            myCalendar.setFirstDayOfWeek( Calendar.MONDAY );

            myCalendar.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
            String lundi = String.valueOf( myCalendar.get( Calendar.DAY_OF_MONTH ) ) + " " + monthName.format( myCalendar.getTime() );

            myCalendar.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
            String vendredi = String.valueOf( myCalendar.get( Calendar.DAY_OF_MONTH ) ) + " " + monthName.format( myCalendar.getTime() );

            spinnerItems.add("Du " + lundi + " au " + vendredi);
            myCalendar.add( Calendar.DAY_OF_MONTH, 7 );
        }

        return spinnerItems;
    }

    /**
     * Exécution de la recherche et affichage des résultats
     */
    private void executeSearch () {

        FragmentManager fm = this.getActivity().getFragmentManager();

        // Création fragment détail
        EdtResult resultFrag = new EdtResult();

        // Envoi de l'item sélectionné au fragment
        resultFrag.setArguments( bundle );

        fm.beginTransaction()
                .replace( R.id.content_container, resultFrag )
                .addToBackStack( null )
                .commit();

        global.setCurrentFragment( new EdtResult() );
    }

    /**
     * Action effectuée lors du changement d'orientation
     * @param outState Bundle de stockage des données
     */
    @Override
    public void onSaveInstanceState ( Bundle outState ) {

        super.onSaveInstanceState( outState );
        outState.putAll( bundle );
    }

    /**
     * Initialisation des adapters des spinners
     */
    private void initializeAdapters () {

        // Initialisation des spinners avec des données constantes
        mGroupAdapter = new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
        mCommAdapter = new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
        mLangAdapter = new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
    }

    /**
     * Affectation des adapters aux spinners
     */
    private void initializeSpinners () {

        mGroupSpinner.setAdapter( mGroupAdapter );
        mCommSpinner.setAdapter( mCommAdapter );
    }

    /**
     * Chargement des spinners avec les données des options des 2A
     */
    private void loadOptionSpinners2A () {

    }

    /**
     * Chargement des spinners avec les données des options des 3A
     */
    private void loadOptionSpinners3A () {

    }
}