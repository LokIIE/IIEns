package com.iiens.net;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ClubPage extends Activity {

    private GlobalState appContext;
    private long enqueue;
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
                                try {

                                    String filename = uriString.substring( uriString.lastIndexOf( "/" ) + 1 );
                                    File file = new File( new File( appContext.getFilesDir(), "Download" ) , filename );

                                    Uri pdfURI = FileProvider.getUriForFile( ClubPage.this, "com.iiens.net.fileprovider", file );
                                    Log.d( "LocalURI", uriString );
                                    Log.d( "ContentURI", pdfURI.toString() );
                                    Intent shareIntent = new Intent( Intent.ACTION_VIEW );
                                    shareIntent.setDataAndType( pdfURI, "application/pdf" )
                                            .setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );

                                    PackageManager pm = getPackageManager();
                                    if ( shareIntent.resolveActivity( pm ) != null ) startActivity( shareIntent );

                                } catch ( Exception e ) {

                                    Log.d( "FileProviderError", e.getMessage() );
                                }
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
                    Log.d( "FINISHED", url );

                    if ( url.endsWith( ".pdf" ) ) {

                        dm = (DownloadManager) appContext.getSystemService( DOWNLOAD_SERVICE );

                        Uri uri = Uri.parse( url );
                        String filename = url.substring( url.lastIndexOf( "/" ) + 1 );

                        Log.d( "filename", filename );

                        final DownloadManager.Request request = new DownloadManager.Request( uri );
                        request.setDestinationInExternalFilesDir( ClubPage.this, Environment.DIRECTORY_DOWNLOADS, filename );

                        new Thread() {

                            public void run () { enqueue = dm.enqueue( request ); }

                        }.start();
                    }

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

    @Override
    public void onStart () {

        super.onStart();

        registerReceiver( onComplete,
                new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
    }

    @Override
    public void onBackPressed () {

        unregisterReceiver( onComplete );
        finish();
    }
}
