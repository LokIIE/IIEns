package com.iiens.net;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.Toast;

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

public class Edt extends Fragment implements DisplayFragment {

    public String TAG = getClass().getName();
    private GlobalState global;
    private RadioGroup radioPromoGroup;
    private RadioButton radio1A, radio2A, radio3A;
    private LinearLayout mComm, mLangue, mOpt2a, mOpt3a;
    private Spinner mEdtWeekSpinner, mGroupSpinner, mCommSpinner, mLangSpinner, mOptSpinner1, mOptSpinner2, mOptSpinner3, mOptSpinner4, mOptSpinner5, mOptSpinner6, mOptSpinnerTc;
    private int currentWeekNumber = 1;
    private String[][] options2a;
    private String[][] options3a;
    private Bundle bundle;

    @Override // this method is only called once for this fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (GlobalState) getActivity().getApplicationContext();
        bundle = global.getBundle();
        options2a = new String[][]{{},
                getResources().getStringArray(R.array.edt_options2A_1_raw),
                getResources().getStringArray(R.array.edt_options2A_2_raw),
                getResources().getStringArray(R.array.edt_options2A_3_raw),
                getResources().getStringArray(R.array.edt_options2A_4_raw),
                getResources().getStringArray(R.array.edt_options2A_5_raw),
                getResources().getStringArray(R.array.edt_options2A_6_raw),
                getResources().getStringArray(R.array.edt_options2A_tc_raw),
        };
        options3a = new String[][]{{},
                getResources().getStringArray(R.array.edt_options3A_1_raw),
                getResources().getStringArray(R.array.edt_options3A_2_raw),
                getResources().getStringArray(R.array.edt_options3A_3_raw),
                getResources().getStringArray(R.array.edt_options3A_4_raw),
                getResources().getStringArray(R.array.edt_options3A_5_raw),
                getResources().getStringArray(R.array.edt_options3A_6_raw),
        };

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
        mEdtWeekSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, generateWeeks()));
        mEdtWeekSpinner.setSelection(2);

        mOpt2a = (LinearLayout) view.findViewById(R.id.edt_options2A);
        mOpt3a = (LinearLayout) view.findViewById(R.id.edt_options3A);
        mComm = (LinearLayout) view.findViewById(R.id.edt_comm);
        mLangue = (LinearLayout) view.findViewById(R.id.edt_langue);
        mGroupSpinner = (Spinner) view.findViewById(R.id.edt_groupe);
        mCommSpinner = (Spinner) view.findViewById(R.id.edt_comm_spin);
        mLangSpinner = (Spinner) view.findViewById(R.id.edt_lang_spin);

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
            }

        });

        radio3A.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpt2a.setVisibility(View.GONE);
                mOpt3a.setVisibility(View.VISIBLE);
                mComm.setVisibility(View.GONE);
                mLangue.setVisibility(View.GONE);
            }
        });

        // action of the search button
        view.findViewById(R.id.edt_search_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radioPromoGroup.getCheckedRadioButtonId();
                if (!(selectedId > 0)) {
                    Toast.makeText(global, R.string.edt_group_missing, Toast.LENGTH_LONG).show();
                    return;
                }

                // find the radiobutton by returned id and set filter accordingly
                String[][] optionsPromo = null;
                String promo = "", option1 = "", option2 = "", option3 = "", option4 = "", option5 = "", option6 = "", optiontc = "";
                LinearLayout promoOptsLayout = null;

                String commGroup = "";
                String langGroup = "";
                if (selectedId == radio1A.getId()) {
                    promo = "1";
                    commGroup = getResources().getStringArray(R.array.edt_comm_grp_raw)[mCommSpinner.getSelectedItemPosition()];
                    langGroup = getResources().getStringArray(R.array.edt_langue_raw)[mLangSpinner.getSelectedItemPosition()];
                } else if (selectedId == radio2A.getId()) {
                    promo = "2";
                    promoOptsLayout = mOpt2a;
                    optionsPromo = options2a;
                    commGroup = getResources().getStringArray(R.array.edt_comm_grp_raw)[mCommSpinner.getSelectedItemPosition()];
                    langGroup = getResources().getStringArray(R.array.edt_langue_raw)[mLangSpinner.getSelectedItemPosition()];
                } else if (selectedId == radio3A.getId()) {
                    promo = "3";
                    promoOptsLayout = mOpt3a;
                    optionsPromo = options3a;
                }

                String studentGroup = getResources().getStringArray(R.array.edt_search_option_groupe_raw)[mGroupSpinner.getSelectedItemPosition()];

                if (promoOptsLayout != null) { // if promo != 1
                    mOptSpinner1 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option1);
                    option1 = optionsPromo[1][mOptSpinner1.getSelectedItemPosition()];
                    mOptSpinner2 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option2);
                    option2 = optionsPromo[2][mOptSpinner2.getSelectedItemPosition()];
                    mOptSpinner3 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option3);
                    option3 = optionsPromo[3][mOptSpinner3.getSelectedItemPosition()];
                    mOptSpinner4 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option4);
                    option4 = optionsPromo[4][mOptSpinner4.getSelectedItemPosition()];
                    mOptSpinner5 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option5);
                    option5 = optionsPromo[5][mOptSpinner5.getSelectedItemPosition()];
                    mOptSpinner6 = (Spinner) promoOptsLayout.findViewById(R.id.edt_option6);
                    option6 = optionsPromo[6][mOptSpinner6.getSelectedItemPosition()];
                    if (promo.equals("2")) {
                        mOptSpinnerTc = (Spinner) promoOptsLayout.findViewById(R.id.edt_optiontc);
                        optiontc = optionsPromo[7][mOptSpinnerTc.getSelectedItemPosition()];
                    }
                }

                String[] filtre = {studentGroup, commGroup, langGroup, option1, option2, option3, option4, option5, option6, optiontc};

                String week = String.valueOf(currentWeekNumber - 2 + mEdtWeekSpinner.getSelectedItemPosition());

                // make the request
                if (global.isOnline()
                        || (week.equals(String.valueOf(currentWeekNumber)) && global.fileExists(getResources().getString(R.string.apiie_edt) + promo))) {
                    Intent i = new Intent(getActivity(), EdtResult.class);
                    bundle.putString("week", week);
                    bundle.putString("promo", promo);
                    bundle.putStringArray("filtre", filtre);
                    i.putExtra("bundle", bundle);
                    startActivity(i);
                } else {
                    Toast.makeText(global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show();
                }

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

    public void displayResult(View view, JSONArray result) {
    }

    public void refreshDisplay() {
    }
}