package com.iiens.net;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AnnivItemsAdapter extends BaseAdapter {

	private List<AnnivItem> annivItemsList = new ArrayList<AnnivItem>();
	private Context context;

	public AnnivItemsAdapter(Context context, ArrayList<AnnivItem> getAnniv) {
		this.annivItemsList = getAnniv;
		this.context = context;
	}

	@Override
	public int getCount() {
		return annivItemsList.size();
	}

	@Override
	public AnnivItem getItem(int arg0) {
		return annivItemsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if(arg1==null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.anniv_item, arg2,false);
		}

		if (annivItemsList.size() > 0) {
		TextView mAnnivDate = (TextView) arg1.findViewById(R.id.anniv_date);
		TextView mAnnivPerson = (TextView) arg1.findViewById(R.id.anniv_person);
		AnnivItem annivItem = annivItemsList.get(arg0);
		String pseudo = " ";
		if (annivItem.getPseudo().length() > 0) {
			pseudo = " '" + annivItem.getPseudo() + "' ";
		}

		String str_anniv = annivItem.getPrenom() + pseudo + annivItem.getNom() + " (" + annivItem.getAge().trim() + " ans)";  

		mAnnivDate.setText(annivItem.getAnniv() + " : ");
		mAnnivPerson.setText(str_anniv);
		}
		
		return arg1;
	}

}