package com.iiens.net.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Classe permettant de gérer la création et la mise à jour des tokens
 * source https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseInstanceIDService.java
 */
public class FbInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onCreate () {

        super.onCreate();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh () {

        Intent intent = new Intent( "tokenRefreshed" );

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance( this );
        broadcastManager.sendBroadcast( intent );
    }
    // [END refresh_token]
}
