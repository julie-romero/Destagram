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
        final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        if(!password.equals(passwordConfirm)&&password!="")
            toast.show();
        else if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches())
        {
            toast.setText(R.string.error_wrong_email);
            toast.show();
        }


    }
}
