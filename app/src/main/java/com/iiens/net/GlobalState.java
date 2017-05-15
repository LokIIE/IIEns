package com.iiens.net;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.iiens.net.tasks.TaskPingArise;
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
    private int currentFragment = 0;

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
    }

    public boolean isOnline () {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isAriseAvailable () throws ExecutionException, InterruptedException {

        return ! ( new TaskPingArise( this ).execute().get() instanceof Exception );
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