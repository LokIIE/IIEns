package com.iiens.net;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/** SplashScreen 
	Animation lors du lancement de l'appli
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 2000; // Splash screen timer
	private boolean backPressed = false; // Was back button pressed ?
	private SharedPreferences preferences;

	/////////////////////////////////////
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "GCMRelated";
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	String regid;
	//////////////////////////////////////

	/* Determines the view to load for this activity */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		////////////////////////////
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			regid = getRegistrationId(getApplicationContext());

			if (regid.isEmpty()) {
				new GCMRegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
			}else{
				Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		///////////////////////////////
	}

	/* Actions when the view is displayed on the screen */
	@Override
	protected void onStart() {
		super.onStart();
		StartAnimation();

		// Execute the actions after a certain delay
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = null;
				if (preferences.getBoolean("login_option", false)) {
					i = new Intent(SplashScreen.this, Login.class);
				} else {
					i = new Intent(SplashScreen.this, Main.class);
				}

				// Start main activity when timer is over or when off-line mode enabled and end splash screen
				if (isOnline() || preferences.getBoolean("storage_option", false)) {  
					if (!backPressed) {
						startActivity(i);
						overridePendingTransition(R.anim.right_in, R.anim.left_out);
					}
				} else {
					Toast.makeText(getApplicationContext(), "Connexion Internet requise", Toast.LENGTH_LONG).show();
				}
				finish();
			}
		}, SPLASH_TIME_OUT);

	}

	/* Action when the back button is toggled */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		backPressed = true;
		finish();  
	}

	/* Animation for the splash screen logo */
	private void StartAnimation() { 
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.top_in);
		ImageView iv = (ImageView) findViewById(R.id.imgLogo);
		iv.startAnimation(anim);
	}

	/* Verifies that the app has internet access */
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	////////////////////////////////

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(getApplicationContext());
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(SplashScreen.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
}