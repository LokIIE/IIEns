package com.iiens.net.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iiens.net.R;
import com.iiens.net.model.AnnivItem;
import com.iiens.net.model.HomeItem;

import java.util.List;

/**
 * NewsItemsAdapter
 * Classe permettant d'adapter les news pour l'affichage
 */

public class HomeItemsAdapter extends ArrayAdapter<HomeItem> {

    private final Context context;

    public HomeItemsAdapter ( @NonNull Context context, @NonNull List<HomeItem> objects ) {

        super( context, R.layout.home_item, objects );
        this.context = context;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent ) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate( R.layout.home_item, parent, false );

        TextView itemTitle = convertView.findViewById( R.id.item_title );

        if( this.getItem( position ) instanceof AnnivItem ) {

            itemTitle.setTextSize( 16 );

        } else {

            itemTitle.setTextSize( 20 );
        }

        HomeItem item = this.getItem( position );

        if ( item.getItemContent().length() > 0 ) {

            itemTitle.setText( Html.fromHtml( item.getItemContent() ) );
        }

        int logoId = context.getResources().getIdentifier( item.getItemIcon(), "drawable", "com.iiens.net" );
        if ( logoId != 0 ) {

            itemTitle.setCompoundDrawablesWithIntrinsicBounds( logoId, 0, 0, 0 );
        }

        return convertView;
    }
}