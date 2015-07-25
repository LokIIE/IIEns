package com.iiens.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * TwitterImgAsyncTask
 * Classe permettant de récupérer les images de profil des auteurs des tweets
 */

public class TweetImgAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private final String imgURL;

    public TweetImgAsyncTask(String imgURL) {
        this.imgURL = imgURL;
    }

    private static Bitmap getTweetImg(String imgURL) {
        Bitmap imageBitmap = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            try {
                URI imageURI = new URI(imgURL);
                response = httpclient.execute(new HttpGet(imageURI));
                imageBitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
            } catch (URISyntaxException | ClientProtocolException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("twitter_img_get", "Error in http connection " + e.toString());
        }

        return imageBitmap;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return getTweetImg(imgURL);
    }

}
