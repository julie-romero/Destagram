package com.pauphilet_romero.destagram.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Permet de charger une image récupérée sur Internet de manière asynchrone
 * Created by Jimmy on 16/12/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
   /* private static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    private static int cacheSize = maxMemory / 8;
    private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };*/
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
        Log.i("download image", "Downloading ...:"+urldisplay);
        Bitmap mIcon11 = null;
        try {
            BitmapFactory.Options bfOptions=new BitmapFactory.Options();
            bfOptions.inJustDecodeBounds = false;
            bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bfOptions.inDither=false;                     //Disable Dithering mode
            bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            bfOptions.inSampleSize = 2;
            bfOptions.inTempStorage=new byte[32 * 1024];
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in, null, bfOptions);
            in.close();
            Log.i("download image", "downloaded ! : " + urldisplay);
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
