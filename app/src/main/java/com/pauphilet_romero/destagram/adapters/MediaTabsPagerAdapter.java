package com.pauphilet_romero.destagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pauphilet_romero.destagram.MainTabsFriendsFragment;
import com.pauphilet_romero.destagram.MainTabsHomeFragment;
import com.pauphilet_romero.destagram.MainTabsMediaFragment;
import com.pauphilet_romero.destagram.MediaTabsCommentFragment;
import com.pauphilet_romero.destagram.MediaTabsMediaFragment;

/**
 * PagerAdapter pour les onglets de l'activité MediaTabsActivity
 * Created by Jimmy on 16/12/2014.
 */
public class MediaTabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Constructeur de MediaTabsPagerAdapter
     * @param fm
     */
    public MediaTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    /**
     * Retourne le fragment correspondant à son onglet
     */
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Fragment gérant les informations du média
                return new MediaTabsMediaFragment();
            case 1:
                // Fragment gérant les commentaires du média
                return new MediaTabsCommentFragment();
        }

        return null;
    }

    @Override
    /**
     * Retourne le nombre d'onglets
     */
    public int getCount() {
        return 2;
    }
}
