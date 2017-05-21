package com.iiens.net.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Classe permettant de gérer la création et la mise à jour des tokens
 */
public class FbInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onCreate () {

        super.onCreate();
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
