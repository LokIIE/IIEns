package com.iiens.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/** Edt
	Fragment faisant office de formulaire pour la recherche dans l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class Edt extends Fragment {

	private TextView mEdtPickedDate;
	private RadioGroup radioPromoGroup;
	private RadioButton radio1A, radio2A, radio3A;
	private ImageButton mEdtCalendar;
	private Calendar myCalendar;
	private LinearLayout mFormulaire, mComm, mProgressSpinner, mOpt2a, mOpt3a;
	private Spinner mGroupSpinner, mCommSpinner, mOptSpinner1, mOptSpinner2, mOptSpinner3, mOptSpinner4, mOptSpinner5, mOptSpinner6;
	private SimpleDateFormat calDateFormater = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
	private SimpleDateFormat rqDateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
	private String[] groupes = {"", "GR1", "GR1.1", "GR1.2", "GR2", "GR2.1", "GR2.2", "GR3", "GR3.1", "GR3.2", "GR4", "GR4.1", "GR4.2", "GR5", "FIPA"};
	private String[] commGroupes = {"", "GR A", "GR B", "GR C", "GR D", "GR E", "GR F"};
	private String[][] options2a = { {},
			{"", "op21.1", "op21.2", "op21.2g1", "op21.3", "op21.4"},
			{"", "op22.1", "op22.2", "op22.3", "op22.4"},
			{"", "op23.1", "op23.1g1", "op23.1g2", "op23.2", "op23.3", "op23.4", "op23.4g1", "op23.4g2"},
			{"", "op24.1", "op24.2", "op24.3", "op24.4g1", "op24.4g2"},
			{"", "op25.1", "op25.2", "op25.3", "op25.4"},
			{"", "op26.1", "op26.2", "op26.2g1", "op26.2g2", "op26.3", "op26.4", "op26.4g1", "op26.4g2"}
	};
	private String[][] options3a = { {},
			{"", "op31.1", "op31.2", "op31.3", "op31.3g1", "op31.3g2", "op31.4",  "op31.4g1"},
			{"", "op32.1", "op32.2", "op32.3", "op32.3g1","op32.3g2", "op32.4"},
			{"", "op33.1", "op33.2", "op33.3", "op33.3g1", "op33.3g2", "op33.4", "op33.4g1"},
			{"", "op34.1", "op34.1g1", "op34.1g2", "op34.1g3", "op34.2", "op34.3", "op34.3g1", "op34.3g2", "op34.4", "op34.4g1", "op34.4g2"},
			{"", "op35.1", "op35.2", "op35.3", "op35.3g1", "op35.4"},
			{"", "op36.1", "op36.2", "op36.3", "op36.3g1", "op36.3g2", "op36.4"}
	};
	private String[][] optionsPromo = null;
	private Button btnSearch;
	private Bundle bundle;
	private Fragment frag;
	private FragmentManager fragmentManager;

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = this.getArguments(); 

		// retain this fragment
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			// Restauration des données du contexte utilisateur
			bundle.putAll(savedInstanceState);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		View view =  inflater.inflate(R.layout.edt_formulaire, container, false);
		myCalendar = Calendar.getInstance();
		mFormulaire = (LinearLayout) view.findViewById(R.id.edt_formulaire);
		mProgressSpinner = (LinearLayout) view.findViewById(R.id.spinner_layout);
		mEdtPickedDate = (TextView) view.findViewById(R.id.edt_picked_date);
		mEdtCalendar = (ImageButton) view.findViewById(R.id.edt_calendar);

		radioPromoGroup = (RadioGroup) view.findViewById(R.id.chk_promo);
		radio1A = (RadioButton) view.findViewById(R.id.chk_1A);
		radio2A = (RadioButton) view.findViewById(R.id.chk_2A);
		radio3A = (RadioButton) view.findViewById(R.id.chk_3A);

		mOpt2a = (LinearLayout) view.findViewById(R.id.edt_options2A);
		mOpt3a = (LinearLayout) view.findViewById(R.id.edt_options3A);
		mComm = (LinearLayout) view.findViewById(R.id.edt_comm);
		mGroupSpinner = (Spinner) view.findViewById(R.id.edt_groupe);
		mCommSpinner = (Spinner) view.findViewById(R.id.edt_comm_spin);


		btnSearch = (Button) view.findViewById(R.id.edt_search_button);

		// hide/show the options when necessary
		radio1A.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOpt2a.setVisibility(View.GONE);
				mOpt3a.setVisibility(View.GONE);
				mComm.setVisibility(View.VISIBLE);
			}

		});

		radio2A.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOpt2a.setVisibility(View.VISIBLE);
				mOpt3a.setVisibility(View.GONE);
				mComm.setVisibility(View.VISIBLE);
			}

		});

		radio3A.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOpt2a.setVisibility(View.GONE);
				mOpt3a.setVisibility(View.VISIBLE);
				mComm.setVisibility(View.GONE);
			}

		});

		// display the current date by default
		mEdtPickedDate.setText(calDateFormater.format(new Date()).toString());

		// set up the calendar to pick the date
		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				myCalendar.setFirstDayOfWeek(Calendar.MONDAY);

				String myFormat = "dd-MM-yyyy";
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

				mEdtPickedDate.setText(sdf.format(myCalendar.getTime()));
			}

		};

		// display the calendar when the icon is clicked
		mEdtCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(getActivity(), date, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		// action of the search button
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// get selected radio button from radioGroup
				int selectedId = radioPromoGroup.getCheckedRadioButtonId();
				if (!(selectedId > 0)) {
					Toast.makeText(getActivity().getApplicationContext(), "Choisis une promo, patate", Toast.LENGTH_LONG).show();
					return ;
				}

				// find the radiobutton by returned id and set filter accordingly
				String promo = null;
				String option1 = ""; String option2 = ""; String option3 = ""; 
				String option4 = ""; String option5 = ""; String option6 = "";
				LinearLayout promoOptsLayout = null;

				String commGroup = "";
				if (selectedId == radio1A.getId()) {
					promo = "1";
					commGroup = commGroupes[mCommSpinner.getSelectedItemPosition()];
				}
				else if (selectedId == radio2A.getId()) {
					promo = "2";
					promoOptsLayout = mOpt2a;
					optionsPromo = options2a;
					commGroup = commGroupes[mCommSpinner.getSelectedItemPosition()];
				} else if (selectedId == radio3A.getId()) {
					promo = "3";
					promoOptsLayout = mOpt3a;
					optionsPromo = options3a;
				}

				String selectedGroup = groupes[mGroupSpinner.getSelectedItemPosition()];

				if (promoOptsLayout != null) {
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
				}

				String [] filtre = {selectedGroup, commGroup, option1, option2, option3, option4, option5, option6};

				// format the date picked to display it in the results and to match the database date format
				String date = mEdtPickedDate.getText().toString();
				try {
					date = rqDateFormater.format(calDateFormater.parse(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				// make the request
				if (isOnline()){
					fragmentManager = getFragmentManager();
					frag = new EdtResult();

					bundle.putString("date", date);
					bundle.putString("promo", promo);
					bundle.putStringArray("filtre", filtre);
					frag.setArguments(bundle);

					mProgressSpinner.setVisibility(View.VISIBLE);
					mFormulaire.setVisibility(View.GONE);				

					fragmentManager.beginTransaction().replace(R.id.content, frag).commit();
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "T'as pas internet, banane", Toast.LENGTH_LONG).show();
				}

			}

		});

		return view;
	}

	// Verifies that the app has internet access
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putAll(bundle);
	}

}