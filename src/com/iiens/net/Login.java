package com.iiens.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Login extends Activity {

	private CookieStore cookieStore;
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUsername = "magadeva2011";
	private String mPassword = "jop";

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private CheckBox mCheckBoxView;
	private Button mConnectionButton;

//	private String cookie_str;
//	private Cookie cookie_intent;

	private String txtURL = "http://www.iiens.net";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Définition de l'interface graphique et des accesseurs
		setContentView(R.layout.activity_login);

		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mCheckBoxView = (CheckBox) findViewById(R.id.remember_login);
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mConnectionButton = (Button) findViewById(R.id.connexion_button);

		SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = preferences.edit();

		// If automatic log in
		if (preferences.getBoolean("remember_login", false) == true) {
			mUsernameView.setText(preferences.getString("login", ""));
			mPasswordView.setText(preferences.getString("password", ""));
			mCheckBoxView.setChecked(true);
			attemptLogin();
		} 		
		else {
			// Set the actions of the connection button 
			mConnectionButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					// If asked, saves the credentials in the preferences of the app
					if (mCheckBoxView.isChecked()) {
						editor.putBoolean("remember_login", true);
						editor.putString("login", "magadeva2011");
						editor.putString("password", "jop");
						editor.commit();
					}

					attemptLogin();
				}	
			});
		}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// Vérification de la saisie et lancement de la procédure de connexion
	public void attemptLogin() {
		// If already logged in
		if (mAuthTask != null) {
			return;
		}

		// If no access to internet
		if (!isOnline()) {Toast.makeText(getApplicationContext(), "Internet marche pas, vas taper Arise", Toast.LENGTH_LONG).show();}

		// Remise à zéro des erreurs
		mUsernameView.setError(null); // Si symbole erreur saisie est affiché, il est effacé
		mPasswordView.setError(null); // idem
		boolean cancel = false;
		View focusView = null;

		// Récupération des identifiants à partir du formulaire
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		// Vérification de la saisie d'un mot de passe
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Vérification de la saisie du login ARISE
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		// Action si les champs sont mal / bien saisis
		if (cancel) {
			focusView.requestFocus();
		} else {
			// affichage animation et tâche de connexion
			mLoginStatusMessageView.setText(R.string.anim_connexion);
			showProgress(true);

			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	// Animation qui apparait en premier plan lors de la connexion
	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	public Boolean httpRequest() {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		cookieStore = new BasicCookieStore(); 
		httpclient.setCookieStore(cookieStore);

		try {
			// Construction de la requête HTTP

			HttpPost requete = new HttpPost(txtURL);

			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("login", mUsername));
			nvps.add(new BasicNameValuePair("pass", mPassword));

			requete.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(requete);
			HttpEntity entity = response.getEntity();

			//            System.out.println("Login form get: " + response.getStatusLine());
			if (entity != null) {
				entity.consumeContent();
			}

			//            System.out.println("Post logon cookies:");
			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			//            if (cookies.isEmpty()) {
			//                System.out.println("None");
			//            } else {
			//                for (int i = 0; i < cookies.size(); i++) {
			//                    System.out.println("- " + cookies.get(i).toString());
			//                }
			//            }

			for(Cookie cookie : cookies){
				cookieStore.addCookie(cookie);
			}

			// When HttpClient instance is no longer needed, 
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();        

			return true;
		} catch (Exception e)
		{
			return false;
		}

	}

	/**
	 * Tâche asynchrone d'authentification de l'utilisateur
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		// Envoi de la requête HTTP en tâche de fond
		protected Boolean doInBackground(Void... params) {				
			httpRequest();
			return true;
		}

		@Override
		// Après réception de la réponse, transition vers Main avec les données récupérées
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				Log.d("*** DEBUG", ": Succès identification ***");
				Intent i = new Intent(Login.this, Main.class);
				i.putExtra("login", mUsername);
				i.putExtra("pass", mPassword);
				//				cookie_intent = cookieStore.getCookies().get(0);
				//				cookie_str = 
				//						"Version=" + cookie_intent.getVersion() + ";"
				//								+ "Name=" + cookie_intent.getName() + ";"
				//								+ "Value=" + cookie_intent.getValue() + ";"
				//								+ "Domain=" + cookie_intent.getDomain() + ";"
				//								+ "Path=" + cookie_intent.getPath() + ";"
				//								+ "Expiry="+ cookie_intent.getExpiryDate() + ";"
				//								;
				//				i.putExtra("cookie", cookie_str);
				startActivity(i);
				overridePendingTransition(R.anim.bottom_in, R.anim.top_out);

				finish();
			} else {
				Log.d("*** DEBUG", ": échec identification ***");
				mPasswordView
				.setError(getString(R.string.error_incorrect));
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
}
