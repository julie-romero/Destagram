package com.pauphilet_romero.destagram;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.FriendsAdapter;
import com.pauphilet_romero.destagram.adapters.MainTabsPagerAdapter;
import com.pauphilet_romero.destagram.models.Friend;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Activité gérant les onglets "home - ajouter un média - amis"
 */
public class MainTabsActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private MainTabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        // Initialisation pour les onglets
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new MainTabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Ajout des onglets
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_new_picture).setTabListener(this), 0, false);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_menu_home).setTabListener(this), 1, true);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_group).setTabListener(this), 2, false);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
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
        if (id == R.id.myProfile) {
            Intent oldIntent = getIntent();
            String token = oldIntent.getStringExtra("token");
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ajout d'un ami
     * @param view
     */
    public void addFriend(View view)
    {
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getApplicationContext(), R.string.error_empty_friend, Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        final String token = intent.getStringExtra("token");
        EditText usernameField = (EditText) findViewById(R.id.newFriendField);
        final String username= usernameField.getText().toString();

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
                            request = new HttpRequest("http://destagram.zz.mu/add_friend.php?token="+ URLEncoder.encode(token, "UTF-8")+"&username="+ URLEncoder.encode(username, "UTF-8"));
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
