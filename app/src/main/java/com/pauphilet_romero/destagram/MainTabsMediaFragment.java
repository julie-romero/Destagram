package com.pauphilet_romero.destagram;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

//import com.pauphilet_romero.destagram.utils.MultipartEntity;


/**
 * Onglet "Ajout de média"
 */
public class MainTabsMediaFragment extends Fragment {
    private Button mTakePhoto;
    private Button mUpload;
    private ImageView mImageView;
    private Bitmap fullsizePhoto;
    private static final String TAG = "upload";
    private String token = "";
    static final int REQUEST_TAKE_PHOTO = 1;
    File fullSizeFile = null;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Intent intent = getActivity().getIntent();
        token = intent.getStringExtra("token");
        rootView = inflater.inflate(R.layout.fragment_main_tabs_media, container, false);
        EditText edit_titre = (EditText) rootView.findViewById(R.id.titre);
        EditText edit_desc = (EditText) rootView.findViewById(R.id.description);
        edit_titre.setText(new String(""));
        edit_desc.setText(new String(""));
        mTakePhoto = (Button) rootView.findViewById(R.id.take_photo);
        mImageView = (ImageView) rootView.findViewById(R.id.imageview);
        mUpload = (Button) rootView.findViewById(R.id.upload);
        mTakePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.take_photo:
                        takePhoto();
                        break;
                }
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendPhoto();
            }

        });
        return rootView;
    }


    private void takePhoto() {
		//Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		//intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	    //this.getActivity().startActivityForResult(intent, 0);
        dispatchTakePictureIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onActivityResult: " + this);
        MainTabsActivity activity = (MainTabsActivity)getActivity();
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                setFullImageFromFilePath(activity.getmCurrentPhotoFullSizePath(), mImageView);
                compressFile(fullsizePhoto);
            } else {
                Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                        .show();
            }

    }
    private void compressFile(Bitmap bitmap)
    {
        try{
            OutputStream out = null;
            out = new FileOutputStream(fullSizeFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,out);
            out.flush();
            out.close();
        }catch(Exception e){
            Log.e("Dak","Erreur compress : "+e.toString());
        }
    }
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        fullsizePhoto = BitmapFactory.decodeFile(imagePath, bmOptions);

        imageView.setImageBitmap(fullsizePhoto);
    }

    private void sendPhoto() {
        new DownloadImageTask().execute(token);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    private void dispatchTakePictureIntent() {
        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        MainTabsActivity activity = (MainTabsActivity)getActivity();
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            try {
                fullSizeFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(activity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (fullSizeFile != null) {
                // Save a file: path for use with ACTION_VIEW intents
                Log.i("path", "path : " + Uri.fromFile(fullSizeFile).getPath().toString());
                activity.setmCurrentPhotoFullSizePath(Uri.fromFile(fullSizeFile).getPath().toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(fullSizeFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
           }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }


    private class DownloadImageTask extends AsyncTask {

        private String token;
        @Override
        protected Object doInBackground(Object[] params) {
            token = params[0].toString();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            MainTabsActivity activity = (MainTabsActivity)getActivity();
            try {
                Log.i(TAG, "token : " + token);
                EditText edit_titre = (EditText) rootView.findViewById(R.id.titre);
                EditText edit_desc = (EditText) rootView.findViewById(R.id.description);
                HttpPost httppost = new HttpPost("http://destagram.zz.mu/upload.php"); // server
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                reqEntity.addPart("file",
                        new FileBody(fullSizeFile));

                try {
                    reqEntity.addPart("token",
                            new StringBody(token));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                };

                try {
                    reqEntity.addPart("titre",new StringBody(edit_titre.getText().toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    reqEntity.addPart("description",
                            new StringBody(edit_desc.getText().toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                httppost.setEntity(reqEntity);
                Log.i(TAG, "request " + httppost.getRequestLine());
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    String json = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "response : " + json);
                    Intent intent = new Intent(getActivity(), MainTabsActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i(TAG, "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            token = token;
        }

    }
}
