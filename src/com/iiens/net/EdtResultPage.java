package com.iiens.net;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/** EdtResultPage
	Fragment affichant les résultats de la recherche sur l'edt pour un jour donné
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtResultPage extends Fragment {

	private ListView mListView;
	private Bundle bundle;
	private ArrayList<EdtItem> edtItemsList;
	
    // newInstance constructor for creating fragment with arguments
    public EdtResultPage(ArrayList<EdtItem> dayItems) {
        edtItemsList = dayItems;
    }
    
    public EdtResultPage() {}

	@Override // this method is only called once for this fragment
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			bundle.putAll(savedInstanceState);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.edt_result_page, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);

		mListView.setAdapter(new EdtItemsAdapter(getActivity().getApplicationContext(), edtItemsList));

		return view;
	}
}