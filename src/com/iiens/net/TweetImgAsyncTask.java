package com.iiens.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/** TwitterImgAsyncTask 
	Classe permettant de récupérer les images de profil des auteurs des tweets
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TweetImgAsyncTask extends AsyncTask<Void, Void, Bitmap> {

	private String imgURL;

	public TweetImgAsyncTask(String imgURL){
		this.imgURL = imgURL;
	}

	@Override
	protected Bitmap doInBackground(Void ... voids) {
		Bitmap result = getTweetImg(imgURL);

		return result;
	}

	public static Bitmap getTweetImg(String imgURL) {
		Bitmap imageBitmap = null;

		try	{
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = null;
			try {
				URI imageURI = new URI(imgURL);
				response = httpclient.execute(new HttpGet(imageURI));
				imageBitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Log.e("twitter_img_get", "Error in http connection " + e.toString());
		}

		return imageBitmap;
	}

}
