package com.iiens.net;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.HttpCookie;
import java.util.List;

public class Itvtube extends Activity {

    WebView wv = null;

    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.itvtube );

        wv = (WebView) findViewById( R.id.itvtube_webview );
        wv.setWebViewClient( new WebViewClient() {

            @Override
            public void onLoadResource ( WebView view, String url ) {

//                Log.d( "onLoadResource", url );
//
//                if( url == getResources().getString(R.string.url_itvtube_tags) || url == getResources().getString(R.string.url_itvtube_videos) || url == getResources().getString(R.string.url_itvtube_raw) ) {
//
//                    List<HttpCookie> cookies = GlobalState.cookieManager.getCookieStore().getCookies();
//                    for ( HttpCookie cookie : cookies ) {
//
//                        Log.d( "COOKIE" , cookie.toString() );
//                    }
//                }
            }

            @Override
            public void onPageFinished ( WebView view, String url ) {

                Log.d( "FINISHED", url );

                List<HttpCookie> cookies = GlobalState.cookieManager.getCookieStore().getCookies();
                for ( HttpCookie cookie : cookies ) {

                    Log.d( "COOKIE", cookie.toString() );
                }
            }
        });

        WebSettings wv_settings = wv.getSettings();
        wv_settings.setJavaScriptEnabled( true );
        wv_settings.setBuiltInZoomControls( true );
        wv_settings.setDisplayZoomControls( false );

        if ( savedInstanceState == null ) {

            wv.loadUrl( getResources().getString( R.string.url_itvtube ) );
        }
    }

    @Override
    public void onConfigurationChanged ( Configuration newConfig ) {

        super.onConfigurationChanged( newConfig );
    }

    @Override
    protected void onSaveInstanceState ( Bundle outState ) {

        super.onSaveInstanceState( outState );
        wv.saveState( outState );
    }

    @Override
    protected void onRestoreInstanceState ( Bundle savedInstanceState ) {

        super.onRestoreInstanceState( savedInstanceState );
        wv.restoreState( savedInstanceState );
    }
}
