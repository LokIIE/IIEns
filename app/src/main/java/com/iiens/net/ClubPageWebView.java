package com.iiens.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;

public class ClubPageWebView extends BaseFragment {

    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        this.layoutId = R.layout.clubpage;
    }

    protected void generateView ( View view ) {

        WebView wv = (WebView) view.findViewById( R.id.clubpage_webview );
        wv.setWebViewClient( new WebViewClient() {

            @Override
            public void onLoadResource ( WebView view, String url ) {}

            @Override
            public void onPageFinished ( WebView view, String url ) {

                Log.d( "FINISHED", url );
            }
        });

        WebSettings wv_settings = wv.getSettings();
        wv_settings.setJavaScriptEnabled( true );
        wv_settings.setBuiltInZoomControls( true );
        wv_settings.setDisplayZoomControls( false );

        String url = getArguments().getString( "url" );

        if( url != null ) wv.loadUrl( url );
    }

    protected void displayResult ( View view, JSONArray result ) {}
}
