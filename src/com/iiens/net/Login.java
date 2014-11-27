package com.iiens.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** Login
 * 	Permet de "se connecter" aux serveurs d'Arise / de vérifier ses identifiants
 *  Auteur : Srivatsan 'Loki' Magadevane (Promo 2014)
 */

public class Login extends Activity {
	
	// App parameters
	private String connectURL = "https://www.iiens.net"; // Address of the site making where we authenticate

	// UI references.
	private EditText mUsernameView, mPasswordView;
	private View mLoginFormView, mLoginStatusView;
	private TextView mLoginStatus;
	private CheckBox mCheckBoxView;
	private Button mConnectButton;
	
	// Other
	private UserLoginTask mAuthTask = null;
	private String mUsername = "";
	private String mPassword = "";
	private String mNom = "";
	private Boolean isConnected = false;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Loading the layout
		setContentView(R.layout.activity_login);

		// Initializing the UI and other parameters
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mCheckBoxView = (CheckBox) findViewById(R.id.auto_connect);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatus = (TextView) findViewById(R.id.login_status_message);
		mConnectButton = (Button) findViewById(R.id.connect_button);
		preferences = this.getPreferences(Context.MODE_PRIVATE);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		// If automatic log in is enabled, try to authenticate
		if (preferences.getBoolean("remember_login", false) == true) {
			mUsernameView.setText(preferences.getString("login", ""));
			mPasswordView.setText(preferences.getString("password", ""));
			mCheckBoxView.setChecked(true);
			attemptLogin();
		} 		
		else {
			// Set the actions of the connection button 
			mConnectButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// If asked, saves the credentials in the preferences of the app
					if (mCheckBoxView.isChecked()) {
						editor = preferences.edit();
						editor.putBoolean("remember_login", true);
						editor.putString("login", mUsernameView.getText().toString());
						editor.putString("password", mPasswordView.getText().toString());
						editor.commit();
					}

					// Try to authenticate
					attemptLogin();
				}	
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// Animation making the user wait for the authentication
	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	// Verifies that the app has internet access
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {				
			return connectToArise();
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				mLoginStatus.setText(R.string.authenticated);
				Intent i = new Intent(Login.this, Main.class);
				i.putExtra("login", mUsername);
				i.putExtra("pass", mPassword);
				i.putExtra("nom", mNom);
				i.putExtra("isConnected", isConnected);
				startActivity(i);
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
				finish();
			} else {
				showProgress(false);
				mUsernameView.setError(getString(R.string.error_incorrect));
				mUsernameView.requestFocus();
				mPasswordView.setError(getString(R.string.error_incorrect));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
			this.cancel(true);
		}

	}

	/* Verifies the inputs are correctly provided */
	public void attemptLogin() {
		// If already logged in
		if (mAuthTask != null) {
			return;
		}

		// If no access to internet
		if (!isOnline()) {
			Toast.makeText(getApplicationContext(), "Internet marche pas, vas taper Arise", Toast.LENGTH_LONG).show();
			return;
		}

		// Reset the errors previously shown
		mUsernameView.setError(null);
		mPasswordView.setError(null);
		boolean cancel = false;
		View focusView = null;

		// Verify the parameters are not empty
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		// If problem, cancel authentication else try to connect
		if (cancel) {
			focusView.requestFocus();
		} else {
			// affichage animation et tâche de connexion
			mLoginStatus.setText(R.string.in_progress);
			showProgress(true);

			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	public boolean connectToArise() {

		DefaultHttpClient httpclient = null;
		CookieStore cookieStore = new BasicCookieStore(); 

		InputStream is = null;
		String result = "";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", mUsername));
		nameValuePairs.add(new BasicNameValuePair("pass", mPassword));
		nameValuePairs.add(new BasicNameValuePair("slp", "Connexion"));

		// Envoi de la commande http
		try {
			SchemeRegistry schemeRegistry = new SSLArise().init(this);

			HttpParams params = new BasicHttpParams();
			ClientConnectionManager cm = 
					new ThreadSafeClientConnManager(params, schemeRegistry);

			httpclient = new DefaultHttpClient(cm, params);
			httpclient.setCookieStore(cookieStore);
			HttpPost httppost = new HttpPost(connectURL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log.e("login", "Error in http connection " + e.toString());
		}

		// Conversion de la requête en string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Log.e("login", "Error converting result " + e.toString());
		}
		
		Document doc = Jsoup.parse(result);
		String element = doc.select("div.connect").first().text();
		Log.d("element", element);
		
		httpclient.getConnectionManager().shutdown();
		
		if (element.contains("Mauvais login")) {
			return false;
		}
		
		mNom = element;
		isConnected = true;
		
		return true;

	}


}
