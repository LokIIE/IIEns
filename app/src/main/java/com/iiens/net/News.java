package com.iiens.net;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.iiens.net.adapter.NewsItemsAdapter;
import com.iiens.net.database.NewsDb;
import com.iiens.net.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Liste des nouvelles
 */
public class News extends BaseFragment {

    private final String TAG = getClass().getName();
    private NewsDb dal;
    private ListView mListView;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        this.apiKey = getResources().getString(R.string.apiie_news);
        this.dal = new NewsDb( context );

        this.layoutId = R.layout.listview;
    }

    protected void generateView ( View view ) {

        this.mListView = (ListView) view.findViewById( R.id.listview );

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
        NewsItem firstItem = dal.getFirstItem();

        // Get the JSON data for this fragment
        try {

            if ( preferences.getBoolean( getResources().getString( R.string.bool_news_update_name ), false ) && global.isOnline() ) {

                // If there is an update available and we are connected to the internet
                Log.e( TAG, "from web with save" );
                dal.deleteAll();
                this.apiRequest( view );
                preferences.edit().putBoolean( getResources().getString( R.string.bool_news_update_name ), false ).apply();

            } else if ( firstItem != null ) {

                Log.e( TAG, "from database" );
                this.setListViewContent( dal.getAllItems() );

            } else if ( global.isOnline() ) {

                // If the file doesn't exist yet (first launch for example), fetch the data
                Log.e( TAG, "from web" );
                this.apiRequest( view );

            } else {

                // If no connection or data stored, can't do anything
                Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void displayResult ( View view, JSONArray jResult ) {

        final ArrayList<NewsItem> itemList = this.jArrayToArrayList( jResult );

        // If the request was successful, save the items to save data consumption and populate listview
        if (jResult != null && jResult.length() > 0) {

            this.setListViewContent( itemList );
        }

        // In case the refresh button was triggered, stop the "animation"
        if ( getView() != null ) {

            getView().setAlpha((float) 1);
        }

        dal.deleteAll();
        for ( NewsItem item : itemList ) {

            //if (dal.findItemId(item) > 0) {
            dal.createItem( item );
            //}
        }
    }

    private void setListViewContent ( final ArrayList<NewsItem> itemList ) {

        mListView.setAdapter( new NewsItemsAdapter( context, itemList ) );
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                FragmentManager fm = getActivity().getFragmentManager();

                // Création fragment détail
                NewsDetail newsDetail = new NewsDetail();

                // Envoi de l'item sélectionné au fragment
                Bundle bundle = new Bundle();
                bundle.putString( "item", itemList.get( position ).toJsonObject().toString() );
                newsDetail.setArguments( bundle );

                FragmentTransaction ft = fm.beginTransaction();
                // Remplacement de la vue par le nouveau fragment
                ft.replace( R.id.content_container, newsDetail );
                // Ajout du nouveau fragment au backstack pour navigation arrière
                ft.addToBackStack( null );

                ft.commit();
            }
        });
    }

    private ArrayList<NewsItem> jArrayToArrayList ( JSONArray jArray ) {

        ArrayList<NewsItem> newsItemsList = new ArrayList<>();

        try {

            for (int i = 0; i < jArray.length(); i++) {

                JSONObject json_data = jArray.getJSONObject( i );
                NewsItem newsItem = new NewsItem();
                newsItem.fromJsonObject( json_data );
                newsItemsList.add( newsItem );
            }

        } catch (JSONException e) {

            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return newsItemsList;
    }
}