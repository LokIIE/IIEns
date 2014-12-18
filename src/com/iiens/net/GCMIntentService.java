package com.iiens.net;

import java.util.Random;

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

public class GCMIntentService extends IntentService {
	private static final String TAG = "GcmIntentService";
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

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
			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.d("Send error: ", extras.toString());
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.d("Deleted messages on server: ", 
						extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {}

				// Post notification of received message.
				String type_msg = extras.getString("type_msg");
				if (type_msg.equals("news")) sendNewsNotification(extras.getString("msg"));
				else if (type_msg.startsWith("partiel")) sendPartielNotification(type_msg, extras.getString("msg"));
				else if (type_msg.equals("anniv")) sendAnnivNotification(extras.getString("msg"));
				else if (type_msg.equals("club")) sendClubNotification(extras.getString("auteur"), extras.getString("msg"));
				Log.i(TAG, "Received: " + extras.getString("msg"));
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNewsNotification(String msg) {
		boolean buildNotification = false;
		int notification_id = 1;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashScreen.class), 0);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setContentText(msg);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		preferences.edit().putBoolean("news_new_update", true).commit();
		if (preferences.getBoolean("news_notif", false)) {
			buildNotification = true;
			mBuilder.setContentTitle("News");
		}

		if (buildNotification){
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}
	}

	private void sendPartielNotification(String type_msg, String msg) {
		boolean buildNotification = false;
		int notification_id = 2;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashScreen.class), 0);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setAutoCancel(true) // suppress the notification on click
		.setContentText(msg);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getString("partiel_notif", "none").equals(type_msg)) {
			buildNotification = true;	
			mBuilder.setContentTitle("Partiel incoming");
		}
		if (buildNotification){
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}
	}

	private void sendAnnivNotification(String msg) {
		boolean buildNotification = true;
		int notification_id = 3;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashScreen.class), 0);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		msg = msg.substring(1, msg.length()-1);
		String[] people = msg.split(",");
		for (int i=0; i < people.length; i++) {
			inboxStyle.addLine(people[i].substring(1, people[i].length()-1));
		}

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Anniversaires du jour (" + people.length + ")")
		.setStyle(inboxStyle)
		.setAutoCancel(true) // suppress the notification on click
		.setContentText(msg);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		preferences.edit().putBoolean("anniv_new_update", true).commit();
		if (preferences.getBoolean("anniv_notif", false)) {
			buildNotification = true;
		}

		if (buildNotification){
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}
	}

	private void sendClubNotification(String auteur, String msg) {
		boolean buildNotification = false;
		int notification_id = 4;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SplashScreen.class), 0);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setContentText(msg);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getBoolean("club_notif", false)) {
			buildNotification = true;
			if (auteur.equals("aeiie")) { 
				mBuilder.setContentTitle("Event BdE"); 
			} else {
				Random r = new Random();
				notification_id = r.nextInt(42 - 5) + 5;
				mBuilder.setContentTitle(auteur);
			}
		}

		if (buildNotification){
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(notification_id, mBuilder.build());
		}
	}

}