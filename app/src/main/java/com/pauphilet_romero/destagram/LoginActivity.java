package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;
import com.pauphilet_romero.destagram.utils.PasswordEncrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Activité de connexion à l'application
 */
public class LoginActivity extends Activity {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    // champ texte de l'email
    private EditText emailField;
    // champ texte du mot de passe
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    /**
     * Connexion à l'application
     * @param view
     */
    public void login(View view) {
        // récupération de l'email
        final String email = emailField.getText().toString();
        // récupération du mot de passe
        final String password = passwordField.getText().toString();
        // création de l'intent
        final Intent intent = new Intent(getApplicationContext(), MainTabsActivity.class);
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT);

        // on vérifie la connexion Internet
        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        if(connection.isConnectingToInternet()) {
            // on vérifie que les champs ne sont pas vides
            if (!email.isEmpty() && !password.isEmpty()) {

                // nouveau thread pour la requête HTTP
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String passwordHash = PasswordEncrypt.encryptPassword(password);

                        // Requête http
                        HttpRequest request = null;
                        try {
                            request = new HttpRequest("http://destagram.zz.mu/login.php?login="+ URLEncoder.encode(email, "UTF-8")+"&password="+URLEncoder.encode(passwordHash, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            // on traduit la réponse en objet JSON
                            JSONObject json = new JSONObject(request.getResponse());

                            error = json.getBoolean("error");
                            // si il n'y a pas d'erreur
                            if (!error) {
                                // on envoie le token à l'activité ContactsActivity
                                intent.putExtra("token", json.getString("token"));
                                // changement d'activité
                                startActivity(intent);
                                // sinon on affiche le toast avec le message d'erreur correspondant
                            } else {
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
                toast.show();
            }
        } else {
            toast.setText(R.string.error_internet);
            toast.show();
        }
    }

}
