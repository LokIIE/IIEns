package com.iiens.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iiens.net.gcm.GCMRegisterApp;

import java.io.File;

/**
 * SplashScreen
 * Animation lors du lancement de l'appli et création d'un compte sur GCM pour les notifications
 */

public class SplashScreen extends Activity {

    /* Activity parameters */
    private static final int SPLASH_TIME_OUT = 5000; // Splash screen animation duration
    /* Google Cloud Messaging parameters */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCMRelated";
    private boolean backPressed = false; // Was back button pressed during the animation
    private SharedPreferences preferences;
    private GlobalState global;
    private GoogleCloudMessaging gcm;
    private boolean ariseOnline = false;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /* Initialize the variables needed for this activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (GlobalState) getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(getResources().getString(R.string.bool_storage_option_name), false)) {
            for (File file : getFilesDir().listFiles()) {
                file.delete();
            }
        }

        setContentView(R.layout.activity_splash);
    }

    /* Actions when the view is displayed on the screen */
    @Override
    protected void onStart() {
        super.onStart();
        startLogoAnimation(R.anim.top_in);

        // End if there is no Internet connection
        if (!global.isOnline()) {
            Toast.makeText(global, getResources().getText(R.string.internet_unavailable), Toast.LENGTH_SHORT).show();
            finish();
        }

        // GCM needs Google Play Services to function correctly
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            if (getRegistrationId().isEmpty()) {
                new GCMRegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.google_play_services_unavailable), Toast.LENGTH_SHORT).show();
        }

        // Go to main activity after the duration of the logo animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PreferenceManager.setDefaultValues(getApplicationContext(), R.layout.preferences, false);
                goToNextActivity();
            }
        }, SPLASH_TIME_OUT);

    }

    /* Back button toggled during animation <=> the user didn't want to launch the application */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
    }

    /* Animation for the splash screen logo */
    private void startLogoAnimation(int animation) {
        Animation anim = AnimationUtils.loadAnimation(this, animation);
        ImageView iv = (ImageView) findViewById(R.id.imgLogo);
        iv.startAnimation(anim);
    }

    /* Start the main activity if the user didn't cancel by pressing the back key */
    private void goToNextActivity() {
        Intent i = new Intent(SplashScreen.this, Main.class);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        // backPressed value has to be checked here because of the handler delay
        if (!backPressed) {
            // Main activity requires either data stored on the device or an internet connection
            if (preferences.getBoolean("storage_option", false) || global.isOnline()) {
                startActivity(i);
            }
        }

        // Destroy this activity (no longer needed)
        finish();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        // Persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}