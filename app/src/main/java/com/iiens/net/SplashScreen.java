package com.iiens.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.concurrent.ExecutionException;

/**
 * Ecran d'attente au lancement
 */
public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    // private static final String TWITTER_KEY = "S3TQM4DSB34hwSuigO1EccDzZ";
    // private static final String TWITTER_SECRET = "nmxP2Dr3psD97pDLIf78EM6PJarw45Vor8Kodg3PQbq0Bnri3N";

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        //Fabric.with(this, new Twitter(authConfig));
        setContentView( R.layout.activity_splash );


//        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(SplashScreen.class.getSimpleName(),
//                Context.MODE_PRIVATE);
//
//        if (checkPlayServices()) {
//            prefs.edit().putBoolean(GlobalState.PrefsConst.HAS_PLAY_SERVICES, true).apply();
//
//            // Verify if a gcm registration id exists, if not generates it
//            // GCM needs Google Play Services to function correctly
//            if (prefs.getBoolean(GlobalState.PrefsConst.HAS_PLAY_SERVICES, false)) {
//                if (prefs.getBoolean(GlobalState.PrefsConst.FIRST_LAUNCH, true)
//                        || prefs.getBoolean(GlobalState.PrefsConst.UPDATE_FCM_TOKEN, true)) {
//
//                    // TODO : vérification existence token, sinon création
//                    if (true) {
//                        String fcmToken = FirebaseInstanceId.getInstance().getToken();
//
//                        if (GlobalState.debug) {
//                            Log.d(this.getLocalClassName(), "New token : " + fcmToken);
//                        }
//                        // TODO : envoi token en base de données
//
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean(GlobalState.PrefsConst.FIRST_LAUNCH, false);
//                        editor.putBoolean(GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false);
//                       editor.putString(GlobalState.PrefsConst.SAVED_FCM_TOKEN, fcmToken);
//                        editor.commit();
//                    }
//                } else {
//
//                    if (GlobalState.debug) {
//                        Log.d(this.getLocalClassName(), "No Google Play Service");
//                    }
//                }
//        } else {
//            // TODO : dialogue pour notifications non dispo (1 fois)
//            if (prefs.getBoolean(GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, true)) {
//                prefs.edit().putBoolean(GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, false).apply();
//            }
//        }


//        }
    }

    @Override
    protected void onStart () {

        super.onStart();

        if ( ((GlobalState) getApplicationContext()).isOnline() ) {

            try {

                Boolean statutArise = ((GlobalState) getApplicationContext()).isAriseAvailable();
                displayActivity( statutArise ? Login.class : Main.class );

            } catch ( InterruptedException | ExecutionException e) {

                e.printStackTrace();
            }

        } else {

            displayActivity( Main.class );
            Toast.makeText( this, getResources().getString( R.string.internet_unavailable ), Toast.LENGTH_LONG )
                    .show();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable( this );
        if ( resultCode != ConnectionResult.SUCCESS ) {
            if ( googleAPI.isUserResolvableError( resultCode ) ) {
                googleAPI.getErrorDialog(
                        this,
                        resultCode,
                        9000
                ).show();
            }

            return false;
        }

        return true;
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