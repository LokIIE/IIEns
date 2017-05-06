package com.iiens.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {

    RequestQueue queue;
    EditText form_login;
    EditText form_password;
    AriseWebViewClient wv;
    JsonObjectRequest uri_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        form_login = (EditText) findViewById(R.id.username);
        form_password = (EditText) findViewById(R.id.password);

        wv = new AriseWebViewClient( this,
                new AriseWebViewClient.AriseInterface () {
                    public void onConnectionSuccess () {
                        Intent i = new Intent(Login.this, Itvtube.class);
                        startActivity(i);
                    }
        }) {
            @Override
            public String getJsScript() {
                return "javascript:( function () {" +
                    "window.HtmlViewer.hideWebView();" +
                    "var credential = document.getElementsByClassName('credential');" +
                    "var autorize = document.getElementsByClassName('well');" +
                    "var login = document.getElementById('login');" +
                    "var pass = document.getElementById('password');" +
                    "if( credential.length > 0 ) {" +
                        "credential = credential[0].innerHTML;" +
                        "window.HtmlViewer.autoSendVerifier( credential );" +
                    "} else if( autorize.length > 0 ) {" +
                        "window.HtmlViewer.expandWebView();" +
                    "} else if( login && pass ) {" +
                        "login.value = '" + form_login.getText().toString() + "';" +
                        "pass.value = '" + form_password.getText().toString() + "';" +
                        "submit_btn = document.getElementsByClassName('btn-success')[0];" +
                        "submit_btn.click();" +
                    "}" +
                "})();";
            }
        };

        // Request a string response from the provided URL.
        uri_request = new JsonObjectRequest( getResources().getString(R.string.apiie_login_arise), new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if( response.has("connection_status") && response.getString("connection_status").equals("ok") ) {

                            } else {

                                wv.loadUrl( response.getString("redirect") );
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),
                                    "Error1: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Error4: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
        );

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Button connect_button = (Button) findViewById(R.id.connect_button);
        connect_button.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                queue.add( uri_request );
                queue.start();
            }
        });
    }

//    @Override
//    public void onDestroy() {
//        try {
//            RequestQueue queue = Volley.newRequestQueue( getApplicationContext() );
//            JSONObject postData = new JSONObject();
//
//            postData.put( "logout", "logout" );
//            Log.d( "data" , postData.toString() );
//
//            JsonObjectRequest destroyClient = new JsonObjectRequest( getResources().getString(R.string.apiie_login_arise), postData,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d( "DESTROY", response.toString() );
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            });
//
//            queue.add( destroyClient );
//
//        } catch ( Exception e ) {
//            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        super.onDestroy();
//    }
}