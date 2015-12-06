package com.iiens.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.iiens.net.gcm.GCMMain;

/**
 * SplashScreen
 * Animation lors du lancement de l'appli et création d'un compte sur GCM pour les notifications
 */

public class SplashScreen extends Activity {

    private static final int SPLASH_TIME_OUT = 2000; // Splash screen duration

    /* Initialize the variables needed for this activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    /* Actions when the view is displayed on the screen */
    @Override
    protected void onStart() {
        super.onStart();

        // Verify if a gcm registration id exists, if not generates it
        new GCMMain(getApplicationContext(), this).checkGcm();

        // Go to main activity after the duration of the logo animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Main.class);

                startActivity(i);

                // Destroy this activity (no longer needed)
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}