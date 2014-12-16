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
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.TabsPagerAdapter;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;

public class ProfilActivity extends FragmentActivity implements ActionBar.TabListener {

    public String token;

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // récupération de l'intent
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra("token");
        }

        // Initialisation pour les onglets
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

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
