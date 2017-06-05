package com.iiens.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page de connexion
 */
public class Login extends Activity {

    RequestQueue queue;
    AriseWebViewClient wv;
    JsonObjectRequest uri_request;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        wv = new AriseWebViewClient( this, new AriseWebViewClient.AriseInterface () {
                    public void onConnectionSuccess () {
                        startMainActivity();
                    }
        });

        wv.enableLoginMode(
                (EditText) findViewById( R.id.username ),
                (EditText) findViewById( R.id.password )
        );

        uri_request = new JsonObjectRequest( getResources().getString( R.string.url_apiie_login ),
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if( response.has( "connection_status" ) && response.getString( "connection_status" ).equals( "ok" ) ) {

                                ((GlobalState) getApplicationContext())
                                        .setOauthConnected( true )
                                        .setUserInfos( response );
                                startMainActivity();

                            } else wv.loadUrl( response.getString("redirect") );

                        } catch (JSONException e) {

                            Toast.makeText( getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG )
                                    .show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText( getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG )
                                .show();
                    }
                }
        );

        queue = Volley.newRequestQueue( this );
    }

    @Override
    public void onStart () {

        super.onStart();
        queue.start();
    }

    @Override
    public void onDestroy () {

        try {

            RequestQueue queue = Volley.newRequestQueue( getApplicationContext() );
            JSONObject postData = new JSONObject();
            postData.put( "logout", "logout" );

            JsonObjectRequest destroyClient = new JsonObjectRequest(
                    getResources().getString( R.string.url_apiie_login ),
                    postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse ( JSONObject response ) {
                            ((GlobalState) getApplicationContext()).setOauthConnected( false );
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse ( VolleyError error ) {

                            Toast.makeText( getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG ).show();
                        }
                    });

            queue.add( destroyClient );

        } catch ( Exception e ) {

            Toast.makeText( getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG ).show();
        }

        super.onDestroy();
    }

    public void onForgotPasswordClicked ( View v ) {

        wv.loadUrl( getResources().getString( R.string.url_oauth_forgot_password ) );
    }

    public void onOfflineClicked ( View v ) {

        startMainActivity();
    }

    public void onConnectClicked ( View v ) {

        queue.cancelAll( this );
        queue.add( uri_request );
    }

    private void startMainActivity () {

        startActivity( new Intent(Login.this, Main.class) );
        finish();
    }
}