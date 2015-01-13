package com.pauphilet_romero.destagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pauphilet_romero.destagram.activities.mediaTabs.CommentFragment;
import com.pauphilet_romero.destagram.activities.mediaTabs.MediaInfoFragment;

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
                return new MediaInfoFragment();
            case 1:
                // Fragment gérant les commentaires du média
                return new CommentFragment();
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
