package com.iiens.net;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ClubPage extends Activity {

    private GlobalState appContext;
    private long enqueue;

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

                Log.d( "FINISHED", url );

                if ( url.endsWith( ".pdf" ) ) {

                    final DownloadManager dm = (DownloadManager) appContext.getSystemService( DOWNLOAD_SERVICE );

                    Uri uri = Uri.parse( url );

                    final DownloadManager.Request request = new DownloadManager.Request( uri );
                    request.setDestinationInExternalFilesDir( ClubPage.this, Environment.DIRECTORY_DOWNLOADS, url.substring( url.lastIndexOf( "/" ) ) );

                    new Thread() {

                        public void run () {

                            enqueue = dm.enqueue( request );
                        }
                    }.start();
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
    public void onBackPressed () {

        finish();
    }
}
