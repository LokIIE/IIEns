package com.iiens.net;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iiens.net.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * NewsDetail
 * Fragment permettant l'affichage du détail d'une news
 */

public class NewsDetail extends BaseFragment {

    private final String TAG = getClass().getName();
    private NewsItem newsItem;

    @Override // this method is only called once for this fragment
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        this.apiKey = getResources().getString(R.string.apiie_news);
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.news_detail, container, false );

        this.newsItem = new NewsItem();
        if ( this.getArguments() != null ) {

            this.generateView( view );
        }

        return view;
    }

    protected void generateView ( View view ) {

        TextView newsTitle = (TextView) view.findViewById( R.id.item_title );
        TextView newsDescription = (TextView) view.findViewById( R.id.item_description );
        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject( this.getArguments().getString( "item" ) );

        } catch ( JSONException e ) {

            e.printStackTrace();
        }
        this.newsItem.fromJsonObject( jsonObject );

        if ( this.newsItem.getTitre().length() > 0 ) newsTitle.setText( this.newsItem.getTitre() );
        if ( newsItem.getContenu().length() > 0 ) newsDescription.setText( Html.fromHtml( this.newsItem.getContenu() ) );
        int logoId = context.getResources().getIdentifier( this.newsItem.getAuteur(), "drawable", "com.iiens.net" );
        if ( logoId != 0 ) newsTitle.setCompoundDrawablesWithIntrinsicBounds( logoId, 0, 0, 0 );
    }

    public void displayResult ( View view, JSONArray jResult ) {}
}