package com.iiens.net;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.iiens.net.tasks.TaskPingArise;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class GlobalState extends Application {

    private static Bundle appBundle = new Bundle();
    private String AUTHORITY = "com.iiens.net.fileprovider";
    private Fragment currentFragment = null;
    private ArrayMap<String, String> userInfos = new ArrayMap<>();
    private boolean connectionOAuthStatus = false;

    private SharedPreferences prefs;

    public static CookieManager cookieManager = new CookieManager();

    public Bundle getBundle () {
        return appBundle;
    }

    public void setBundle ( Bundle bundle ) {
        appBundle = bundle;
    }

    public Fragment getCurrentFragment () { return ( this.currentFragment != null ) ? this.currentFragment : new News(); }

    public void setCurrentFragment ( Fragment value ) { this.currentFragment = value; }

    public String getScriptURL () { return getResources().getString( R.string.url_apiie ); }

    @Override
    public void onCreate () {

        super.onCreate();

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

    public GlobalState setOauthConnected ( boolean status ) {

        this.connectionOAuthStatus = status;
        return this;
    }

    public boolean isOauthConnected () {

        return connectionOAuthStatus;
    }

    public GlobalState setUserInfos ( JSONObject data ) {

        this.userInfos.clear();

        try {

            Iterator keys = data.keys();
            while( keys.hasNext() )  {

                String key = (String) keys.next();
                this.userInfos.put( key, data.getString( key ) );
            }

        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return this;
    }

    public String getUserInfo ( String key ) {

        return this.userInfos.get( key );
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

    public void openPdf ( String uriString ) {

        try {

            String filename = uriString.substring( uriString.lastIndexOf( "/" ) + 1 );
            File file = new File( new File( this.getFilesDir(), "Download" ) , filename );

            Uri pdfURI = FileProvider.getUriForFile( GlobalState.this, AUTHORITY, file );
            Log.d( "LocalURI", uriString );
            Log.d( "ContentURI", pdfURI.toString() );
            Intent intent = new Intent( Intent.ACTION_VIEW );
            intent.setDataAndType( pdfURI, "application/pdf" )
                    .setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );

            if ( intent.resolveActivity( getPackageManager() ) != null ) startActivity( intent );

        } catch ( Exception e ) {

            Log.d( "FileProviderError", e.getMessage() );
        }
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