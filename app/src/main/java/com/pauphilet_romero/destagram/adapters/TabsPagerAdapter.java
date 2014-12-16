package com.pauphilet_romero.destagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pauphilet_romero.destagram.FriendsFragment;
import com.pauphilet_romero.destagram.HomeFragment;
import com.pauphilet_romero.destagram.MediaFragment;

/**
 * Created by Jimmy on 16/12/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Media fragment activity
                return new MediaFragment();
            case 1:
                // Friends fragment activity
                return new HomeFragment();
            case 2:
                // Home fragment activity
                return new FriendsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // retourne le nombre d'onglets
        return 3;
    }

}
