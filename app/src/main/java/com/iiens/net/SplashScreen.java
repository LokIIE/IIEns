package com.iiens.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ecran d'attente au lancement
 */
public class SplashScreen extends Activity {

    private GlobalState context;
    private SharedPreferences prefs;
    private RequestQueue queue;

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

        if ( context.isOnline() ) {

            queue.start();

            if( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) {

                Log.d( "FIRSTLAUNCH", "En attente du broadcast ..." );

            } else {

                updateLastActivity();
            }

        } else if ( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) {

            Toast.makeText( this, getResources().getString( R.string.internet_unavailable ), Toast.LENGTH_LONG )
                    .show();

            finish();
        }

        (new Handler()).postDelayed( () -> {

            Class nextActivity = Main.class;

            if( prefs.getBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, true ) ) {

                nextActivity = Intro.class;
            }

            startActivity( new Intent(SplashScreen.this, nextActivity ) );

            finish();

        }, 1000 );
    }

    /**
     * Actions après l'enregistrement du token FCM
     */
    private void sendTokenToServer( RequestQueue queue ) {

        try {

            JSONObject data = new JSONObject();
            data.put( "token", FirebaseInstanceId.getInstance().getToken() );

            JsonObjectRequest request = new JsonObjectRequest(
                context.getFcmURL( R.string.fcm_register ),
                data,
                response -> {

                    if( response.length() > 0 ) {

                        try {

                            fcmTokenRegistered( response.getString("token" ) );

                        } catch ( JSONException e ) {

                            e.printStackTrace();
                        }
                    }
                },
                error -> Log.e( "sendTokenToServer", error.getMessage() )
            );

            queue.add( request );

        } catch ( JSONException e ) {

            e.printStackTrace();
        }
    }

    private void fcmTokenRegistered ( String token ) {

        SharedPreferences.Editor editor = getSharedPreferences(
                getResources().getString( R.string.app_settings ),
                Context.MODE_PRIVATE
        ).edit();

        editor.putString( GlobalState.PrefsConst.APP_TOKEN, token );
        editor.apply();

        Log.d( "tokenReceived", token );
    }

    private void updateLastActivity () {

        try {

            JSONObject data = new JSONObject();
            data.put( "appToken", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );

            Log.d( "appToken", prefs.getString( GlobalState.PrefsConst.APP_TOKEN, "" ) );

            JsonObjectRequest request = new JsonObjectRequest(
                context.getApiURL( R.string.api_launched ),
                data,
                response -> {

                    if( response.length() > 0 ) {

                        Log.d( "updateLastActivity", "SUCCESS" );
                    }
                },
                error -> Log.e( "updateLastActivity", error.getMessage() )
            );

            queue.add( request );

        } catch ( JSONException e ) {

            e.printStackTrace();
        }
    }
}