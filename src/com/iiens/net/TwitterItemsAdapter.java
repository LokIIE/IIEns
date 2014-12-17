package com.iiens.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** TwitterItemsAdapter 
	Classe permettant d'adapter les tweets récupérés pour l'affichage
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
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
			ImageView avatar = (ImageView) v.findViewById(R.id.avatar);
			TextView username = (TextView) v.findViewById(R.id.username);
			TextView message = (TextView) v.findViewById(R.id.message);
			TextView account = (TextView) v.findViewById(R.id.useraccount);
			TextView time = (TextView) v.findViewById(R.id.publishtime);

			String imgURL = tweet.getUser().getProfileImageUrl();
			String imgName = imgURL.replace("http://pbs.twimg.com/profile_images/", "").split("/")[0];

			Bitmap profileImg = loadImgFromStorage(imgName);

			if (profileImg != null) {
				avatar.setImageBitmap(profileImg);
			} else if (isOnline()){
				try {
					profileImg = new TweetImgAsyncTask(imgURL).execute().get();
					saveToInternalStorage(profileImg, imgName);
					if (profileImg != null) avatar.setImageBitmap(profileImg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.notfound));

			if (username != null) {
				username.setText(tweet.getUser().getName());
			}

			if (message != null) {
				String tweetTxt = tweet.getText();
				message.setText(Html.fromHtml(tweetTxt));
			}

			if (account != null) {
				account.setText("@" + tweet.getUser().getScreenName());
			}

			if(time != null) {
				String dateCreated = tweet.getDateCreated();

				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);
					String now = sdf.format(new Date());

					SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
					dateCreated = sdf.format(sdf2.parse(dateCreated));

					Date datePublished = sdf.parse(dateCreated);
					Date dateNow = sdf.parse(now);

					time.setText(printDifference(datePublished, dateNow));
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

		return v;
	}

	// 1 minute = 60 seconds
	// 1 hour = 60 x 60 = 3600
	// 1 day = 3600 x 24 = 86400
	public String printDifference(Date startDate, Date endDate){
		String result = new String();

		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

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

		if (elapsedDays > 0) {
			result = String.valueOf(elapsedDays) + " j";
		} else if (elapsedHours > 0) {
			result = String.valueOf(elapsedHours) + " h";
		} else if (elapsedMinutes > 0) {
			result = String.valueOf(elapsedMinutes) + " m";
		} else {
			result = String.valueOf(elapsedSeconds) + " s";
		}

		return result;

	}

	/* Verifies that the app has internet access */
	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/* Save the profile images in local storage */
	private String saveToInternalStorage(Bitmap bitmapImage, String bitmapURL){
		ContextWrapper cw = new ContextWrapper(context);
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("tweetsProfileImg", Context.MODE_PRIVATE);
		// Create imageDir
		File imgFile=new File(directory, bitmapURL+".jpeg");

		FileOutputStream fos = null;
		try {           
			fos = new FileOutputStream(imgFile);

			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return directory.getAbsolutePath();
	}

	private Bitmap loadImgFromStorage(String bitmapURL){
		Bitmap result = null;
		ContextWrapper cw = new ContextWrapper(context);
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("tweetsProfileImg", Context.MODE_PRIVATE);
		// Create imageDir
		File imgFile=new File(directory, bitmapURL+".jpeg");

		if (imgFile.exists()){
			try {
				result = BitmapFactory.decodeStream(new FileInputStream(imgFile));
			} catch (FileNotFoundException e) {
			}
		}
		return result;
	}
}