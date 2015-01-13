package com.pauphilet_romero.destagram.activities;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Activité d'inscription
 */
public class RegisterActivity extends Activity {

    // booléen déterminant si une erreur est apparue lors de l'inscription
    private Boolean error = true;
    // champ texte de l'email
    private EditText emailField;
    // champ texte du mot de passe
    private EditText passwordField;
    // champ texte de la confirmation du mot de passe
    private EditText passwordConfirmField;
    // champ texte du pseudo
    private EditText pseudoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        passwordConfirmField = (EditText) findViewById(R.id.passwordConfirmField);
        pseudoField = (EditText) findViewById(R.id.pseudoField);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
     * Inscription
     * @param view
     */
    public void register(View view) {
        // récupération de l'email
        final String email = emailField.getText().toString();
        // récupération du mot de passe
        final String password = passwordField.getText().toString();
        //récupération du mot de passe confirmé
        final String passwordConfirm = passwordConfirmField.getText().toString();
        //récupération du pseudo
        final String pseudo = pseudoField.getText().toString();
        // création de l'intent
        final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_confirm_password, Toast.LENGTH_SHORT);
        //Pattern pour la verification de l'email
        final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        //Pattern pour le mot de passe : au moins 1 minuscule, 1 majuscule, 1 chiffre, entre 6 et 20 caractères
        final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{6,20}$");

        //Si l'email n'est pas valide
        if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches())
        {
            toast.setText(R.string.error_wrong_email);
            toast.show();
        }
        // Si le mot de passe n'est pas valide
        else if (!PASSWORD_PATTERN.matcher(password).matches())
        {
            toast.setText(R.string.error_wrong_password);
            toast.show();
        }
        //Si le mot de passe et la confirmation ne sont pas égaux
        else if(!password.equals(passwordConfirm)&&password!="")
            toast.show();
        else if(pseudo == null || pseudo.equals(""))
        {
            toast.setText(R.string.error_empty_pseudo);
            toast.show();
        }
        else
        {
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

                            // Requête http // Password provisoire
                            HttpRequest request = null;
                            try {
                                request = new HttpRequest("http://destagram.zz.mu/register.php?login="+ URLEncoder.encode(email, "UTF-8")
                                        + "&password="  + URLEncoder.encode(passwordHash, "UTF-8")
                                        + "&pseudo="  + URLEncoder.encode(pseudo, "UTF-8"));

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            try {
                                // on traduit la réponse en objet JSON
                                JSONObject json = new JSONObject(request.getResponse());

                                error = json.getBoolean("error");
                                // si il n'y a pas d'erreur
                                if (!error) {
                                    toast.setText(R.string.register_success);
                                    toast.show();
                                    // changement d'activité
                                    startActivity(intent);
                                    // sinon on affiche le toast avec le message d'erreur correspondant
                                } //Si l'email existe déjà
                                else if(json.getInt("code")==1)
                                {
                                    toast.setText(R.string.existing_email);
                                    toast.show();
                                }
                                else if(json.getInt("code")==2)
                                {
                                    toast.setText(R.string.error_existing_pseudo);
                                    toast.show();
                                }
                                else {
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
}
