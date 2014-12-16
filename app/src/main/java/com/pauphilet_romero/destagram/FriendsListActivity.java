package com.pauphilet_romero.destagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.adapters.FriendsAdapter;
import com.pauphilet_romero.destagram.models.Friend;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;
import com.pauphilet_romero.destagram.utils.PasswordEncrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FriendsListActivity extends Activity {
    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private ArrayList<Friend> pseudos;
    private FriendsAdapter adapter;
    // liste des amis
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        pseudos = new ArrayList<Friend>();
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_friend, Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        listView = (ListView) findViewById(R.id.listFriends);
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
                        request = new HttpRequest("http://destagram.zz.mu/friends.php?token="+ URLEncoder.encode(token, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        // on traduit la réponse en objet JSON
                        JSONObject json = new JSONObject(request.getResponse());

                        error = json.getBoolean("error");
                        // si il n'y a pas d'erreur
                        if (!error) {
                            JSONArray jsonContacts = json.getJSONArray("friends");
                            final ArrayList<Friend> friends = Friend.fromJson(jsonContacts);
                            pseudos.addAll(friends);
                             adapter = new FriendsAdapter(getApplicationContext(), pseudos);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // on lie l'adapter Ã  la ListView
                                    listView.setAdapter(adapter);
                                }
                            });

                        } else if(json.getInt("code")==1){
                            toast.setText(R.string.error_not_existing_account);
                            toast.show();
                        }
                        else if(json.getInt("code")==2) {
                            toast.setText(R.string.error_friend_in_list);
                            toast.show();
                        }
                        else if(json.getInt("code")==3) {
                            toast.setText(R.string.error_add_myself);
                            toast.show();
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
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
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

    public void addFriend(View view)
    {
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_friend, Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        EditText usernameField = (EditText) findViewById(R.id.newFriendField);
        final String username= usernameField.getText().toString();
        // liste des amis
        final ListView listView = (ListView) findViewById(R.id.listFriends);
        if(username!= null && !username.equals("") ) {
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
                            request = new HttpRequest("http://destagram.zz.mu/add_friend.php?token="+URLEncoder.encode(token, "UTF-8")+"&username="+ URLEncoder.encode(username, "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            // on traduit la réponse en objet JSON
                            JSONObject json = new JSONObject(request.getResponse());

                            error = json.getBoolean("error");
                            // si il n'y a pas d'erreur
                            if (!error) {
                                JSONObject jsonFriend = json.getJSONObject("friend");
                                final Friend friend = new Friend(jsonFriend);
                                //pseudos.add(friend);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        adapter.add(friend);
                                    }
                                });
                            } else if(json.getInt("code")==1){
                                toast.setText(R.string.error_not_existing_account);
                                toast.show();
                            }
                            else if(json.getInt("code")==2) {
                                toast.setText(R.string.error_friend_in_list);
                                toast.show();
                            }
                            else if(json.getInt("code")==3) {
                                toast.setText(R.string.error_add_myself);
                                toast.show();
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
        else {

            toast.show();
        }

    }

}
