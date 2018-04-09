package com.iiens.net;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.iiens.net.database.AppDb;
import com.iiens.net.database.EdtSearchCategoryDao;
import com.iiens.net.database.EdtSearchOptionDao;
import com.iiens.net.model.EdtSearchCategory;
import com.iiens.net.model.EdtSearchOption;

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
public class EdtSearch extends BaseFragment {

    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mOptLayout;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner;
    private int currentWeekNumber = 1;
    private Bundle bundle;

    private JSONObject elements, conf, confBase;

    private EdtSearchCategoryDao dal;
    private EdtSearchOptionDao dalOptions;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        bundle = global.getBundle();

        this.apiKey = "edtFormScrapper";
        this.layoutId = R.layout.edt_search_form;

        this.dal = AppDb.getAppDb( context ).edtSearchCategoryDao();
        this.dalOptions = AppDb.getAppDb( context ).edtSearchOptionDao();
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
        mGroupSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mGroupSpinner.setTag( "gp[]" );

        mCommSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mCommSpinner.setTag( "gpcomm[]" );

        mLangSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mLangSpinner.setTag( "langue[]" );

        // Action du bouton de recherche
        view.findViewById( R.id.edt_search_button ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération de tous les paramètres de recherche saisis
                ArrayList<String> searchParams = new ArrayList<>();

                // Promotion, obligatoire sinon annulation
                int selectedRadioId = radioPromoGroup.getCheckedRadioButtonId();
                int promoNumber = -1;

                if ( selectedRadioId > 0 ) {

                    if ( selectedRadioId == radio1A.getId() ) {

                        promoNumber = 1;

                    } else if ( selectedRadioId == radio2A.getId() ) {

                        promoNumber = 2;

                    } else if ( selectedRadioId == radio3A.getId() ) {

                        promoNumber = 3;
                    }

                } else {

                    Toast.makeText( global, R.string.edtForm_choseGroup, Toast.LENGTH_LONG ).show();
                    return;
                }

                // Promo
                searchParams.add( String.valueOf( promoNumber ) );

                // Semaine
                searchParams.add( String.valueOf( currentWeekNumber - 2 + mEdtWeekSpinner.getSelectedItemPosition() ) );

                // Transition d'activité pour effectuer la recherche et afficher les résultats
                if ( global.isOnline() ) {

                    bundle.putStringArrayList( "edtParams", searchParams );

                    FragmentManager fm = getActivity().getFragmentManager();

                    // Création fragment détail
                    EdtView resultFrag = new EdtView();

                    // Envoi de l'item sélectionné au fragment
                    resultFrag.setArguments( bundle );

                    fm.beginTransaction()
                            .replace( R.id.content_container, resultFrag )
                            .addToBackStack( null )
                            .commit();

                    global.setCurrentFragment( new EdtView() );

                } else {

                    Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
                }
            }
        });

        // Génération dynamique des éléments du formulaire et mise en places des triggers
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
        EdtSearchCategory firstItem = dal.getFirst();

        try {

            if ( global.isOnline() && ( preferences.getBoolean( getResources().getString( R.string.bool_edtSearch_update_name ), false ) || firstItem == null ) ) {

                // Récupération configuration formulaire et stockage local
                dal.deleteAll();
                this.apiRequest( view );
                Log.e( "EdtSearch", "from web" );

            } else if( firstItem != null ) {

                this.generateView( view );

            } else {

                // If no connection or data stored, can't do anything
                Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
            }

        } catch ( Exception e ) {

            e.printStackTrace();
        }
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
                ArrayAdapter<EdtSearchOption> currentAdapter = (ArrayAdapter<EdtSearchOption>) currentSpinner.getAdapter();

                EdtSearchCategory edtCategory = dal.getByValue( key );
                if( edtCategory == null ) {

                    edtCategory = new EdtSearchCategory();
                    edtCategory.setPromo( "" );
                    edtCategory.setLabel( "" );
                    edtCategory.setName( key );
                    edtCategory.setValue( key );
                    dal.insert( edtCategory );

                    edtCategory = dal.getByValue( key );
                }

                // Actualisation des tags des spinners
                currentSpinner.setTag( confBase.getString( key ) );

                // Ajout des éléments du select au spinnerAdapter respectif
                JSONObject spinnerConfig = elements.getJSONObject( key );
                String label = spinnerConfig.names().get( 0 ).toString();

                Log.d( "label", label );

                JSONArray spinnerElements = spinnerConfig.getJSONArray( label );

                for ( int i = 0; i < spinnerElements.length(); i++ ) {
                    JSONObject option = spinnerElements.getJSONObject( i );

                    EdtSearchOption edtOption = new EdtSearchOption( i, option.getString( "text" ), option.getString( "value" ) );
                    dalOptions.insert( edtOption );

                    currentAdapter.add( edtOption );
                }

                currentAdapter.notifyDataSetChanged();
            }

            // Chargement des spinners des options
            mOptLayout.addView( loadOptionSpinners( "2A" ) );
            mOptLayout.addView( loadOptionSpinners( "3A" ) );

            // Affichage ou non des layouts ou spinners suivant le radio bouton choisi et la conf actuelle
            radio1A.setOnClickListener( v -> {
                try {

                    JSONArray conf1A = conf.getJSONArray("1A");
                    Log.d( "CONF1A", conf1A.toString() );
                    mOptLayout.setVisibility( View.INVISIBLE );
                    mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
                    mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            } );

            radio2A.setOnClickListener( v -> {
                try {
                    Log.d( "CONF1A", conf.getJSONArray("2A").toString() );
                    mOptLayout.setVisibility( View.VISIBLE );
                    mOptLayout.findViewWithTag( "2A" ).setVisibility( View.VISIBLE );
                    mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            } );

            radio3A.setOnClickListener( v -> {
                try {
                    Log.d( "CONF1A", conf.getJSONArray( "3A" ).toString() );
                    mOptLayout.setVisibility( View.VISIBLE );
                    mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
                    mOptLayout.findViewWithTag( "3A" ).setVisibility( View.VISIBLE );
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            } );

        } catch ( JSONException e ) {
            e.printStackTrace();
        }
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

                        EdtSearchCategory edtCategory = dal.getByValue( spinnerKey );
                        if( edtCategory == null ) {

                            edtCategory = new EdtSearchCategory();
                            edtCategory.setPromo( confKey );
                            edtCategory.setLabel( label );
                            edtCategory.setName( spinnerKey );
                            edtCategory.setValue( spinnerKey );
                            dal.insert( edtCategory );

                            edtCategory = dal.getByValue( spinnerKey );
                        }

                        Spinner spinner = new Spinner( context );
                        spinner.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
                        spinner.setPadding( 16, 0, 16, 0 );

                        ArrayAdapter<EdtSearchOption> spinnerAdapter = new ArrayAdapter<>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
                        spinner.setAdapter( spinnerAdapter );
                        spinner.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {

                            @Override
                            public boolean onItemLongClick ( AdapterView<?> adapterView, View view, int i, long l ) {

                                Toast.makeText( context, ((EdtSearchOption) adapterView.getItemAtPosition( i )).getLabel(), Toast.LENGTH_LONG ).show();
                                return false;
                            }
                        } );

                        for ( int k = 0; k < spinnerElements.length(); k++ ) {

                            final JSONObject spinnerOption = spinnerElements.getJSONObject( k );

                            EdtSearchOption edtOption = new EdtSearchOption( edtCategory.getId(), spinnerOption.getString( "text" ), spinnerOption.getString( "value" ) );
                            dalOptions.insert( edtOption );

                            spinnerAdapter.add( edtOption );
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