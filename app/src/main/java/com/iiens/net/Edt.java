package com.iiens.net;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.iiens.net.database.EdtOptDb;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Edt
 * Fragment faisant office de formulaire pour la recherche dans l'edt
 */

public class Edt extends BaseFragment {

    public String TAG = getClass().getName();
    private GlobalState global;
    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mComm, mLangue, mOpt2a, mOpt3a;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner, mOptSpinner1, mOptSpinner2, mOptSpinner3, mOptSpinner4, mOptSpinner5, mOptSpinner6, mOptSpinnerTc;
    private int currentWeekNumber = 1;
    private Bundle bundle;
    private EdtOptDb dal;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dal = new EdtOptDb(context);
        global = (GlobalState) getActivity().getApplicationContext();
        bundle = global.getBundle();

        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.edt_formulaire, container, false);

        // display weeks in spinner
        mEdtWeekSpinner = (Spinner) view.findViewById(R.id.edt_week);
        mEdtWeekSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                generateWeeks()));
        mEdtWeekSpinner.setSelection(2);


        mOpt2a = (LinearLayout) view.findViewById(R.id.edt_options2A);
        mOpt3a = (LinearLayout) view.findViewById(R.id.edt_options3A);
        mComm = (LinearLayout) view.findViewById(R.id.edt_comm);
        mLangue = (LinearLayout) view.findViewById(R.id.edt_langue);

        // Find spinners
        mGroupSpinner = (Spinner) view.findViewById(R.id.edt_groupe);
        mCommSpinner = (Spinner) view.findViewById(R.id.edt_comm_spin);
        mLangSpinner = (Spinner) view.findViewById(R.id.edt_lang_spin);
        mOptSpinner1 = (Spinner) view.findViewById(R.id.edt_option1);
        mOptSpinner2 = (Spinner) view.findViewById(R.id.edt_option2);
        mOptSpinner3 = (Spinner) view.findViewById(R.id.edt_option3);
        mOptSpinner4 = (Spinner) view.findViewById(R.id.edt_option4);
        mOptSpinner5 = (Spinner) view.findViewById(R.id.edt_option5);
        mOptSpinner6 = (Spinner) view.findViewById(R.id.edt_option6);
        mOptSpinnerTc = (Spinner) view.findViewById(R.id.form_tc);

        this.initializeSpinners();

        radioPromoGroup = (RadioGroup) view.findViewById(R.id.chk_promo);
        radio1A = (RadioButton) view.findViewById(R.id.chk_1A);
        radio2A = (RadioButton) view.findViewById(R.id.chk_2A);
        radio3A = (RadioButton) view.findViewById(R.id.chk_3A);

        // hide/show the options when necessary
        radio1A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpt2a.setVisibility(View.GONE);
                mOpt3a.setVisibility(View.GONE);
                mComm.setVisibility(View.VISIBLE);
                mLangue.setVisibility(View.VISIBLE);
            }

        });

        radio2A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpt2a.setVisibility(View.VISIBLE);
                mOpt3a.setVisibility(View.GONE);
                mComm.setVisibility(View.VISIBLE);
                mLangue.setVisibility(View.VISIBLE);
                loadOptionSpinners2A();
            }

        });

        radio3A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpt2a.setVisibility(View.GONE);
                mOpt3a.setVisibility(View.VISIBLE);
                mComm.setVisibility(View.GONE);
                mLangue.setVisibility(View.GONE);
                loadOptionSpinners3A();
            }
        });

        // action of the search button
        view.findViewById(R.id.edt_search_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                // get selected radio button from radioGroup
//                int selectedId = radioPromoGroup.getCheckedRadioButtonId();
//                if (!(selectedId > 0)) {
//                    Toast.makeText(global, R.string.form_group_missing, Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                // find the radiobutton by returned id and set filter accordingly
//                String[][] optionsPromo = null;
//                String promo = "", option1 = "", option2 = "", option3 = "", option4 = "", option5 = "", option6 = "", optiontc = "";
//                LinearLayout promoOptsLayout = null;
//
//                String commGroup = "";
//                String langGroup = "";
//                if (selectedId == radio1A.getId()) {
//                    promo = "1";
//                    commGroup = getResources().getStringArray(R.array.edt_comm_grp_raw)[mCommSpinner.getSelectedItemPosition()];
//                    langGroup = getResources().getStringArray(R.array.edt_langue_raw)[mLangSpinner.getSelectedItemPosition()];
//                } else if (selectedId == radio2A.getId()) {
//                    promo = "2";
//                    promoOptsLayout = mOpt2a;
//                    optionsPromo = options2a;
//                    commGroup = getResources().getStringArray(R.array.edt_comm_grp_raw)[mCommSpinner.getSelectedItemPosition()];
//                    langGroup = getResources().getStringArray(R.array.edt_langue_raw)[mLangSpinner.getSelectedItemPosition()];
//                } else if (selectedId == radio3A.getId()) {
//                    promo = "3";
//                    promoOptsLayout = mOpt3a;
//                    optionsPromo = options3a;
//                }
//
//                String studentGroup = getResources().getStringArray(R.array.edt_search_option_groupe_raw)[mGroupSpinner.getSelectedItemPosition()];
//
//                if (promoOptsLayout != null) { // if promo != 1
//                    mOptSpinner1 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option1);
//                    option1 = optionsPromo[1][mOptSpinner1.getSelectedItemPosition()];
//                    mOptSpinner2 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option2);
//                    option2 = optionsPromo[2][mOptSpinner2.getSelectedItemPosition()];
//                    mOptSpinner3 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option3);
//                    option3 = optionsPromo[3][mOptSpinner3.getSelectedItemPosition()];
//                    mOptSpinner4 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option4);
//                    option4 = optionsPromo[4][mOptSpinner4.getSelectedItemPosition()];
//                    mOptSpinner5 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option5);
//                    option5 = optionsPromo[5][mOptSpinner5.getSelectedItemPosition()];
//                    mOptSpinner6 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option6);
//                    option6 = optionsPromo[6][mOptSpinner6.getSelectedItemPosition()];
//                    if (promo.equals("2")) {
//                        mOptSpinnerTc = (Spinner) promoOptsLayout.findViewById(R.id.form_tc);
//                        optiontc = optionsPromo[7][mOptSpinnerTc.getSelectedItemPosition()];
//                    }
//                }
//
//                String[] filtre = {studentGroup, commGroup, langGroup, option1, option2, option3, option4, option5, option6, optiontc};
//
//                String week = String.valueOf(currentWeekNumber - 2 + mEdtWeekSpinner.getSelectedItemPosition());
//
//                // make the request
//                if (global.isOnline()) {
//                    Intent i = new Intent(getActivity(), EdtResult.class);
//                    bundle.putString("week", week);
//                    bundle.putString("promo", promo);
//                    bundle.putStringArray("filtre", filtre);
//                    i.putExtra("bundle", bundle);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
//                }

            }

        });

        return view;
    }

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

    /* Action when (for ex) the screen orientation changes */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(bundle);
    }

    protected void generateView(View view) {}

    @Override
    protected void displayResult(View view, JSONArray result) {}

    @Override
    protected void refreshDisplay() {}

    private void initializeSpinners() {
        mGroupSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(1)));
        mCommSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(2)));
        mLangSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(16)));
        mOptSpinnerTc.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(15)
        ));
    }

    private void loadOptionSpinners2A() {
        mOptSpinner1.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(3)));
        mOptSpinner2.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(4)));
        mOptSpinner3.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(5)));
        mOptSpinner4.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(6)));
        mOptSpinner5.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(7)));
        mOptSpinner6.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(8)));
    }

    private void loadOptionSpinners3A() {
        mOptSpinner1.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(9)));
        mOptSpinner2.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(10)));
        mOptSpinner3.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(11)));
        mOptSpinner4.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(12)));
        mOptSpinner5.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(13)));
        mOptSpinner6.setAdapter(new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                dal.getSpinnerItems(14)));
    }
}