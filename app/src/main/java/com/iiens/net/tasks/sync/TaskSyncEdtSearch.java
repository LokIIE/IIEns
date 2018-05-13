package com.iiens.net.tasks.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.iiens.net.GlobalState;
import com.iiens.net.R;
import com.iiens.net.database.AppDb;
import com.iiens.net.database.EdtSearchCategoryDao;
import com.iiens.net.database.EdtSearchOptionDao;
import com.iiens.net.model.EdtSearchCategory;
import com.iiens.net.model.EdtSearchOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Mise à jour des options de l'emploi du temps
 */

public class TaskSyncEdtSearch extends AsyncTask<Void, Void, Boolean> {

    private GlobalState context;
    private EdtSearchCategoryDao dalCategory;
    private EdtSearchOptionDao dalOptions;
    private String url;

    public TaskSyncEdtSearch ( GlobalState context ) {

        this.context = context;

        AppDb db = AppDb.getAppDb( context );
        this.dalCategory = db.edtSearchCategoryDao();
        this.dalOptions = db.edtSearchOptionDao();
        this.url = context.getApiURL( R.string.api_edtOptions );
    }

    @Override
    protected Boolean doInBackground ( Void... voids ) {

        try {

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    this.url,
                    null,
                    response -> {

                        Log.d( "REPONSE", response.toString());
                        try {

                            dalCategory.deleteAll();
                            dalOptions.deleteAll();

                            JSONObject elements = response.getJSONObject( 0 );

                            JSONObject conf = elements.getJSONObject( "conf" );
                            JSONObject confBase = conf.getJSONObject( "base" );
                            Iterator<String> confBaseKeys = confBase.keys();

                            while ( confBaseKeys.hasNext() ) {

                                String key = confBaseKeys.next();

                                EdtSearchCategory edtCategory = new EdtSearchCategory();
                                edtCategory.setPromo( "" );
                                edtCategory.setLabel( "" );
                                edtCategory.setName( key );
                                edtCategory.setValue( key );
                                dalCategory.insert( edtCategory );

                                // Ajout des éléments du select au spinnerAdapter respectif
                                JSONObject spinnerConfig = elements.getJSONObject( key );
                                String label = spinnerConfig.names().get( 0 ).toString();

                                Log.d( "label", label );

                                JSONArray spinnerElements = spinnerConfig.getJSONArray( label );

                                for ( int i = 0; i < spinnerElements.length(); i++ ) {
                                    JSONObject option = spinnerElements.getJSONObject( i );

                                    EdtSearchOption edtOption = new EdtSearchOption( i, option.getString( "text" ), option.getString( "value" ) );
                                    dalOptions.insert( edtOption );
                                }
                            }

                        } catch ( JSONException e ) {

                            Log.e( "SYNC EDT SEARCH", "Error parsing data " );
                        }
                    },
                    error -> Log.e( "SYNC EDT SEARCH", "API FAIL" )
            );

            queue.add(request);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return true;
    }

}
