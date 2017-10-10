package com.iiens.net;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iiens.net.model.EdtFormItemv2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Formulaire de recherche de l'emploi du temps
 */
public class Edtv2 extends BaseFragment {

    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mOptLayout;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner;
    private int currentWeekNumber = 1;
    private Bundle bundle;

    private JSONObject elements, conf, confBase;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        bundle = global.getBundle();

        this.apiKey = "edtFormScrapper";
        this.layoutId = R.layout.edt_formulaire_v2;
    }

    protected void generateView ( final View view ) {

        // Affichage des semaines dans le spinner
        mEdtWeekSpinner = view.findViewById(R.id.edt_week);
        mEdtWeekSpinner.setAdapter( new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                generateWeeks()) );
        mEdtWeekSpinner.setSelection(2);

        // Références des radio boutons
        radioPromoGroup = view.findViewById( R.id.chk_promo );
        radio1A = view.findViewById( R.id.chk_1A );
        radio2A = view.findViewById( R.id.chk_2A );
        radio3A = view.findViewById( R.id.chk_3A );

        // Références des layouts
        mOptLayout = view.findViewById( R.id.edt_options );

        // Références des spinners
        mGroupSpinner = view.findViewById( R.id.edt_groupe );
        mCommSpinner = view.findViewById( R.id.edt_comm );
        mLangSpinner = view.findViewById( R.id.edt_lang );

        // Initialialisation des spinners
        mGroupSpinner.setAdapter( new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mGroupSpinner.setTag( "gp[]" );

        mCommSpinner.setAdapter( new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mCommSpinner.setTag( "gpcomm[]" );

        mLangSpinner.setAdapter( new ArrayAdapter<EdtFormItemv2>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mLangSpinner.setTag( "langue[]" );

        // Génération dynamique des éléments du formulaire et mise en places des triggers
        this.apiRequest( view );

        // Action du bouton de recherche
        view.findViewById( R.id.edt_search_button ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération de tous les paramètres de recherche saisis
                ArrayList<String> searchParams = new ArrayList<>();

                // Promotion, obligatoire sinon annulation
                int selectedRadioId = radioPromoGroup.getCheckedRadioButtonId();
                int promoNumber = -1;
                String promoConf = "";

                if ( selectedRadioId > 0 ) {

                    if ( selectedRadioId == radio1A.getId() ) {

                        promoNumber = 1;
                        promoConf = "1A";

                    } else if ( selectedRadioId == radio2A.getId() ) {

                        promoNumber = 2;
                        promoConf = "2A";

                    } else if ( selectedRadioId == radio3A.getId() ) {

                        promoNumber = 3;
                        promoConf = "3A";
                    }

                } else {

                    Toast.makeText( global, R.string.edtForm_choseGroup, Toast.LENGTH_LONG ).show();
                    return;
                }

                // Promo
                searchParams.add( String.valueOf( promoNumber ) );

                // Semaine
                searchParams.add( String.valueOf( currentWeekNumber - 2 + mEdtWeekSpinner.getSelectedItemPosition() ) );

//                // Options
//                if ( promoNumber > 1 ) {
//
//                    try {
//
//                        JSONArray spinTags = conf.getJSONArray( promoConf );
//                        for ( int i = 0; i < spinTags.length(); i++ ) {
//
//                            Spinner spinner = view.findViewWithTag( spinTags.getString( i ) );
//                            searchParams.add( ((EdtFormItemv2) spinner.getSelectedItem()).getValue() );
//                        }
//
//                    } catch ( JSONException e ) {
//                        e.printStackTrace();
//                    }
//                }

                // Transition d'activité pour effectuer la recherche et afficher les résultats
                if ( global.isOnline() ) {

                    bundle.putStringArrayList( "edtParams", searchParams );
                    executeSearch();

                } else {

                    Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    /**
     * Création du dynamique du formulaire de l'edt suivant la version du formulaire du site web
     * @param view Vue à remplir
     * @param result Données à afficher
     */
    @Override
    protected void displayResult ( View view, JSONArray result ) {

        try {

            elements = result.getJSONObject( 0 );

            conf = elements.getJSONObject( "conf" );
            confBase = conf.getJSONObject( "base" );
            Iterator<String> confBaseKeys = confBase.keys();

            while ( confBaseKeys.hasNext() ) {
                String key = confBaseKeys.next();
                Spinner currentSpinner = view.findViewWithTag( key );
                ArrayAdapter<EdtFormItemv2> currentAdapter = (ArrayAdapter<EdtFormItemv2>) currentSpinner.getAdapter();

                // Actualisation des tags des spinners
                currentSpinner.setTag( confBase.getString( key ) );

                // Ajout des éléments du select au spinnerAdapter respectif
                JSONObject spinnerConfig = elements.getJSONObject( key );
                String label = spinnerConfig.names().get( 0 ).toString();

                JSONArray spinnerElements = spinnerConfig.getJSONArray( label );

                for ( int i = 0; i < spinnerElements.length(); i++ ) {
                    JSONObject option = spinnerElements.getJSONObject( i );
                    currentAdapter.add( new EdtFormItemv2( key, option.getString( "text" ), option.getString( "value" ) ) );
                }

                currentAdapter.notifyDataSetChanged();
            }

            // Chargement des spinners des options
            mOptLayout.addView( loadOptionSpinners( "2A" ) );
            mOptLayout.addView( loadOptionSpinners( "3A" ) );

            // Affichage ou non des layouts ou spinners suivant le radio bouton choisi et la conf actuelle
            radio1A.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        JSONArray conf1A = conf.getJSONArray("1A");
                        Log.d( "CONF1A", conf1A.toString() );
                        mOptLayout.setVisibility( View.INVISIBLE );
                        mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
                        mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );

                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
            });

            radio2A.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d( "CONF1A", conf.getJSONArray("2A").toString() );
                        mOptLayout.setVisibility( View.VISIBLE );
                        mOptLayout.findViewWithTag( "2A" ).setVisibility( View.VISIBLE );
                        mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );
                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
            });

            radio3A.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d( "CONF1A", conf.getJSONArray( "3A" ).toString() );
                        mOptLayout.setVisibility( View.VISIBLE );
                        mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
                        mOptLayout.findViewWithTag( "3A" ).setVisibility( View.VISIBLE );
                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
            });

        } catch ( JSONException e ) {
            e.printStackTrace();
        }
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
     * Chargement des spinners avec les données des options
     * @param confKey : Clé de configuration du formulaire
     */
    private LinearLayout loadOptionSpinners ( String confKey ) {

        LinearLayout optionLayout = new LinearLayout( context );
        optionLayout.setTag( confKey );
        optionLayout.setOrientation( LinearLayout.VERTICAL );
        optionLayout.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );

        try {

            JSONArray confAnnee = conf.getJSONArray( confKey );

            for ( int i = 0; i < confAnnee.length(); i++ ) {

                String spinnerKey = confAnnee.getString( i );

                if ( ! confBase.names().toString().contains( spinnerKey ) ) {

                    JSONObject spinners = elements.getJSONObject( spinnerKey );
                    Iterator<String> spinnerKeys = spinners.keys();

                    while( spinnerKeys.hasNext() ) {

                        String label = spinnerKeys.next();
                        JSONArray spinnerElements = spinners.getJSONArray( label );

                        Spinner spinner = new Spinner( context );
                        spinner.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
                        spinner.setPadding( 16, 0, 16, 0 );

                        ArrayAdapter<EdtFormItemv2> spinnerAdapter = new ArrayAdapter<>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
                        spinner.setAdapter( spinnerAdapter );
                        spinner.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {

                            @Override
                            public boolean onItemLongClick ( AdapterView<?> adapterView, View view, int i, long l ) {

                                Toast.makeText( context, ((EdtFormItemv2) adapterView.getItemAtPosition( i )).getLabel(), Toast.LENGTH_LONG ).show();
                                return false;
                            }
                        } );

                        for ( int k = 0; k < spinnerElements.length(); k++ ) {
                            final JSONObject spinnerOption = spinnerElements.getJSONObject( k );
                            spinnerAdapter.add( new EdtFormItemv2( spinnerKey, spinnerOption.getString( "text" ), spinnerOption.getString( "value" ) ) );
                        }

                        LinearLayout spinnerView = new LinearLayout( context );
                        spinnerView.setOrientation( LinearLayout.HORIZONTAL );
                        spinnerView.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );

                        TextView spinnerLabel = new TextView( context );
                        spinnerLabel.setText( label );

                        spinnerView.addView( spinnerLabel );
                        spinnerView.addView( spinner );

                        optionLayout.addView( spinnerView );

                        spinnerAdapter.notifyDataSetChanged();
                    }
                }
            }

        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        return optionLayout;
    }
}