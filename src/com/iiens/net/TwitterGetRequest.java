package com.iiens.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/** TwitterGetRequest 
	Classe permettant de récupérer les tweets à partir de l'API Twitter
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
	Modifications par : --
 **/

// Uses an AsyncTask to download a Twitter user's timeline
public class TwitterGetRequest extends AsyncTask<Void, Void, ArrayList<Tweet>> {

	/* Identifiants à récupérer sur dev.twitter.com pour pouvoir récupérer les tweets */
	private String OAuthConsumerKey = "*****";
	private String OAuthConsumerSecret = "*****";
	private String OAuthAccessToken = "*****";
	private String OAuthAccessTokenSecret= "*****";

	private String queryStr = new String();
	private int count = 0;

	public TwitterGetRequest(String query, int count) {
		this.queryStr = query;
		this.count = count;
	}

	@Override
	protected ArrayList<Tweet> doInBackground(Void ... voids) {
		ArrayList<Tweet> result = new ArrayList<Tweet>();

		result = getTweets();

		return result;
	}

	public ArrayList<Tweet> getTweets() {
		ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(OAuthConsumerKey)
		.setOAuthConsumerSecret(OAuthConsumerSecret)
		.setOAuthAccessToken(OAuthAccessToken)
		.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);

		Twitter twitter = new TwitterFactory(cb.build()).getInstance();

		Query query = new Query(queryStr);
		query.setCount(count);
		query.setResultType(Query.RECENT);
		query.setSince("2014-09-01");

		try {
			List<twitter4j.Status> statusList = new ArrayList<twitter4j.Status>();
			QueryResult result;
			do {
				result = twitter.search(query);
				statusList.addAll(result.getTweets());
			} while ((query = result.nextQuery()) != null);

			for (int i = 0; i< statusList.size(); i++) {
				Tweet tweet = new Tweet();
				twitter4j.Status statusItem = statusList.get(i);

				tweet.setDateCreated(statusItem.getCreatedAt().toString());
				tweet.setId(String.valueOf(statusItem.getId()));
				tweet.setInReplyToStatusId(String.valueOf(statusItem.getInReplyToStatusId()));
				tweet.setInReplyToUserId(String.valueOf(statusItem.getInReplyToUserId()));
				tweet.setInReplyToScreenName(statusItem.getInReplyToScreenName());
				tweet.setText(statusItem.getText());

				TwitterUser user = new TwitterUser();

				user.setName(statusItem.getUser().getName());
				// user.setProfileImageUrl(getBitmap(statusItem.getUser().getProfileImageURL()));
				user.setScreenName(statusItem.getUser().getScreenName());
				tweet.setUser(user);

				tweetList.add(tweet);

				//				Log.d("user name : ", statusList.get(i).getUser().getName());
				//				Log.d("text : ", statusList.get(i).getText());
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return tweetList;
	}

	public Bitmap getBitmap(String bitmapUrl) {
		try {
			URL url = new URL(bitmapUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			return null;
		}
	}

}
