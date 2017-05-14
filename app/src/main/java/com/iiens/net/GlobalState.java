package com.iiens.net;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class GlobalState extends Application {

    public static boolean debug = true;

    private static Bundle appBundle = new Bundle();
    private static Resources resources;
    private int currentFragment = 0;

    public static CookieManager cookieManager = new CookieManager();

/*    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/

    public Bundle getBundle () {
        return appBundle;
    }

    public void setBundle ( Bundle bundle ) {
        appBundle = bundle;
    }

    public int getCurrentFragment () { return this.currentFragment; }

    public void setCurrentFragment ( int currentFragmentId ) { this.currentFragment = currentFragmentId; }

    public String getScriptURL () {
        return resources.getString(R.string.url_apiie);
    }

    @Override
    public void onCreate () {

        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.tw_key),
                getString(R.string.tw_secret));
        Fabric.with( this, new Twitter( authConfig ) );

        resources = getResources();

        cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault( GlobalState.cookieManager );

        AppStartAsyncTask at = new AppStartAsyncTask( getApplicationContext() );

        try {

            boolean ariseOnline = at.execute().get();
            if ( !ariseOnline ) {

                Toast.makeText( getApplicationContext(), resources.getString(R.string.arise_unavailable), Toast.LENGTH_LONG ).show();
            }

        } catch ( InterruptedException | ExecutionException e ) {

            e.printStackTrace();
        }
    }

    public boolean isOnline () {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return ( netInfo != null && netInfo.isConnectedOrConnecting() );
    }

    public static class PrefsConst {

        static String FIRST_LAUNCH = "firstLaunch";
        static String UPDATE_FCM_TOKEN = "updateFcmToken";
        static String HAS_PLAY_SERVICES = "hasPlayServices";
        static String NO_PLAY_SERVICES_DIALOG = "noPlayServicesDialog";
        static String SAVED_FCM_TOKEN = "savedFcmToken";
        static String APP_NEW_VERSION = "appNewVersion";
        static String SAVE_PREFERENCES = "savePreferences";
        static String SAVE_CREDENTIALS = "saveCredentials";
        static String SAVED_LOGIN = "savedLogin";
        static String SAVED_PASSWORD = "savedPassword";
    }
}