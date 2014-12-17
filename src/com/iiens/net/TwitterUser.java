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

/** TwitterUser
	Classe permettant stocker les infos concernant l'auteur d'un tweet
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TwitterUser {

	//	@SerializedName("screen_name")
	private String screenName;

	//	@SerializedName("name")
	private String name;

	//	@SerializedName("profile_image_url")
	private String profileImageUrl;
	
	private Bitmap profileImage;

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public Bitmap getProfileImage(){
		return profileImage;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	
	public void setProfileImage(String profileImageUrl) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			URI imageURI = new URI(profileImageUrl);
			response = httpclient.execute(new HttpGet(imageURI));
			Bitmap imageBitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
			this.profileImage = imageBitmap;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}