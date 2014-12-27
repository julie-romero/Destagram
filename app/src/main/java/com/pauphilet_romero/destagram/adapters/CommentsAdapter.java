package com.pauphilet_romero.destagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pauphilet_romero.destagram.R;
import com.pauphilet_romero.destagram.models.Comment;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.DateFunctions;
import com.pauphilet_romero.destagram.utils.DownloadImageTask;

import java.util.ArrayList;

/**
 * Created by Jimmy on 16/12/2014.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    private ViewHolder viewHolder;
    private Comment comment;

    /**
     * Cache permettant d'améliorer les performances lors de l'affichage sur la vue
     */
    private static class ViewHolder {
        TextView username;
        TextView date;
        TextView message;
    }

    /**
     * Constructeur de l'adapter
     * @param context
     * @param comments Liste des commentaires
     */
    public CommentsAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    @Override
    /**
     * Retourne la vue à l'écran
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        comment = getItem(position);
        // vérifie si une vue existante est utilisée

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_comment, parent, false);
            viewHolder.username = (TextView) convertView.findViewById(R.id.commentUsername);
            viewHolder.date = (TextView) convertView.findViewById(R.id.commentDate);
            viewHolder.message = (TextView) convertView.findViewById(R.id.commentMessage);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // attribue les données de l'objet aux éléments de la vue
        viewHolder.username.setText(comment.getPseudo());
        viewHolder.date.setText(DateFunctions.diffDate(comment.getDate()));
        viewHolder.message.setText(comment.getMessage());

        // retourne la vue à l'écran
        return convertView;
    }
}