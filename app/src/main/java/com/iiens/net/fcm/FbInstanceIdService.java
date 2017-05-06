package com.iiens.net.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iiens.net.SplashScreen;

/**
 * Classe permettant de gérer la création et la mise à jour des tokens
 * source https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseInstanceIDService.java
 */
public class FbInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private Context ctx;
    private int appVersion;

    public FbInstanceIdService() {}

    public FbInstanceIdService(Context ctx, int appVersion) {
        this.ctx = ctx;
        this.appVersion = appVersion;
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        final SharedPreferences prefs = ctx.getSharedPreferences(SplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", refreshedToken);
        editor.putInt("appVersion", appVersion);
        editor.apply();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Toast.makeText(this.getApplicationContext(), token, Toast.LENGTH_SHORT).show();
/*        URL url;
        try {
            url = new URL(ctx.getResources().getString(R.string.url_fcm_api) + regid);
            HttpURLConnection httpclient = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
