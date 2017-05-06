package com.iiens.net;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Itvtube extends Activity {

    RequestQueue queue;
    WebView wv = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.arise_login);

        wv = (WebView) findViewById(R.id.arise_webview);
        WebChromeClient wcClient = new WebChromeClient();

        wv.setWebChromeClient(wcClient);

        WebSettings wv_settings = wv.getSettings();
        wv_settings.setJavaScriptEnabled(true);
        wv_settings.setBuiltInZoomControls(true);
        wv_settings.setDisplayZoomControls(false);

/*        wv.setWebViewClient( new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadVideosList();
            }
        });*/

        if (savedInstanceState == null) {
            wv.loadUrl("https://itvtube.iiens.net");
        }
    }

    public void loadVideosList() {

        setContentView(R.layout.itvtube);

        GridView gridview = (GridView) findViewById(R.id.itvtube_gridview);
        gridview.setAdapter(new ItvtubeAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(Itvtube.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        queue = Volley.newRequestQueue(this, new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpURLConnection connection = super.createConnection(url);
                connection.setInstanceFollowRedirects(true);

                return connection;
            }
        });

        queue.start();

        JsonObjectRequest videosListReq = new JsonObjectRequest( "https://itvtube.iiens.net/videos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d( "RES1", response.toString() );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });

        videosListReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add( videosListReq );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        wv.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        wv.restoreState(savedInstanceState);
    }
}
