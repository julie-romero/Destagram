package com.pauphilet_romero.destagram;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.pauphilet_romero.destagram.adapters.MediaTabsPagerAdapter;

/**
 * Activité gérant les onglets "infos d'un média - commentaires d'un média"
 */
public class MediaTabsActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private MediaTabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_tabs);

        // Initialisation pour les onglets
        viewPager = (ViewPager) findViewById(R.id.pagerMedia);
        actionBar = getActionBar();
        mAdapter = new MediaTabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Ajout des onglets
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_about).setTabListener(this), 0, true);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_action_chat).setTabListener(this), 1, false);

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
}
