package com.iiens.net;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AriseWebViewClient {

    public WebView webView;
    private Activity context;
    private AriseInterface callback;
    private JSInterface jsInterface;

    private LinearLayout container;
    private Button btnCloseWebview;

    public AriseWebViewClient (Activity ctx, final AriseInterface callback ) {

        this.context = ctx;
        this.container = (LinearLayout) ctx.findViewById(R.id.arise_webview_container);
        this.webView = (WebView) ctx.findViewById(R.id.arise_webview);
        this.callback = callback;

        this.jsInterface = new JSInterface();

        this.webView.setWebChromeClient( new WebChromeClient() );
        this.webView.setWebViewClient( new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl( getJsScript() );
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading( WebView view, WebResourceRequest request ) {
                return true;
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading( WebView view, String url ) {
                view.loadUrl(url);
                return true;
            }
        });

        this.btnCloseWebview = (Button) ctx.findViewById(R.id.arise_webview_close);
        this.btnCloseWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsInterface.hideWebView();
            }
        });

        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.addJavascriptInterface( jsInterface, "HtmlViewer" );
    }

    public String getJsScript () {

        return "javascript:( function () {" +
            "window.HtmlViewer.hideWebView();" +
            "var credential = document.getElementsByClassName('credential');" +
            "var autorize = document.getElementsByClassName('well');" +
            "if( credential.length > 0 ) {" +
                "credential = credential[0].innerHTML;" +
                "window.HtmlViewer.autoSendVerifier( credential );" +
            "} else if( autorize.length > 0 ) {" +
                "window.HtmlViewer.expandWebView();" +
            "}"+
        "})();";
    }

    public void loadUrl ( String url ) {

        this.webView.loadUrl( url );
    }

    public interface AriseInterface {

        void onConnectionSuccess ();
    }

    public class JSInterface {

        String ariseVerifier = "";

        JSInterface () {}

        @JavascriptInterface
        public void autoSendVerifier (String content) {

            ariseVerifier = content.replace("<wbr xmlns=\"http://www.w3.org/1999/xhtml\" />", "");

            try {

                RequestQueue queue = Volley.newRequestQueue( context );
                JSONObject postData = new JSONObject();

                postData.put( "verifier", ariseVerifier );

                JsonObjectRequest loginVerifier = new JsonObjectRequest( context.getResources().getString(R.string.apiie_login_arise), postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    if( response.has("connection_status") && response.getString("connection_status").equals("ok") ) {

                                        callback.onConnectionSuccess();

                                    } else {

                                        throw new JSONException( response.getString("message") );
                                    }

                                } catch (JSONException e) {

                                    Toast.makeText( context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText( context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                queue.add( loginVerifier );

            } catch ( Exception e ) {

                Toast.makeText( context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @JavascriptInterface
        public void expandWebView () {
            // Log.d( "JSInterface", "expandWebView" );
            context.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams containerLayoutParams = container.getLayoutParams();
                    containerLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    containerLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    container.setLayoutParams( containerLayoutParams );

                    btnCloseWebview.setVisibility(View.VISIBLE);
                }
            });
        }

        @JavascriptInterface
        public void hideWebView () {
            // Log.d( "JSInterface", "hideWebView" );
            context.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams containerLayoutParams = container.getLayoutParams();
                    containerLayoutParams.width = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics() );
                    containerLayoutParams.height = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics() );
                    container.setLayoutParams( containerLayoutParams );

                    btnCloseWebview.setVisibility(View.GONE);
                }
            });
        }
    }
}
