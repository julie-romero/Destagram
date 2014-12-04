package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.regex.Pattern;


public class RegisterActivity extends Activity {

    // booléen déterminant si une erreur est apparue lors de l'inscription
    private Boolean error = true;
    // champ texte de l'email
    private EditText emailField;
    // champ texte du mot de passe
    private EditText passwordField;
    // champ texte de la confirmation du mot de passe
    private EditText passwordConfirmField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        passwordConfirmField = (EditText) findViewById(R.id.passwordConfirmField);
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

    public void register(View view) {
        // récupération de l'email
        final String email = emailField.getText().toString();
        // récupération du mot de passe
        final String password = passwordField.getText().toString();
        //récupération du mot de passe confirmé
        final String passwordConfirm = passwordConfirmField.getText().toString();
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
        //Si le mot de passe et la confirmation ne sont pas égaux
        if(!password.equals(passwordConfirm)&&password!="")
            toast.show();
        //Si l'email n'est pas valide
        else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches())
        {
            toast.setText(R.string.error_wrong_email);
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
                            HttpRequest request = new HttpRequest("http://destagram.zz.mu/register.php?login="+email+"&password="+password);
                            try {
                                Log.d("lala", request.getResponse().toString());
                                // on traduit la réponse en objet JSON
                                JSONObject json = new JSONObject(request.getResponse());

                                error = json.getBoolean("error");
                                // si il n'y a pas d'erreur
                                if (!error) {
                                    // changement d'activité
                                    startActivity(intent);
                                    // sinon on affiche le toast avec le message d'erreur correspondant
                                } //Si l'email existe déjà
                                else if(json.getInt("code")==1)
                                {
                                    toast.setText(R.string.existing_email);
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
