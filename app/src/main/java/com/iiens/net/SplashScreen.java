package com.iiens.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Ecran d'attente au lancement
 */
public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    // private static final String TWITTER_KEY = "S3TQM4DSB34hwSuigO1EccDzZ";
    // private static final String TWITTER_SECRET = "nmxP2Dr3psD97pDLIf78EM6PJarw45Vor8Kodg3PQbq0Bnri3N";

    GlobalState context;
    SharedPreferences prefs;
    RequestQueue queue;

    BroadcastReceiver tokenRefreshed = new BroadcastReceiver () {
        @Override
        public void onReceive ( Context context, Intent intent ) {

            Log.d( "tokenRefreshed", "Intent received" );

            onFirstLaunch( queue );
        }
    };

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_splash);

        context = (GlobalState) getApplicationContext();
        prefs = context.getPreferences();
        queue = Volley.newRequestQueue( this );

        LocalBroadcastManager.getInstance(this).registerReceiver(
                tokenRefreshed,
                new IntentFilter( "tokenRefreshed" )
        );
    }

    @Override
    protected void onStart () {

        super.onStart();

        boolean hasPlayServices = context.checkPlayServices();
        prefs.edit().putBoolean( GlobalState.PrefsConst.HAS_PLAY_SERVICES, hasPlayServices ).apply();

        if ( context.isOnline() ) {

            try {

                Boolean statutArise = context.isAriseAvailable();
                displayActivity( statutArise ? Login.class : Main.class );

            } catch ( InterruptedException | ExecutionException e) {

                e.printStackTrace();
            }

            if( hasPlayServices ) {

                queue.start();

                if( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) Log.d( "FIRSTLAUNCH", "En attente du broadcast ..." );
                else if( prefs.getBoolean( GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false ) ) updateFcmToken( context, queue );
                else updateDerniereActivite( context, queue );

            } else {

                // TODO : afficher popup Google Play Services manquants
                prefs.edit().putBoolean( GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, false ).apply();
            }

        } else {

            Toast.makeText( this, getResources().getString( R.string.internet_unavailable ), Toast.LENGTH_LONG )
                    .show();

            if ( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) finish();
            else displayActivity( Main.class );
        }
    }

    /**
     * Actions après l'enregistrement du token FCM
     */
    private void onFirstLaunch ( RequestQueue queue ) {

        try {

            Resources res = getResources();
            String registerUrl = res.getString( R.string.url_fcm_api ) + res.getString( R.string.fcm_register );
            JSONObject data = new JSONObject();
            data.put( "token", FirebaseInstanceId.getInstance().getToken() );

            JsonObjectRequest request = new JsonObjectRequest( registerUrl, data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse ( JSONObject response ) {

                            if( response.length() > 0 ) {

                                try {

                                    SharedPreferences.Editor editor = getSharedPreferences(
                                            getResources().getString( R.string.app_settings ),
                                            Context.MODE_PRIVATE
                                    ).edit();

                                    editor.putBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, false );
                                    editor.putBoolean( GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false );
                                    editor.putString( GlobalState.PrefsConst.APP_TOKEN, response.getString( "token" ) );
                                    editor.apply();

                                    Log.d( "onFirstLaunch", "SUCCESS, token : " + response.getString( "token" ) );

                                } catch ( JSONException e ) {

                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse ( VolleyError error ) {

                            Log.e( "onFirstLaunch", error.getMessage() );
                        }
                    }
            );

            queue.add( request );

        } catch ( JSONException e ) {

            e.printStackTrace();
        }
    }

    /**
     * Mise à jour du token FCM
     */
    private void updateFcmToken ( GlobalState context, final RequestQueue queue ) {

        // TODO
        try {

            FirebaseInstanceId.getInstance().deleteInstanceId();

            final String token = FirebaseInstanceId.getInstance().getToken();

            Resources res = getResources();
            String registerUrl = res.getString( R.string.url_fcm_api ) + res.getString( R.string.fcm_register );
            JSONObject data = new JSONObject();
            data.put( "appToken", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );
            data.put( "token", token );

            JsonObjectRequest request = new JsonObjectRequest( registerUrl, data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse ( JSONObject response ) {

                            if( response.length() > 0 ) {

                                try {

                                    SharedPreferences.Editor editor = getSharedPreferences(
                                            getResources().getString( R.string.app_settings ),
                                            Context.MODE_PRIVATE
                                    ).edit();

                                    editor.putBoolean( GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false );
                                    editor.putString( GlobalState.PrefsConst.APP_TOKEN, response.getString( "token" ) );
                                    editor.apply();

                                    Log.d( "updateFcmToken", "SUCCESS" );

                                } catch ( JSONException e ) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse ( VolleyError error ) {

                            Log.e( "updateFcmToken", error.getMessage() );
                        }
                    }
            );

            queue.add( request );

        } catch ( JSONException | IOException e ) {

            e.printStackTrace();
        }
    }

    private void updateDerniereActivite ( GlobalState context, final RequestQueue queue ) {

        try {

            Resources res = getResources();
            String url = res.getString( R.string.url_apiie ) + res.getString( R.string.apiie_appLaunched );
            JSONObject data = new JSONObject();
            data.put( "appToken", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );

            Log.d( "appToken", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );

            JsonObjectRequest request = new JsonObjectRequest( url, data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse ( JSONObject response ) {

                            if( response.length() > 0 ) {

                                Log.d( "updateDerniereActivite", "SUCCESS" );
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse ( VolleyError error ) {

                            Log.e( "updateDerniereActivite", error.getMessage() );
                        }
                    }
            );

            queue.add( request );

        } catch ( JSONException e ) {

            e.printStackTrace();
        }
    }

    private void displayActivity ( final Class<?> intentTarget ) {

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                startActivity( new Intent( SplashScreen.this, intentTarget ) );
                finish();
            }
        }, 1000);
    }
}