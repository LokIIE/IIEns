package com.iiens.net;

/**
 * TwitterImgAsyncTask
 * Classe permettant de récupérer les images de profil des auteurs des tweets
 */

//public class TweetImgAsyncTask extends AsyncTask<Void, Void, Bitmap> {

//    private final String imgURL;
//
//    public TweetImgAsyncTask(String imgURL) {
//        this.imgURL = imgURL;
//    }
//
//    private static Bitmap getTweetImg(String imgURL) {
//        Bitmap imageBitmap = null;
//
//        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpResponse response;
//            try {
//                URI imageURI = new URI(imgURL);
//                response = httpclient.execute(new HttpGet(imageURI));
//                imageBitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
//            } catch (URISyntaxException | ClientProtocolException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            Log.e("twitter_img_get", "Error in http connection " + e.toString());
//        }
//
//        return imageBitmap;
//    }
//
//    @Override
//    protected Bitmap doInBackground(Void... voids) {
//        return getTweetImg(imgURL);
//    }

//}
