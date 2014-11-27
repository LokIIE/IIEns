package com.iiens.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

/** SplashScreen 
	Animation lors du lancement de l'appli
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 2000; // Splash screen timer
	private boolean backPressed = false; // Was back button pressed ?

	/* Determines the view to load for this activity */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
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
				Intent i = new Intent(SplashScreen.this, Login.class);
				if (isOnline()) { /* Start main activity when timer is over and end splash screen */
					if (!backPressed) {
						startActivity(i);
						overridePendingTransition(R.anim.right_in, R.anim.left_out);
					}
				} else {
					Toast.makeText(getApplicationContext(), "Connection Internet requise", Toast.LENGTH_LONG).show();
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

}