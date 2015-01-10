package com.pauphilet_romero.destagram.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.DownloadImageTask;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Adapter pour les médias
 * Created by Jimmy on 16/12/2014.
 */
public class MediasAdapter extends ArrayAdapter<Media> {

    private ViewHolder viewHolder;
    private Media media;

    /**
     * Cache permettant d'améliorer les performances lors de l'affichage sur la vue
     */
    private static class ViewHolder {
        TextView titre;
        ImageView picture;
    }

    /**
     * Constructeur de l'adapter
     * @param context
     * @param medias Liste des medias
     */
    public MediasAdapter(Context context, ArrayList<Media> medias) {
        super(context, 0, medias);
    }

    @Override
    /**
     * Retourne la vue à l'écran
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        media = getItem(position);
        // vérifie si une vue existante est utilisée

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_media, parent, false);
            viewHolder.titre = (TextView) convertView.findViewById(R.id.mediaTitle);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(viewHolder);
            new DownloadImageTask(viewHolder.picture).execute("http://destagram.zz.mu/uploads/" + media.getName() + "." + media.getExtension());

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // attribue les données de l'objet aux éléments de la vue
        viewHolder.titre.setText(media.getTitre());

        // retourne la vue à l'écran
        return convertView;
    }

}