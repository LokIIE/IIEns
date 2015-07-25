package com.iiens.net.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iiens.net.R;
import com.iiens.net.SplashScreen;

public class GCMIntentService extends IntentService {
    private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;
    private SharedPreferences preferences;
    private PendingIntent contentIntent;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            Log.i(TAG, "Received something ...");
            switch (messageType) {
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR:
                    Log.d("Send error: ", extras.toString());
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED:
                    Log.d("Deleted msg on server: ", extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    // Post notification of received message.
                    mNotificationManager = (NotificationManager)
                            this.getSystemService(Context.NOTIFICATION_SERVICE);
                    preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    contentIntent = PendingIntent.getActivity(this, 0,
                            new Intent(this, SplashScreen.class), 0);
                    String type_msg = extras.getString("type_msg");
                    Log.i(TAG, "Received: " + extras.getString("msg") + ", type : " + type_msg + ", wanted : " + preferences.getString(getResources().getString(R.string.pref_partiel_key), "none"));
                    switch (type_msg) {
                        case "news":
                            if (preferences.getBoolean(getResources().getString(R.string.pref_news_key), false)) {
                                showNewsNotification(extras.getString("msg"));
                            }
                            break;
                        case "partiel":
                            if (preferences.getString(getResources().getString(R.string.pref_partiel_key), "none").equals(extras.getString("promo"))) {
                                showPartielNotification(extras.getString("msg"));
                            }
                            break;
                        case "anniv":
                            if (preferences.getBoolean(getResources().getString(R.string.pref_anniv_key), false)) {
                                showAnnivNotification(extras.getString("msg"));
                            }
                            break;
                        case "club":
                            if (preferences.getBoolean(getResources().getString(R.string.pref_events_key), false)) {
                                showClubNotification(extras.getString("msg"));
                            }
                            break;
                        default:
                            break;
                    }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void showNewsNotification(String msg) {
        int notification_id = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);

        preferences.edit().putBoolean(getResources().getString(R.string.bool_news_update_name), true).apply();
        mBuilder.setContentTitle(getResources().getString(R.string.notif_news_title));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    private void showPartielNotification(String msg) {
        int notification_id = 2;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true) // suppress the notification on click
                        .setContentText(msg);

        mBuilder.setContentTitle(getResources().getString(R.string.notif_partiels_title));
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    private void showAnnivNotification(String msg) {
        int notification_id = 3;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        msg = msg.substring(1, msg.length() - 1);
        String[] people = msg.split(",");
        for (String person : people) {
            inboxStyle.addLine(person.substring(1, person.length() - 1));
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.notif_anniv_title) + "(" + people.length + ")")
                        .setStyle(inboxStyle)
                        .setAutoCancel(true) // suppress the notification on click
                        .setContentText(msg);

        preferences.edit().putBoolean(getResources().getString(R.string.bool_anniv_update_name), true).apply();

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

    private void showClubNotification(String msg) {
        int notification_id = 4;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        msg = msg.substring(1, msg.length() - 1);
        String[] events = msg.split("\",");
        for (String event : events) {
            inboxStyle.addLine(event.substring(1, event.length()));
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.notif_events_title) + "(" + events.length + ")")
                        .setStyle(inboxStyle)
                        .setAutoCancel(true) // suppress the notification on click
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }

}