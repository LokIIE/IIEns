package com.iiens.net.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * TwitterUser
 * Mod�le d'un utilisateur de Twitter
 */

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

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImageUrl) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        try {
            URI imageURI = new URI(profileImageUrl);
            response = httpclient.execute(new HttpGet(imageURI));
            this.profileImage = BitmapFactory.decodeStream(response.getEntity().getContent());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getScreenName() {
        return screenName;
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