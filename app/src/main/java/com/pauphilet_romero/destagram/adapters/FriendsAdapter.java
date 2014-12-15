package com.pauphilet_romero.destagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.models.Friend;

import java.util.ArrayList;

/**
 * Created by Julie on 15/12/2014.
 */
public class FriendsAdapter extends ArrayAdapter<Friend> {
    /**
     * Cache permettant d'amÃ©liorer les performances lors de l'affichage sur la vue
     */
    private static class ViewHolder {
        TextView pseudo;
    }

    /**
     * Constructeur de l'adapter
     * @param context
     * @param friends Liste des contacts
     */
    public FriendsAdapter(Context context, ArrayList<Friend> friends) {
        super(context, 0, friends);
    }

    @Override
    /**
     * Retourne la vue Ã  l'Ã©cran
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend friend = getItem(position);
        // vÃ©rifie si une vue existante est utilisÃ©e
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_friend, parent, false);
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.friendName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // attribue les donnÃ©es de l'objet aux Ã©lÃ©ments de la vue
        viewHolder.pseudo.setText(friend.getPseudo());

        // retourne la vue Ã  l'Ã©cran
        return convertView;
    }
}
