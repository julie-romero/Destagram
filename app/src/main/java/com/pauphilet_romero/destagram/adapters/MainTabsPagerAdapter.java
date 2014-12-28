package com.pauphilet_romero.destagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pauphilet_romero.destagram.MainTabsFriendsFragment;
import com.pauphilet_romero.destagram.MainTabsHomeFragment;
import com.pauphilet_romero.destagram.MainTabsMediaFragment;

/**
 * PagerAdapter pour les onglets de l'activité MainTabsActivity
 * Created by Jimmy on 16/12/2014.
 */
public class MainTabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Constructeur de MainTabsPagerAdapter
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
                // Fragment gérant l'upload de média
                return new MainTabsMediaFragment();
            case 1:
                // Fragment listant les nouveaux médias des amis
                return new MainTabsHomeFragment();
            case 2:
                // Fragment gérant les amis
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
