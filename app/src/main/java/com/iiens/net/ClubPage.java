package com.iiens.net;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ClubPage extends AppCompatActivity {

    private GlobalState appContext;

    public long enqueue;
    private DownloadManager dm;

    BroadcastReceiver onComplete = new BroadcastReceiver () {

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
                                appContext.openPdf( uriString );
                            }
                        }
                    }
                } catch ( ActivityNotFoundException e ) {

                    Log.e( "onComplete", "ActivityNotFound" );
                }
            }
        }
    };

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_clubpage );

        appContext = (GlobalState) this.getApplicationContext();

        WebView wv = (WebView) findViewById( R.id.clubpage_webview );
        wv.setWebViewClient( new WebViewClient() {

            @Override
            public void onLoadResource ( WebView view, String url ) {

            }

            @Override
            public void onPageFinished ( WebView view, String url ) {

                try {

                    url = URLDecoder.decode( url, "UTF-8" );

                    if ( url.endsWith( ".pdf" ) ) downloadPdf( url );

                } catch ( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                }
            }
        } );

        WebSettings wv_settings = wv.getSettings();
        wv_settings.setJavaScriptEnabled( true );
        wv_settings.setBuiltInZoomControls( true );
        wv_settings.setDisplayZoomControls( false );

        String url = getIntent().getStringExtra( "url" );

        if ( url != null ) wv.loadUrl( url );
    }

//    @Override
//    public void onStart () {
//
//        super.onStart();
//
//        registerReceiver( this.onComplete,
//                new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
//    }

    @Override
    public void onBackPressed () {

        // unregisterReceiver( onComplete );
        finish();
    }

    public void downloadPdf ( String url ) {

        Uri uri = Uri.parse( url );
        String filename = url.substring( url.lastIndexOf( "/" ) + 1 );

        File fileToDownload = new File( getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );

        if( fileToDownload.exists() ) {

            fileToDownload.delete();
//            appContext.openPdf( Uri.fromFile( fileToDownload ).toString() );

        } // else {

            dm = (DownloadManager) getSystemService( DOWNLOAD_SERVICE );
            final DownloadManager.Request request = new DownloadManager.Request( uri );
            request.setDestinationInExternalFilesDir( this, Environment.DIRECTORY_DOWNLOADS, filename )
                    .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );

            new Thread() {
                public void run() {
                    enqueue = dm.enqueue( request );
                }
            }.start();
//        }
    }
}
