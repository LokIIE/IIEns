package com.iiens.net;

import android.app.Activity;
import android.app.AlertDialog;
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

import static android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;

/**
 * Ecran d'attente au lancement
 */
public class SplashScreen extends Activity {

    private GlobalState context;
    private SharedPreferences prefs;
    private RequestQueue queue;
    private AlertDialog noPlayServicesDialog = null;

    private final BroadcastReceiver tokenRefreshed = new BroadcastReceiver () {
        @Override
        public void onReceive ( Context context, Intent intent ) {

            Log.d( "tokenRefreshed", "Intent received" );
            sendTokenToServer( queue );
        }
    };

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_splashscreen );

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

        int postDelayed = 1000;

        if ( context.isOnline() ) {

            if( hasPlayServices ) {

                queue.start();

                if( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) Log.d( "FIRSTLAUNCH", "En attente du broadcast ..." );
                else if( prefs.getBoolean( GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false ) ) updateFcmToken();
                else updateDerniereActivite();

                postDelayed = 1000;

            } else if( prefs.getBoolean( GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, true ) ) {

                noPlayServicesDialog = new AlertDialog.Builder( this, Theme_DeviceDefault_Light_Dialog_Alert )
                        .setTitle( R.string.alert_no_gps_title )
                        .setMessage( R.string.alert_no_gps_msg )
                        .show();
                prefs.edit().putBoolean( GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, false ).apply();

                postDelayed = 5000;
            }

        } else if ( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) {

            Toast.makeText( this, getResources().getString( R.string.internet_unavailable ), Toast.LENGTH_LONG )
                    .show();
            finish();
        }

        (new Handler()).postDelayed( new Runnable() {

            @Override
            public void run () {

                if ( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) {

                    startActivity( new Intent( SplashScreen.this, IntroActivity.class ) );

                } else {

                    startActivity( new Intent( SplashScreen.this, Main.class ) );
                }
                finish();

                if( noPlayServicesDialog != null && noPlayServicesDialog.isShowing() ) noPlayServicesDialog.dismiss();
            }
        }, postDelayed );
    }

    /**
     * Actions après l'enregistrement du token FCM
     */
    private void sendTokenToServer( RequestQueue queue ) {

        try {

            Resources res = getResources();
            String url = res.getString( R.string.url_fcm_api ) + res.getString( R.string.fcm_register );

            JSONObject data = new JSONObject();
            data.put( "token", FirebaseInstanceId.getInstance().getToken() );

            JsonObjectRequest request = new JsonObjectRequest( url, data,
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

                                    Log.d( "sendTokenToServer", "SUCCESS, token : " + response.getString( "token" ) );

                                } catch ( JSONException e ) {

                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse ( VolleyError error ) {

                            Log.e( "sendTokenToServer", error.getMessage() );
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
    private void updateFcmToken () {

        new Thread( new Runnable() {
            @Override
            public void run() {

                try {

                    FirebaseInstanceId.getInstance().deleteInstanceId();

                    Resources res = getResources();
                    String url = res.getString( R.string.url_fcm_api ) + res.getString( R.string.fcm_unregister );

                    JSONObject data = new JSONObject();
                    data.put( "app_token", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );

                    JsonObjectRequest request = new JsonObjectRequest( url, data,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse ( JSONObject response ) {

                                    if( response.length() > 0 ) {

                                        SharedPreferences.Editor editor = getSharedPreferences(
                                                getResources().getString( R.string.app_settings ),
                                                Context.MODE_PRIVATE
                                        ).edit();

                                        editor.putBoolean( GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false );
                                        editor.apply();

                                        Log.d( "updateFcmToken", "SUCCESS, token deleted, generating new token" );

                                        FirebaseInstanceId.getInstance().getToken();
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
        }).start();
    }

    private void updateDerniereActivite () {

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
}