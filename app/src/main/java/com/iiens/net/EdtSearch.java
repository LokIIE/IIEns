package com.iiens.net;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EdtSearch extends BaseFragment {

    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mOptLayout;
    private Spinner mEdtWeekSpinner;
    private int currentWeekNumber = 1;
    private Bundle bundle;

    private EdtSearchCategoryDao dalCategory;
    private EdtSearchOptionDao dalOptions;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        bundle = global.getBundle();

        this.apiKey = "edtFormScrapper";
        this.layoutId = R.layout.edt_search_form;

        AppDb db = AppDb.getAppDb( context );
        this.dalCategory = db.edtSearchCategoryDao();
        this.dalOptions = db.edtSearchOptionDao();
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
        Spinner mGroupSpinner = view.findViewById( R.id.edt_groupe );
        Spinner mCommSpinner = view.findViewById( R.id.edt_comm );
        Spinner mLangSpinner = view.findViewById( R.id.edt_lang );

        // Initialialisation des spinners
        mGroupSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mGroupSpinner.setTag( "gp[]" );

        mCommSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mCommSpinner.setTag( "gpcomm[]" );

        mLangSpinner.setAdapter( new ArrayAdapter<EdtSearchOption>( getActivity(), android.R.layout.simple_spinner_dropdown_item ) );
        mLangSpinner.setTag( "langue[]" );

        // Action du bouton de recherche
        view.findViewById( R.id.edt_search_button ).setOnClickListener( v -> {

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
        } );

//        try {
//
//            if( dalCategory.getFirst() != null ) {
//
//                this.generateView( view );
//
//            } else {
//
//                // If no connection or data stored, can't do anything
//                Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
//            }
//
//        } catch ( Exception e ) {
//
//            e.printStackTrace();
//        }
    }

    /**
     * Création du dynamique du formulaire de l'edt suivant la version du formulaire du site web
     * @param view Vue à remplir
     * @param result Données à afficher
     */
    @Override
    protected void displayResult ( View view, JSONArray result ) {

        for ( EdtSearchCategory category : dalCategory.getAll() ) {

            Spinner currentSpinner = view.findViewWithTag( category.getName() );
            ArrayAdapter<EdtSearchOption> currentAdapter = (ArrayAdapter<EdtSearchOption>) currentSpinner.getAdapter();

            // Actualisation des tags des spinners
            currentSpinner.setTag( category.getName() );

            // Ajout des éléments du select au spinnerAdapter respectif
            for ( EdtSearchOption option : dalOptions.getAll() ) {

                currentAdapter.add( option );
            }

            currentAdapter.notifyDataSetChanged();
        }

        // Chargement des spinners des options
        mOptLayout.addView( loadOptionSpinners( "2A" ) );
        mOptLayout.addView( loadOptionSpinners( "3A" ) );

        // Affichage ou non des layouts ou spinners suivant le radio bouton choisi et la conf actuelle
        radio1A.setOnClickListener( v -> {

            mOptLayout.setVisibility( View.INVISIBLE );
            mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
            mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );
        });

        radio2A.setOnClickListener( v -> {

            mOptLayout.setVisibility( View.VISIBLE );
            mOptLayout.findViewWithTag( "2A" ).setVisibility( View.VISIBLE );
            mOptLayout.findViewWithTag( "3A" ).setVisibility( View.GONE );
        });

        radio3A.setOnClickListener( v -> {

            mOptLayout.setVisibility( View.VISIBLE );
            mOptLayout.findViewWithTag( "2A" ).setVisibility( View.GONE );
            mOptLayout.findViewWithTag( "3A" ).setVisibility( View.VISIBLE );
        });
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

        for ( EdtSearchCategory edtCategory : dalCategory.getAll() ) {

            Spinner spinner = new Spinner( context );
            spinner.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
            spinner.setPadding( 16, 0, 16, 0 );

            ArrayAdapter<EdtSearchOption> spinnerAdapter = new ArrayAdapter<>( getActivity(), android.R.layout.simple_spinner_dropdown_item );
            spinner.setAdapter( spinnerAdapter );
            spinner.setOnItemLongClickListener( ( adapterView, view, i1, l ) -> {

                Toast.makeText( context, ((EdtSearchOption) adapterView.getItemAtPosition( i1 )).getLabel(), Toast.LENGTH_LONG ).show();
                return false;
            } );

            for ( EdtSearchOption option : dalOptions.getAll() ) {

                spinnerAdapter.add( option );
            }

            LinearLayout spinnerView = new LinearLayout( context );
            spinnerView.setOrientation( LinearLayout.HORIZONTAL );
            spinnerView.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );

            TextView spinnerLabel = new TextView( context );
            spinnerLabel.setText( edtCategory.getName() );

            spinnerView.addView( spinnerLabel );
            spinnerView.addView( spinner );

            optionLayout.addView( spinnerView );

            spinnerAdapter.notifyDataSetChanged();
        }

        return optionLayout;
    }
}