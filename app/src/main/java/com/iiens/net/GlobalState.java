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

import com.iiens.net.tasks.TaskPingArise;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;

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
    private Fragment currentFragment = null;
    private ArrayMap<String, String> userInfos = new ArrayMap<>();
    private boolean connectionOAuthStatus = false;

    private SharedPreferences prefs;

    private TwitterConfig twitterConfig;
    private TwitterListTimeline twListTimeline;

    public static CookieManager cookieManager = new CookieManager();

/*    public DownloadManager dm;
    public long enqueue;

    public BroadcastReceiver onComplete = new BroadcastReceiver () {

        public void onReceive ( Context ctxt, Intent intent ) {
            // check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if( referenceId == enqueue ) {

                try {

                    String action = intent.getAction();

                    if ( DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals( action ) ) {

                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById( enqueue );
                        Cursor c = dm.query( query );

                        if ( c.moveToFirst() ) {

                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                            if ( DownloadManager.STATUS_SUCCESSFUL == c.getInt( columnIndex ) ) {

                                String uriString = c.getString( c.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
                                openPdf(  uriString );
                            }
                        }
                    }
                } catch ( ActivityNotFoundException e ) {

                    Log.e( "onComplete", "ActivityNotFound" );
                }
            }
        }
    };*/

    public Bundle getBundle () {
        return appBundle;
    }

    public void setBundle ( Bundle bundle ) {
        appBundle = bundle;
    }

    public Fragment getCurrentFragment () { return ( this.currentFragment != null ) ? this.currentFragment : new Home(); }

    public void setCurrentFragment ( Fragment value ) { this.currentFragment = value; }

    public String getApiURL () { return getResources().getString( R.string.url_api ); }

    public String getApiURL ( int apiKeyId ) {

        return this.getApiURL() + getResources().getString( apiKeyId );
    }

    public String getFcmURL () { return getResources().getString( R.string.url_fcm_api ); }

    public String getFcmURL ( int apiKeyId ) {

        return this.getFcmURL() + getResources().getString( apiKeyId );
    }

    @Override
    public void onCreate () {

        super.onCreate();

        cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault( GlobalState.cookieManager );

        // dm = (DownloadManager) getSystemService( DOWNLOAD_SERVICE );

        this.prefs = getSharedPreferences(
                getResources().getString( R.string.app_settings ),
                Context.MODE_PRIVATE
        );

        this.twitterConfig = new TwitterConfig.Builder( this )
                .twitterAuthConfig(
                        new TwitterAuthConfig(
                                getString(R.string.tw_key),
                                getString(R.string.tw_secret)
                        )
                ).build();
        com.twitter.sdk.android.core.Twitter.initialize( this.twitterConfig );

        this.twListTimeline = new TwitterListTimeline.Builder()
                .id( Long.valueOf( getResources().getString( R.string.tw_list_id ) ) )
                .build();
    }

    /**
     * G�n�ration dynamique de l'Adapter des tweets
     * @return Adapter
     */
    public TweetTimelineListAdapter getTwListAdapter () {

        return new TweetTimelineListAdapter.Builder(this )
                .setTimeline( twListTimeline )
                .setViewStyle( prefs.getBoolean( getString( R.string.pref_mode_nuit_key ), false ) ? R.style.TweetDarkStyle : R.style.TweetLightStyle )
                .build();
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
     * V�rifie si l'appareil est connect� � Internet
     * @return true si connect�
     */
    public boolean isOnline () {

        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo netInfo = ( cm != null ) ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * V�rifie la disponibilit� des services Arise
     * @return true si joignable
     * @throws ExecutionException Exception
     * @throws InterruptedException Exception
     */
    public boolean isAriseAvailable () throws ExecutionException, InterruptedException {

        return ! ( new TaskPingArise( this ).execute().get() instanceof Exception );
    }

    /*public Thread downloadPdf ( Uri uri ) {

        String filename = uri.toString().substring(  uri.toString().lastIndexOf( "/" ) + 1 );

        final DownloadManager.Request request = new DownloadManager.Request( uri );
        request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, filename );

        return new Thread() {

            public void run () { enqueue = dm.enqueue( request ); }

        };
    }*/

    public void openPdf ( String uriString ) {

        try {

            String filename = uriString.substring( uriString.lastIndexOf( "/" ) + 1 );
            File file = new File( getFilesDir(), filename );

            Uri pdfURI = FileProvider.getUriForFile( this, "com.iiens.net.fileprovider", file );
            Intent pdfIntent = new Intent();
            pdfIntent.setAction( Intent.ACTION_VIEW )
                    .setDataAndType( pdfURI, "application/pdf" )
                    .addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );

            if ( pdfIntent.resolveActivity( getPackageManager() ) != null ) {

                startActivity( pdfIntent );
            }

        } catch ( Exception e ) {

            Log.d( "FileProviderError", e.getMessage() );
        }
    }

    static class PrefsConst {

        static String FIRST_LAUNCH = "firstLaunch";
        static String APP_TOKEN = "appToken";
        static String APP_NEW_VERSION = "appNewVersion";
    }
}