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
     * Récupération de l'image en arrière-plan
     * @param urls
     * @return
     */
    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap icon = null;
        try {
            // Décode un fichier Image en tant que Bitmap redimensionné pour remplir la vue
            BitmapFactory.Options bfOptions=new BitmapFactory.Options();
            // Options de décodage
            bfOptions.inSampleSize = 2;
            bfOptions.inJustDecodeBounds = false;
            bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bfOptions.inDither = false;     // Désactive le Dithering mode
            bfOptions.inPurgeable = true;  // Permet de purger le Bitmap
            bfOptions.inInputShareable = true;
            bfOptions.inTempStorage = new byte[1024];
            InputStream in = new java.net.URL(urlDisplay).openStream();
            icon = BitmapFactory.decodeStream(in, null, bfOptions);
            in.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return icon;
    }

    /***
     * Attribution de l'image à l'ImageView
     * @param result
     */
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }

}
