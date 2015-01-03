package com.pauphilet_romero.destagram.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Permet de charger une image récupérée sur Internet de manière asynchrone
 * Created by Jimmy on 16/12/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    /***
     * Constructeur
     * @param bmImage
     */
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    /***
     * Récupération de l'image
     * @param urls
     * @return
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Log.i("url",urldisplay);
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    /***
     * Attribution de l'image à l'ImageView
     * @param result
     */
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
