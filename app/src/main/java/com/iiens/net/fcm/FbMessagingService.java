package com.iiens.net.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Classe permettant la réception de notification dans les applications en premier plan, des payload de données et d'envoyer des messages
 source https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MyFirebaseMessagingService.java
 */
public class FbMessagingService extends FirebaseMessagingService{
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
/*        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
    }

    /*    @Override
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
    }*/
}
