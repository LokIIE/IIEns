package com.iiens.net;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.iiens.net.database.EdtOptDb;
import com.iiens.net.model.EdtFormItem;
import com.iiens.net.model.EdtOptItem;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Formulaire de recherche de l'emploi du temps
 */
public class Edt extends BaseFragment {

    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mComm, mLangue, mOptLayout, mOptTcLayout;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner,
            mOptSpinner1, mOptSpinner2, mOptSpinner3, mOptSpinner4, mOptSpinner5,
            mOptSpinner6, mOptSpinnerTc;
    private ArrayAdapter<EdtOptItem> mGroupAdapter, mCommAdapter, mLangAdapter,
            mOptAdapter1, mOptAdapter2, mOptAdapter3, mOptAdapter4, mOptAdapter5,
            mOptAdapter6, mOptAdapterTc;
    private int currentWeekNumber = 1;
    private Bundle bundle;
    private EdtOptDb dal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dal = new EdtOptDb(context);
        bundle = global.getBundle();

        this.layoutId = R.layout.edt_formulaire;
    }

    protected void generateView(View view) {
        // Affichage des semaines dans le spinner
        mEdtWeekSpinner = (Spinner) view.findViewById(R.id.edt_week);
        mEdtWeekSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                generateWeeks()));
        mEdtWeekSpinner.setSelection(2);

        // Références des layouts
        mOptLayout = (LinearLayout) view.findViewById(R.id.edt_options);
        mOptTcLayout = (LinearLayout) view.findViewById(R.id.edt_optionTc_layout);
        mComm = (LinearLayout) view.findViewById(R.id.edt_comm_layout);
        mLangue = (LinearLayout) view.findViewById(R.id.edt_langue_layout);

        // Références des spinners
        mGroupSpinner = (Spinner) view.findViewById(R.id.edt_groupe);
        mCommSpinner = (Spinner) view.findViewById(R.id.edt_comm);
        mLangSpinner = (Spinner) view.findViewById(R.id.edt_lang);
        mOptSpinner1 = (Spinner) view.findViewById(R.id.edt_option1);
        mOptSpinner2 = (Spinner) view.findViewById(R.id.edt_option2);
        mOptSpinner3 = (Spinner) view.findViewById(R.id.edt_option3);
        mOptSpinner4 = (Spinner) view.findViewById(R.id.edt_option4);
        mOptSpinner5 = (Spinner) view.findViewById(R.id.edt_option5);
        mOptSpinner6 = (Spinner) view.findViewById(R.id.edt_option6);
        mOptSpinnerTc = (Spinner) view.findViewById(R.id.edt_optionTc);

        // Initialialisation des spinners
        this.initializeAdapters();
        this.initializeSpinners();

        // Références des radio boutons
        radioPromoGroup = (RadioGroup) view.findViewById(R.id.chk_promo);
        radio1A = (RadioButton) view.findViewById(R.id.chk_1A);
        radio2A = (RadioButton) view.findViewById(R.id.chk_2A);
        radio3A = (RadioButton) view.findViewById(R.id.chk_3A);

        // Affichage ou non des layouts ou spinners suivant le radio bouton choisi
        radio1A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptLayout.setVisibility(View.GONE);
                mComm.setVisibility(View.VISIBLE);
                mLangue.setVisibility(View.VISIBLE);
            }

        });
        radio2A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptLayout.setVisibility(View.VISIBLE);
                mComm.setVisibility(View.VISIBLE);
                mLangue.setVisibility(View.VISIBLE);
                mOptTcLayout.setVisibility(View.VISIBLE);
                loadOptionSpinners2A();
            }

        });
        radio3A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mComm.setVisibility(View.GONE);
                mLangue.setVisibility(View.GONE);
                mOptLayout.setVisibility(View.VISIBLE);
                mOptTcLayout.setVisibility(View.GONE);
                loadOptionSpinners3A();
            }
        });

        // Action du bouton de recherche
        view.findViewById(R.id.edt_search_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération de tous les paramètres de recherche saisis
                EdtForm edtForm = new EdtForm();

                // Promotion, obligatoire sinon annulation
                int selectedRadioId = radioPromoGroup.getCheckedRadioButtonId();
                if (selectedRadioId > 0) {
                    if (selectedRadioId == radio1A.getId()) {
                        edtForm.promoNumber = 1;
                    } else if (selectedRadioId == radio2A.getId()) {
                        edtForm.promoNumber = 2;
                    } else if (selectedRadioId == radio3A.getId()) {
                        edtForm.promoNumber = 3;
                    }
                } else {
                    Toast.makeText(global, R.string.edtForm_choseGroup, Toast.LENGTH_LONG).show();
                    return;
                }

                // Semaine
                edtForm.weekNumber = currentWeekNumber - 2 + mEdtWeekSpinner.getSelectedItemPosition();

                // Groupe
                edtForm.groupCode = ((EdtOptItem)mGroupSpinner.getSelectedItem()).getCode();

                // Langue
                edtForm.langueCode = ((EdtOptItem)mLangSpinner.getSelectedItem()).getCode();

                // Communication
                edtForm.commCode = ((EdtOptItem)mCommSpinner.getSelectedItem()).getCode();

                // Options
                if (edtForm.promoNumber > 1) {
                    edtForm.optionCodeList.add(0, ((EdtOptItem) mOptSpinner1.getSelectedItem()).getCode());
                    edtForm.optionCodeList.add(1, ((EdtOptItem) mOptSpinner2.getSelectedItem()).getCode());
                    edtForm.optionCodeList.add(2, ((EdtOptItem) mOptSpinner3.getSelectedItem()).getCode());
                    edtForm.optionCodeList.add(3, ((EdtOptItem) mOptSpinner4.getSelectedItem()).getCode());
                    edtForm.optionCodeList.add(4, ((EdtOptItem) mOptSpinner5.getSelectedItem()).getCode());
                    edtForm.optionCodeList.add(5, ((EdtOptItem) mOptSpinner6.getSelectedItem()).getCode());
                }

                // Tronc commun
                if (edtForm.promoNumber == 2) {
                    while (edtForm.optionCodeList.size() < 6) {
                        edtForm.optionCodeList.add("");
                    }
                    edtForm.optionCodeList.add(6, ((EdtOptItem) mOptSpinnerTc.getSelectedItem()).getCode());
                }

                // Transition d'activité pour effectuer la recherche et afficher les résultats
                if (global.isOnline()) {
                    bundle.putStringArrayList("edtParams", edtForm.toArrayList());
                    executeSearch();
                } else {
                    Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    /**
     * Génération de la liste des libellés des semaines du spinner du formulaire
     * @return Liste des libellés des semaines
     */
    private List<String> generateWeeks() {
        List<String> spinnerItems = new ArrayList<>();
        Calendar myCalendar = Calendar.getInstance(Locale.FRENCH);
        myCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        currentWeekNumber = myCalendar.get(Calendar.WEEK_OF_YEAR);
        myCalendar.add(Calendar.WEEK_OF_YEAR, -2);
        SimpleDateFormat monthName = new SimpleDateFormat("MMM", Locale.FRENCH);
        for (int i = 0; i < 11; i++) {
            myCalendar.setFirstDayOfWeek(Calendar.MONDAY);

            myCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String lundi = String.valueOf(myCalendar.get(Calendar.DAY_OF_MONTH)) + " " + monthName.format(myCalendar.getTime());

            myCalendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            String vendredi = String.valueOf(myCalendar.get(Calendar.DAY_OF_MONTH)) + " " + monthName.format(myCalendar.getTime());

            spinnerItems.add("Du " + lundi + " au " + vendredi);
            myCalendar.add(Calendar.DAY_OF_MONTH, 7);
        }
        return spinnerItems;
    }

    /**
     * Exécution de la recherche et affichage des résultats
     */
    private void executeSearch() {
        FragmentManager fm = this.getActivity().getFragmentManager();

        // Création fragment détail
        EdtResult resultFrag = new EdtResult();

        // Envoi de l'item sélectionné au fragment
        resultFrag.setArguments(bundle);

        FragmentTransaction ft = fm.beginTransaction();
        // Remplacement de la vue par le nouveau fragment
        ft.replace(R.id.content_container, resultFrag);
        // Ajout du nouveau fragment au backstack pour navigation arrière
        ft.addToBackStack(null);

        ft.commit();
    }

    /**
     * Action effectuée lors du changement d'orientation
     * @param outState Bundle de stockage des données
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(bundle);
    }

    /**
     * Paramètres de la recherche
     */
    private class EdtForm {
        /**
         * Année recherchée
         */
        protected int promoNumber;

        /**
         * Numéro de semaine recherchée
         */
        protected int weekNumber;

        /**
         * Filtre des groupes
         */
        protected String groupCode;

        /**
         * Filtre des langues vivantes
         */
        protected String langueCode;

        /**
         * Filtre des groupes de communication
         */
        protected String commCode;

        /**
         * Filtre des options 2A et 3A
         */
        protected ArrayList<String> optionCodeList = new ArrayList<>(7);

        /**
         * Crée un array contenant les paramètres de la recherche
         * @return Array contenant les paramètres de la recherche
         */
        public ArrayList<String> toArrayList() {
            ArrayList<String> res = new ArrayList<>(10);
            res.add(0, String.valueOf(weekNumber));
            res.add(1, String.valueOf(promoNumber));
            res.add(2, groupCode);
            res.add(3, langueCode);
            res.add(4, commCode);
            res.addAll(optionCodeList);
            return res;
        }
    }

    /**
     * Initialisation des adapters des spinners
     */
    private void initializeAdapters() {
        // Initialisation des spinners avec des données constantes
        mGroupAdapter = this.getArrayAdapter(EdtFormItem.EnumFormId.Group.getValue());
        mCommAdapter = this.getArrayAdapter(EdtFormItem.EnumFormId.Comm.getValue());
        mOptAdapterTc = this.getArrayAdapter(EdtFormItem.EnumFormId.OptionTc.getValue());
        mLangAdapter = this.getArrayAdapter(EdtFormItem.EnumFormId.Langue.getValue());

        // Initialisation des spinners avec des données variables
        mOptAdapter1 = this.getArrayAdapter(-1);
        mOptAdapter2 = this.getArrayAdapter(-1);
        mOptAdapter3 = this.getArrayAdapter(-1);
        mOptAdapter4 = this.getArrayAdapter(-1);
        mOptAdapter5 = this.getArrayAdapter(-1);
        mOptAdapter6 = this.getArrayAdapter(-1);
    }

    /**
     * Affectation des adapters aux spinners
     */
    private void initializeSpinners() {
        mGroupSpinner.setAdapter(mGroupAdapter);
        mCommSpinner.setAdapter(mCommAdapter);
        mOptSpinner1.setAdapter(mOptAdapter1);
        mOptSpinner2.setAdapter(mOptAdapter2);
        mOptSpinner3.setAdapter(mOptAdapter3);
        mOptSpinner4.setAdapter(mOptAdapter4);
        mOptSpinner5.setAdapter(mOptAdapter5);
        mOptSpinner6.setAdapter(mOptAdapter6);
        mOptSpinnerTc.setAdapter(mOptAdapterTc);
        mLangSpinner.setAdapter(mLangAdapter);
    }

    /**
     * Chargement des spinners avec les données des options des 2A
     */
    private void loadOptionSpinners2A() {
        mOptAdapter1.clear();
        mOptAdapter1.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option21.getValue()));
        mOptAdapter1.notifyDataSetChanged();

        mOptAdapter2.clear();
        mOptAdapter2.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option22.getValue()));
        mOptAdapter2.notifyDataSetChanged();

        mOptAdapter3.clear();
        mOptAdapter3.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option23.getValue()));
        mOptAdapter3.notifyDataSetChanged();

        mOptAdapter4.clear();
        mOptAdapter4.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option24.getValue()));
        mOptAdapter4.notifyDataSetChanged();

        mOptAdapter5.clear();
        mOptAdapter5.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option25.getValue()));
        mOptAdapter5.notifyDataSetChanged();

        mOptAdapter6.clear();
        mOptAdapter6.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option26.getValue()));
        mOptAdapter6.notifyDataSetChanged();
    }

    /**
     * Chargement des spinners avec les données des options des 3A
     */
    private void loadOptionSpinners3A() {
        mOptAdapter1.clear();
        mOptAdapter1.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option31.getValue()));
        mOptAdapter1.notifyDataSetChanged();

        mOptAdapter2.clear();
        mOptAdapter2.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option32.getValue()));
        mOptAdapter2.notifyDataSetChanged();

        mOptAdapter3.clear();
        mOptAdapter3.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option33.getValue()));
        mOptAdapter3.notifyDataSetChanged();

        mOptAdapter4.clear();
        mOptAdapter4.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option34.getValue()));
        mOptAdapter4.notifyDataSetChanged();

        mOptAdapter5.clear();
        mOptAdapter5.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option35.getValue()));
        mOptAdapter5.notifyDataSetChanged();

        mOptAdapter6.clear();
        mOptAdapter6.addAll(dal.getSpinnerItems(EdtFormItem.EnumFormId.Option36.getValue()));
        mOptAdapter6.notifyDataSetChanged();
    }

    /**
     * Crée un ArrayAdapter pour les spinners du formulaire de recherche
     * @param edtFormId Identifiant de la catégorie de données
     * @return ArrayAdapter alimenté avec les options
     */
    public ArrayAdapter<EdtOptItem> getArrayAdapter(int edtFormId) {
        if (edtFormId < 0) {
            return new ArrayAdapter<>(
                    getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    new ArrayList<EdtOptItem>());
        } else {
            return new ArrayAdapter<>(
                    getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    dal.getSpinnerItems(edtFormId));
        }
    }

    @Override
    protected void displayResult(View view, JSONArray result) {}
}