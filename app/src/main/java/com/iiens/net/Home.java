package com.iiens.net;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.iiens.net.adapter.HomeItemsAdapter;
import com.iiens.net.database.AnnivDao;
import com.iiens.net.database.AppDb;
import com.iiens.net.database.NewsDao;
import com.iiens.net.model.HomeItem;
import com.iiens.net.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * Liste des nouvelles
 */
public class Home extends BaseFragment {

    private final String TAG = getClass().getName();
    private NewsDao dalNews;
    private AnnivDao dalAnniv;
    private ListView mListView;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        this.apiKey = getResources().getString( R.string.api_news );
        AppDb mDb = AppDb.getAppDb( context );
        this.dalNews = mDb.newsDao();
        this.dalAnniv = mDb.annivDao();

        this.layoutId = R.layout.home_listview;
    }

    protected void generateView ( View view ) {

        this.mListView = view.findViewById( R.id.home_listview );
        this.mListView.setOnItemClickListener( ( arg0, arg1, position, arg3 ) -> {

            if( this.mListView.getItemAtPosition( position ) instanceof NewsItem ) {

                FragmentManager fm = getActivity().getFragmentManager();

                NewsDetail newsDetail = new NewsDetail();
                NewsItem selectedItem = (NewsItem) this.mListView.getItemAtPosition( position );

                // Envoi de l'item sélectionné au fragment
                Bundle bundle = new Bundle();
                bundle.putString( "item", selectedItem.toJsonObject().toString() );
                newsDetail.setArguments( bundle );

                fm.beginTransaction()
                        .replace( R.id.content_container, newsDetail )
                        .addToBackStack( null )
                        .commit();
            }
        } );

        mListView.setAdapter( new HomeItemsAdapter( context, new ArrayList<>() ) );

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
        NewsItem firstItem = dalNews.getFirst();

        // Get the JSON data for this fragment
        try {

            if ( preferences.getBoolean( getResources().getString( R.string.bool_news_update_name ), false ) && global.isOnline() ) {

                // If there is an update available and we are connected to the internet
                Log.e( TAG, "from web with save" );
                this.apiRequest( view );
                preferences.edit().putBoolean( getResources().getString( R.string.bool_news_update_name ), false ).apply();

            } else if ( firstItem != null ) {

                Log.e( TAG, "from database" );

                this.updateListView();

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

        dalNews.deleteAll();

        for ( NewsItem item : getNewsItemsArray( jResult ) ) {

            dalNews.insert( item );
        }

        this.updateListView();
    }

    private void updateListView () {

        SimpleDateFormat isoFormat = new SimpleDateFormat( "yyyy-MM-dd", Locale.FRENCH );

        Calendar c = Calendar.getInstance( Locale.ENGLISH );
        c.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
        String sunday = isoFormat.format( c.getTime() );

        c.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
        String saturday = isoFormat.format( c.getTime() );

        ArrayList<HomeItem> items = new ArrayList<>( dalNews.getAll() );
        items.addAll( new ArrayList<>(
                dalAnniv.getAllBetweenDates( sunday, saturday ) )
        );

        Collections.sort( items );

        HomeItemsAdapter adapter = (HomeItemsAdapter) this.mListView.getAdapter();
        adapter.clear();
        adapter.addAll( items );
        adapter.notifyDataSetChanged();
    }

    private ArrayList<NewsItem> getNewsItemsArray ( JSONArray jArray ) {

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