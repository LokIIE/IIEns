package com.iiens.net.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iiens.net.R;
import com.iiens.net.SplashScreen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCMRegisterApp extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GCMRelated";
    private final Context ctx;
    private final int appVersion;
    private GoogleCloudMessaging gcm;
    private String regid = null;

    public GCMRegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion) {
        this.ctx = ctx;
        this.gcm = gcm;
        this.appVersion = appVersion;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        String msg;
        try {
            if (gcm == null) gcm = GoogleCloudMessaging.getInstance(ctx);
            regid = gcm.register(ctx.getResources().getString(R.string.gcm_id));
            msg = "Device registered, registration ID=" + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // Persist the regID - no need to register again.
            storeRegistrationId(ctx, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    private void storeRegistrationId(Context ctx, String regid) {
        final SharedPreferences prefs = ctx.getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", regid);
        editor.putInt("appVersion", appVersion);
        editor.apply();
    }


    private void sendRegistrationIdToBackend() {
        URL url;
        try {
            url = new URL(ctx.getResources().getString(R.string.url_gcm_register) + regid);
            HttpURLConnection httpclient = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Toast.makeText(ctx, "Registration Completed. Now you can see the notifications", Toast.LENGTH_SHORT).show();
        Log.v(TAG, result);
    }
}