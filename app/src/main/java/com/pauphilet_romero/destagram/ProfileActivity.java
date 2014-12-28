package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.MediasAdapter;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ProfileActivity extends Activity {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private MediasAdapter adapter;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        final int userId = intent.getIntExtra("userId", 0);
        gridView = (GridView) findViewById(R.id.listMedias);

        // On change dynamiquement le titre de la vue selon le média
        if (intent.getStringExtra("userPseudo") == null) {
            setTitle("Vos médias");
        } else {
            setTitle("Médias de " + intent.getStringExtra("userPseudo"));
        }

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
                        request = new HttpRequest("http://destagram.zz.mu/medias.php?token="+ URLEncoder.encode(token, "UTF-8")
                            + "&id=" + userId);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        // on traduit la réponse en objet JSON
                        JSONObject json = new JSONObject(request.getResponse());

                        error = json.getBoolean("error");
                        // si il n'y a pas d'erreur
                        if (!error) {
                            JSONArray jsonMedias = json.getJSONArray("medias");
                            final ArrayList<Media> medias = Media.fromJson(jsonMedias);

                            adapter = new MediasAdapter(getApplicationContext(), medias);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // on lie l'adapter à la gridView
                                    gridView.setAdapter(adapter);

                                    // au clic sur un média, on lance la MediaActivity correspondante
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                            // on récupère le média sélectionné
                                            Media media = (Media) gridView.getItemAtPosition(position);

                                            // On instancie l'intent
                                            Intent intent = new Intent(getApplicationContext(), MediaTabsActivity.class);
                                            // On y place les données souhaitées
                                            intent.putExtra("mediaId", media.getId());
                                            intent.putExtra("mediaTitle", media.getTitre());
                                            intent.putExtra("token", token);

                                            // On démarre la nouvelle activité
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            toast.setText(R.string.error_general);
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
