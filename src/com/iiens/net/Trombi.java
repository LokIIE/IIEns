package com.iiens.net;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class Trombi extends Fragment {

	public CheckBox checkBox_f, checkBox_m;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d("*** DEBUG", ": Trombi***");
		View view =  inflater.inflate(R.layout.fragment_trombi, container, false);

		super.onCreate(savedInstanceState);

		Spinner spinner = (Spinner)view.findViewById(R.id.promo);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.promo, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// Création spinners nom, prenom, pseudo
		adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.option, android.R.layout.simple_spinner_item);
		spinner = (Spinner)view.findViewById(R.id.option_nom);
		spinner.setAdapter(adapter);
		spinner = (Spinner)view.findViewById(R.id.option_prenom);
		spinner.setAdapter(adapter);
		spinner = (Spinner)view.findViewById(R.id.option_pseudo);
		spinner.setAdapter(adapter);
		// Création spinners téléphone
		spinner = (Spinner)view.findViewById(R.id.option_tel);
		adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.option_tel, android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);


		//    	checkBox_f = (CheckBox) view.findViewById(R.id.sexe_f);
		//    	checkBox_m = (CheckBox) view.findViewById(R.id.sexe_m);
		//    	checkBox_evry = (CheckBox) view.findViewById(R.id.antenne_evry);
		//    	checkBox_stras = (CheckBox) view.findViewById(R.id.antenne_stras);

		return view;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}