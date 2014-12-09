package com.pauphilet_romero.destagram;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pauphilet_romero.destagram.profileTabs.FriendFragment;
import com.pauphilet_romero.destagram.profileTabs.HomeFragment;
import com.pauphilet_romero.destagram.profileTabs.MediaFragment;
import com.pauphilet_romero.destagram.profileTabs.TabListener;

public class ProfilActivity extends Activity {

    // Declaring our tabs and the corresponding fragments.
    ActionBar.Tab mediaTab, homeTab, friendTab;
    Fragment mediaFragmentTab = new MediaFragment();
    Fragment friendFragmentTab = new FriendFragment();
    Fragment homeFragmentTab = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asking for the default ActionBar element that our platform supports.
        ActionBar actionBar = getActionBar();

        // Screen handling while hiding ActionBar icon.
        actionBar.setDisplayShowHomeEnabled(false);

        // Screen handling while hiding Actionbar title.
        actionBar.setDisplayShowTitleEnabled(false);

        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Setting custom tab icons.
        mediaTab = actionBar.newTab().setIcon(R.drawable.ic_plus);
        friendTab = actionBar.newTab().setIcon(R.drawable.ic_friends);
        homeTab = actionBar.newTab().setIcon(R.drawable.ic_home);

        // Setting tab listeners.
        mediaTab.setTabListener(new TabListener(mediaFragmentTab));
        friendTab.setTabListener(new TabListener(friendFragmentTab));
        homeTab.setTabListener(new TabListener(homeFragmentTab));

        // Adding tabs to the ActionBar.
        actionBar.addTab(mediaTab);
        actionBar.addTab(friendTab);
        actionBar.addTab(homeTab);
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
}
