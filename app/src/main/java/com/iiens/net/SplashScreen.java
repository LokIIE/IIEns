package com.iiens.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * SplashScreen
 * Animation lors du lancement de l'appli et création d'un compte sur GCM pour les notifications
 */

public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "S3TQM4DSB34hwSuigO1EccDzZ";
    private static final String TWITTER_SECRET = "nmxP2Dr3psD97pDLIf78EM6PJarw45Vor8Kodg3PQbq0Bnri3N";


    private static final int SPLASH_TIME_OUT = 2000; // Splash screen duration
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000; // Constante arbitraire

    /* Initialize the variables needed for this activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_splash);

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);

        if (checkPlayServices()) {
            prefs.edit().putBoolean(GlobalState.PrefsConst.HAS_PLAY_SERVICES, true).apply();

            // Verify if a gcm registration id exists, if not generates it
            // GCM needs Google Play Services to function correctly
            if (prefs.getBoolean(GlobalState.PrefsConst.HAS_PLAY_SERVICES, false)) {
                if (prefs.getBoolean(GlobalState.PrefsConst.FIRST_LAUNCH, true)
                        || prefs.getBoolean(GlobalState.PrefsConst.UPDATE_FCM_TOKEN, true)) {

                    // TODO : vérification existence token, sinon création
                    if (true) {
                        String fcmToken = FirebaseInstanceId.getInstance().getToken();

                        if (GlobalState.debug) {
                            Log.d(this.getLocalClassName(), "New token : " + fcmToken);
                        }
                        // TODO : envoi token en base de données

//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putBoolean(GlobalState.PrefsConst.FIRST_LAUNCH, false);
//                        editor.putBoolean(GlobalState.PrefsConst.UPDATE_FCM_TOKEN, false);
//                        editor.putString(GlobalState.PrefsConst.SAVED_FCM_TOKEN, fcmToken);
//                        editor.commit();
                    }
                } else {

                    if (GlobalState.debug) {
                        Log.d(this.getLocalClassName(), "No Google Play Service");
                    }
                }
        } else {
            // TODO : dialogue pour notifications non dispo (1 fois)
            if (prefs.getBoolean(GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, true)) {
                prefs.edit().putBoolean(GlobalState.PrefsConst.NO_PLAY_SERVICES_DIALOG, false).apply();
            }
        }


        }
    }

    /* Actions when the view is displayed on the screen */
    @Override
    protected void onStart() {
        super.onStart();

        // Go to main activity after the duration of the logo animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Main.class);

                startActivity(i);

                // Destroy this activity (no longer needed)
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }
}