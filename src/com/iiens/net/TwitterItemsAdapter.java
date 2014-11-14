package com.iiens.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/** TwitterItemsAdapter 
	Classe permettant d'afficher les tweets récupérés par TwitterGetRequest
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
	Modifications par : --
 **/

public class TwitterItemsAdapter extends ArrayAdapter<Tweet> {
	private ArrayList<Tweet> tweets;
	private Context context;

	public TwitterItemsAdapter(Context context, int textViewResourceId, ArrayList<Tweet> tweets) {
		super(context, textViewResourceId, tweets);
		this.context = context;
		this.tweets = tweets;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi =	(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.twitter_item, parent, false);
		}

		Tweet tweet = tweets.get(position);
		if (tweet != null) {
			TextView username = (TextView) v.findViewById(R.id.username);
			TextView message = (TextView) v.findViewById(R.id.message);
			TextView account = (TextView) v.findViewById(R.id.useraccount);
			TextView time = (TextView) v.findViewById(R.id.publishtime);
			//			ImageView image = (ImageView) v.findViewById(R.id.avatar);

			if (username != null) {
				username.setText(tweet.getUser().getName());
				//				Log.d("Username : ", username.getText().toString());
			}

			if(message != null) {
				message.setText(tweet.getText());
				//				Log.d("Text : ", message.getText().toString());
			}

			if(account != null) {
				account.setText("@" + tweet.getUser().getScreenName());
				//				Log.d("Text : ", message.getText().toString());
			}

			if(time != null) {
				String dateCreated = tweet.getDateCreated();

				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					String now = sdf.format(new Date());

					SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
					dateCreated = sdf.format(sdf2.parse(dateCreated));

					Date datePublished = sdf.parse(dateCreated);
					Date dateNow = sdf.parse(now);

					time.setText(printDifference(datePublished, dateNow));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				//				Log.d("Text : ", message.getText().toString());
			}

			// Affichage des images annulé pour le moment car temps de chargement trop long
			//			if(image != null) {
			//				image.setImageBitmap(tweet.getUser().getProfileImageUrl());
			//			}
		}

		return v;
	}

	//1 minute = 60 seconds
	//1 hour = 60 x 60 = 3600
	//1 day = 3600 x 24 = 86400
	public String printDifference(Date startDate, Date endDate){
		String result = new String();

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		//		System.out.println("startDate : " + startDate);
		//		System.out.println("endDate : "+ endDate);
		//		System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		//		System.out.printf(
		//				"%d days, %d hours, %d minutes, %d seconds%n", 
		//				elapsedDays,
		//				elapsedHours, elapsedMinutes, elapsedSeconds);


		if (elapsedDays > 0) {
			result = String.valueOf(elapsedDays) + "d";
		} else if (elapsedHours > 0) {
			result = String.valueOf(elapsedHours) + "h";
		} else if (elapsedMinutes > 0) {
			result = String.valueOf(elapsedMinutes) + "m";
		} else {
			result = String.valueOf(elapsedSeconds) + "s";
		}

		return result + " ago";

	}
}