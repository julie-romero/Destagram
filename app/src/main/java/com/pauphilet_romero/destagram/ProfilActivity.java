package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;
import com.pauphilet_romero.destagram.utils.PasswordEncrypt;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfilActivity extends Activity {

    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        // récupération de l'intent
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra("token");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayFriends(View view) {
        // création de l'intent
        final Intent intent2 = new Intent(getApplicationContext(), FriendsListActivity.class);
        // on vérifie la connexion Internet
        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT);
        if (connection.isConnectingToInternet()) {
            // nouveau thread pour la requête HTTP
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Requête http

                            // on envoie le token à l'activité ContactsActivity
                            intent2.putExtra("token",token);
                            // changement d'activité
                            startActivity(intent2);
                            // sinon on affiche le toast avec le message d'erreur correspondant

                }
            });
            thread.start();
        }
        else
        {
            toast.setText(R.string.error_internet);
            toast.show();
        }
    }
}
