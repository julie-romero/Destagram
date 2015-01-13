package com.pauphilet_romero.destagram.activities.homeTabs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.activities.ProfileActivity;
import com.pauphilet_romero.destagram.utils.FileDialog;

/**
 * Onglet "Ajout de média"
 */
public class UploadFragment extends Fragment {
    private ImageButton mTakePhoto;
    private Button mUpload;
    private ImageButton mChooseFile;
    private ImageView mImageView;
    private FileDialog fileDialog;
    private Bitmap fullSizePhoto;
    private static final String TAG = "upload";
    private String token = "";
    private EditText editTitre;
    private EditText editDesc;
    static final int REQUEST_TAKE_PHOTO = 1;
    File fullSizeFile = null;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Intent intent = getActivity().getIntent();
        token = intent.getStringExtra("token");
        rootView = inflater.inflate(R.layout.fragment_main_tabs_media, container, false);
        mChooseFile = (ImageButton) rootView.findViewById(R.id.choose_file);
        mTakePhoto = (ImageButton) rootView.findViewById(R.id.take_photo);
        mImageView = (ImageView) rootView.findViewById(R.id.imageview);
        mUpload = (Button) rootView.findViewById(R.id.upload);
        editTitre = (EditText) rootView.findViewById(R.id.titre);
        editDesc = (EditText) rootView.findViewById(R.id.description);
        editTitre.setText("");
        editDesc.setText("");

        mChooseFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                HomeActivity activity = (HomeActivity)getActivity();
                fileDialog = new FileDialog(activity, mPath);
                fileDialog.setFileEndsWith(".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());
                        fullSizeFile = file;
                        HomeActivity activity = (HomeActivity)getActivity();
                        activity.setmCurrentPhotoFullSizePath(file.toString());
                        setFullImageFromFilePath(activity.getmCurrentPhotoFullSizePath(), mImageView);
                        compressFile(fullSizePhoto);

                    }
                });

                fileDialog.showDialog();
            }
        });

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
                if (fullSizeFile == null || !fullSizeFile.exists()) {
                    Toast toast = Toast.makeText(getActivity(), R.string.error_media_file, Toast.LENGTH_SHORT);
                    toast.show();
                } else if (TextUtils.isEmpty(editTitre.getText().toString())) {
                    Toast toast = Toast.makeText(getActivity(), R.string.error_media_title, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    sendPhoto();
                }
            }

        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        HomeActivity activity = (HomeActivity)getActivity();
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                setFullImageFromFilePath(activity.getmCurrentPhotoFullSizePath(), mImageView);
                compressFile(fullSizePhoto);
            } else {
                Toast.makeText(getActivity(), "La capture a échoué", Toast.LENGTH_SHORT)
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
            Log.e("Dak","Erreur de compression : "+e.toString());
        }
    }
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {

        // Récupère les dimensions de la vue
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Récupère les dimensions du bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Calcule l'echelle de l'image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Décodage de l'image redimensionée pour remplir la vue
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bmOptions.inDither=false;
        bmOptions.inTempStorage=new byte[1024];
        fullSizePhoto = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(fullSizePhoto);
    }

    private void sendPhoto() {
        new UploadImageTask().execute(token);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void takePhoto() {
        // Vérifie si il y a une camera
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "Il n'y a pas d'appareil photo petit plaisantin!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Si la camera existe. Procédons !
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // S'assurer qu'il y ait une application pour prendre une photo
        HomeActivity activity = (HomeActivity)getActivity();
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Création d'un fichier pour accueillir la photo
            try {
                fullSizeFile = createImageFile();
            } catch (IOException ex) {
                // Erreur pendant la création du fichier
                Toast toast = Toast.makeText(activity, "Problème pendant la sauvegarde du fichier", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue si le fichier est bien créé
            if (fullSizeFile != null) {
                //Lance l'application permettant de capturer une photo
                activity.setmCurrentPhotoFullSizePath(Uri.fromFile(fullSizeFile).getPath().toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(fullSizeFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
           }
        }
    }


    private File createImageFile() throws IOException {
        // Créer un fichier pour l'image à venir
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


    private class UploadImageTask extends AsyncTask {

        private String token;
        @Override
        protected Object doInBackground(Object[] params) {
            token = params[0].toString();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HomeActivity activity = (HomeActivity)getActivity();
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
                    reqEntity.addPart("titre",new StringBody(edit_titre.getText().toString(), Charset.forName("UTF-8")));
                    reqEntity.addPart("description",
                            new StringBody(edit_desc.getText().toString(), Charset.forName("UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                };

                httppost.setEntity(reqEntity);
                Log.i(TAG, "request " + httppost.getRequestLine());
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    String json = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "response : " + json);
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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


    }
}
