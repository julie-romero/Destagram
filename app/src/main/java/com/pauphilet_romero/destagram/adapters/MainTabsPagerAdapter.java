package com.pauphilet_romero.destagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pauphilet_romero.destagram.MainTabsFriendsFragment;
import com.pauphilet_romero.destagram.MainTabsHomeFragment;
import com.pauphilet_romero.destagram.MainTabsMediaFragment;

/**
 * Created by Jimmy on 16/12/2014.
 */
public class MainTabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Constructeur de TabsPagerAdapter
     * @param fm
     */
    public MainTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    /**
     * Retourne le fragment correspondant à son onglet
     */
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Media fragment activity
                return new MainTabsMediaFragment();
            case 1:
                // Friends fragment activity
                return new MainTabsHomeFragment();
            case 2:
                // Home fragment activity
                return new MainTabsFriendsFragment();
        }

        return null;
    }

    @Override
    /**
     * Retourne le nombre d'onglets
     */
    public int getCount() {
        return 3;
    }
}
