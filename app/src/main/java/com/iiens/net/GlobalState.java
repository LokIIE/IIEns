package com.iiens.net;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.iiens.net.tasks.TaskPingArise;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class GlobalState extends Application {

    private static Bundle appBundle = new Bundle();
    private int currentFragment = 0;

    private SharedPreferences prefs;

    public static CookieManager cookieManager = new CookieManager();

    public Bundle getBundle () {
        return appBundle;
    }

    public void setBundle ( Bundle bundle ) {
        appBundle = bundle;
    }

    public int getCurrentFragment () { return this.currentFragment; }

    public void setCurrentFragment ( int currentFragmentId ) { this.currentFragment = currentFragmentId; }

    public String getScriptURL () { return getResources().getString( R.string.url_apiie ); }

    @Override
    public void onCreate () {

        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.tw_key),
                getString(R.string.tw_secret));
        Fabric.with(this, new Twitter(authConfig));

        cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault( GlobalState.cookieManager );

        prefs = getSharedPreferences(
                getResources().getString( R.string.app_settings ),
                Context.MODE_PRIVATE
        );
    }

    public SharedPreferences getPreferences () {

        return this.prefs;
    }

    /**
     * Vérifie si l'appareil est connecté à Internet
     * @return TRUE ou FALSE
     */
    public boolean isOnline () {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Vérifie la disponibilité des services Arise
     * @return TRUE ou FALSE
     * @throws ExecutionException Exception
     * @throws InterruptedException Exception
     */
    public boolean isAriseAvailable () throws ExecutionException, InterruptedException {

        return ! ( new TaskPingArise( this ).execute().get() instanceof Exception );
    }

    /**
     * Vérifie le APK Google Play Services est disponible
     * @return TRUE ou FALSE
     */
    public boolean checkPlayServices () {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable( this );
        return resultCode == ConnectionResult.SUCCESS;
    }

    static class PrefsConst {

        static String FIRST_LAUNCH = "firstLaunch";
        static String UPDATE_FCM_TOKEN = "updateFcmToken";
        static String HAS_PLAY_SERVICES = "hasPlayServices";
        static String NO_PLAY_SERVICES_DIALOG = "noPlayServicesDialog";
        static String APP_TOKEN = "appToken";
        static String APP_NEW_VERSION = "appNewVersion";
        static String SAVE_PREFERENCES = "savePreferences";
        static String SAVE_CREDENTIALS = "saveCredentials";
        static String SAVED_LOGIN = "savedLogin";
        static String SAVED_PASSWORD = "savedPassword";
    }
}