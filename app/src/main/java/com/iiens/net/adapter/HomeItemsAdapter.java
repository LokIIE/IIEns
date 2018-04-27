package com.iiens.net.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iiens.net.R;
import com.iiens.net.model.HomeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsItemsAdapter
 * Classe permettant d'adapter les news pour l'affichage
 */

public class HomeItemsAdapter extends BaseAdapter {

    private final List<? extends HomeItem> itemsList;
    private final Context context;

    public HomeItemsAdapter ( Context context, ArrayList<? extends HomeItem> items ) {

        this.itemsList = items;
        this.context = context;
    }

    @Override
    public int getCount () {

        return itemsList.size();
    }

    @Override
    public HomeItem getItem ( int arg0 ) {

        return itemsList.get( arg0 );
    }

    @Override
    public long getItemId ( int arg0 ) {

        return arg0;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {

        if ( convertView == null ) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( R.layout.home_item, parent, false );
        }

        TextView itemTitle = convertView.findViewById( R.id.item_title );

        HomeItem item = itemsList.get( position );

        if ( item.getItemContent().length() > 0 ) itemTitle.setText( item.getItemContent() );
        int logoId = context.getResources().getIdentifier( item.getItemIcon(), "drawable", "com.iiens.net" );
        if ( logoId != 0 ) itemTitle.setCompoundDrawablesWithIntrinsicBounds( logoId, 0, 0, 0 );

        return convertView;
    }
}