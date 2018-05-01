package com.iiens.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.iiens.net.adapter.AnnivItemsAdapter;
import com.iiens.net.database.AnnivDao;
import com.iiens.net.database.AppDb;
import com.iiens.net.model.AnnivItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Liste des anniversaires publics
 */

public class Anniv extends BaseFragment {

    private final String TAG = getClass().getName();
    private AnnivDao dal;
    private ListView mListView;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        this.apiKey = getResources().getString(R.string.api_anniv );
        this.dal = AppDb.getAppDb( context ).annivDao();

        this.layoutId = R.layout.home_listview;
    }

    private ArrayList<AnnivItem> getItemList ( JSONArray jArray ) {

        ArrayList<AnnivItem> annivItemsList = new ArrayList<>();

        try {

            for ( int i = 0; i < jArray.length(); i++ ) {

                JSONObject data = jArray.getJSONObject( i );
                annivItemsList.add( AnnivItem.load( data ) );
            }

        } catch ( JSONException e ) {

            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return annivItemsList;
    }

    protected void generateView ( final View view ) {

        this.mListView = view.findViewById( R.id.home_listview );

        AnnivItem firstItem = dal.getFirst();

        // Get the JSON data for this fragment
        try {

            DateFormat dateFormat = new SimpleDateFormat( "EEEE dd MMMM", Locale.FRENCH );
            Date firstDate = ( firstItem != null ) ?
                    dateFormat.parse( firstItem.getAnniv() ) : null;
            Date today = new Date();

            // Aucun anniversaire n'est passé
            if ( firstItem != null && firstDate != null && firstDate.compareTo( today ) < 0 ) {

                mListView.setAdapter( new AnnivItemsAdapter( context, new ArrayList<>( dal.getAll() ) ) );

            } else if ( global.isOnline() ) {

                // Mise à jour des données
                dal.deleteAll();
                this.apiRequest( view );
                Log.e( TAG, "from web" );

            } else {

                // If no connection or data stored, can't do anything
                Toast.makeText( global, getResources().getString(R.string.internet_unavailable), Toast.LENGTH_LONG ).show();
            }

        } catch ( Exception e ) {

            e.printStackTrace();
        }
    }

    protected void displayResult ( View view, JSONArray jResult ) {

        ArrayList<AnnivItem> annivItemArrayList = getItemList( jResult );

        // If the request was successful, save the items to save data consumption and populate listview
        if ( jResult != null && jResult.length() > 0 ) {

            mListView.setAdapter( new AnnivItemsAdapter( context, getItemList( jResult ) ) );
        }

        view.setAlpha( (float) 1 );

        for ( AnnivItem item : annivItemArrayList ) {

            if ( dal.findId( item.getNom(), item.getPrenom(), item.getPseudo() ) == 0 ) {

                dal.insert( item );
            }
        }
    }
}