package com.pauphilet_romero.destagram;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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

import com.pauphilet_romero.destagram.utils.MultipartEntity;


/**
 * Onglet "Ajout de média"
 */
public class MainTabsMediaFragment extends Fragment {
    private Button mTakePhoto;
    private Button mUpload;
    private ImageView mImageView;
    private Bitmap bitmapToSend;
    public static final String PATH = "path";
    private static final String TAG = "upload";
    String mCurrentPhotoPath;
    private String token = "";
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Intent intent = getActivity().getIntent();
        token = intent.getStringExtra("token");
        rootView = inflater.inflate(R.layout.fragment_main_tabs_media, container, false);
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
        Log.i(TAG, "data: " + data.getExtras().get("data"));
        bitmapToSend = (Bitmap) data.getExtras().get("data");
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(this.getActivity().getApplicationContext(), bitmapToSend);
        Log.d("upload" , "tempUri " + tempUri);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        /*if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            setPic();
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			if (bitmap != null) {
				mImageView.setImageBitmap(bitmap);
                Log.d("Bitmap to send", bitmap.toString());
                bitmapToSend = bitmap;
			}
        }*/
        setPic();
        MainTabsActivity activity = (MainTabsActivity)getActivity();
        activity.setmCurrentPhotoPath(getRealPathFromURI(tempUri));
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                // Show the full sized image.
                setFullImageFromFilePath(activity.getmCurrentPhotoPath(), mImageView);
            } else {
                Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                        .show();
            }

    }
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmapToSend = BitmapFactory.decodeFile(imagePath, bmOptions);
        //Log.i("upload",bitmapToSend.toString());
        imageView.setImageBitmap(bitmapToSend);
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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(activity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                Log.d("create file", "uri : " + fileUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getmCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        MainTabsActivity activity = (MainTabsActivity)getActivity();
        //activity.setmCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        //MainTabsActivity activity = (MainTabsActivity)getActivity();
        //mCurrentPhotoPath = activity.getmCurrentPhotoPath();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //Log.d("upload","path + "+bitmap.getWidth());
        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        // Rotating Bitmap
        Bitmap rotatedBMP = Bitmap.createBitmap(bitmapToSend, 0, 0, bitmapToSend.getWidth(), bitmapToSend.getHeight(), mtx, true);

        if (rotatedBMP != bitmapToSend)
            bitmapToSend.recycle();
        Log.d("bitmap to send", "bitmap : " + bitmapToSend.getWidth());
        mImageView.setImageBitmap(rotatedBMP);


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
   /* private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }*/
    private class DownloadImageTask extends AsyncTask {

        private String token;
        @Override
        protected Object doInBackground(Object[] params) {
            token = params[0].toString();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            String lineEnd = "rn";
            String twoHyphens = "--";
            String boundary =  "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;
            String responseFromServer = "";
            try {
                Log.i(TAG, "token : " + token);
                EditText edit_titre = (EditText) rootView.findViewById(R.id.titre);
                EditText edit_desc = (EditText) rootView.findViewById(R.id.description);
                //HttpPost httppost = new HttpPost("http://destagram.zz.mu/upload.php"); // server
                // Add your data
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("file", System.currentTimeMillis() + ".jpg"));
                nameValuePairs.add(new BasicNameValuePair("token", token));
                nameValuePairs.add(new BasicNameValuePair("titre", edit_titre.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("description", edit_desc.getText().toString()));
                MainTabsActivity activity = (MainTabsActivity)getActivity();
                Log.d("upload","PAth : " +activity.getmCurrentPhotoPath());
                //------------------ CLIENT REQUEST
                FileInputStream fileInputStream = new FileInputStream(new File(activity.getmCurrentPhotoPath()));
                // open a URL connection to the Servlet
                URL url = new URL("http://destagram.zz.mu/upload.php");
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                String fileName = System.currentTimeMillis() + ".jpg";
                conn.setRequestProperty("file", fileName);
                conn.setRequestProperty("token", token);

                conn.setRequestProperty("titre", edit_titre.getText().toString());
                conn.setRequestProperty("description", edit_desc.getText().toString());
                dos = new DataOutputStream( conn.getOutputStream() );
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+fileName+"\""  + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams
                Log.e("Debug","File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();
            }
            catch (MalformedURLException ex)
            {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }
            catch (IOException ioe)
            {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }
            //------------------ read the SERVER RESPONSE
            try {
                inStream = new DataInputStream( conn.getInputStream() );
                String str;

                while (( str = inStream.readLine()) != null)
                {
                    Log.e("Debug","Server Response "+str);
                }
                inStream.close();

            }
            catch (IOException ioex){
                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
            }
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(bitmapToSend!=null)
                bitmapToSend.compress(Bitmap.CompressFormat.JPEG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream


                try {
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("file",
                        System.currentTimeMillis() + ".jpg", in);
                reqEntity.addPart("token",
                        token, in);
                EditText edit_titre = (EditText) rootView.findViewById(R.id.titre);
                EditText edit_desc = (EditText) rootView.findViewById(R.id.description);
                reqEntity.addPart("titre",
                        edit_titre.getText().toString(), in);
                reqEntity.addPart("description",
                        edit_desc.getText().toString(), in);
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

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }*/
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            token = token;
            //mImageView.setImageBitmap(result);
        }

    }
}
