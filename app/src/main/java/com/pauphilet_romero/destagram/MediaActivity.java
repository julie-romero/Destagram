package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.MediasAdapter;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.DownloadImageTask;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Activité affichant un média, ses commentaires et les informations lui étant liées
 */
public class MediaActivity extends Activity {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        // On récupère l'ID du média via l'intent
        Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        final Integer mediaId = intent.getIntExtra("mediaId", 0);

        // On change dynamiquement le titre de la vue selon le média
        setTitle(intent.getStringExtra("mediaTitle"));

        // on vérifie la connexion Internet
        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        if(connection.isConnectingToInternet()) {
            // nouveau thread pour la requête HTTP
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                // Requête http
                HttpRequest request = null;
                try {
                    request = new HttpRequest("http://destagram.zz.mu/media.php?token="+ URLEncoder.encode(token, "UTF-8")+"&id=" + mediaId);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    // on traduit la réponse en objet JSON
                    JSONObject json = new JSONObject(request.getResponse());

                    error = json.getBoolean("error");
                    // si il n'y a pas d'erreur
                    if (!error) {
                        JSONObject jsonMedia = json.getJSONObject("media");
                        Media media = new Media(jsonMedia);
                        final ImageView imageView = (ImageView) findViewById(R.id.mediaFrame);
                        Log.d("test", json.toString());

                        new DownloadImageTask(imageView).execute("http://destagram.zz.mu/uploads/" + media.getName() + "." + media.getExtension());
                    }
                    else
                    {
                        toast.setText(R.string.error_connect);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            });
            thread.start();
        } else {
            toast.setText(R.string.error_internet);
            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
