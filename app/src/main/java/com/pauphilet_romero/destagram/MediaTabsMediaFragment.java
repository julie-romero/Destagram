package com.pauphilet_romero.destagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.DateFunctions;
import com.pauphilet_romero.destagram.utils.DownloadImageTask;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Onglet affichant un média, ses commentaires et les informations lui étant liées
 */
public class MediaTabsMediaFragment extends Fragment {

    // Booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_media_tabs_media, container, false);

        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        // On récupère l'ID du média et le token de connexion via l'intent
        Intent intent = getActivity().getIntent();
        final String token = intent.getStringExtra("token");
        final Integer mediaId = intent.getIntExtra("mediaId", 0);

        // On change dynamiquement le titre de la vue selon le média
        getActivity().setTitle(intent.getStringExtra("mediaTitle"));

        // on vérifie la connexion Internet
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        if(connection.isConnectingToInternet()) {
            // nouveau thread pour la requête HTTP
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Requête http
                    HttpRequest request = null;
                    try {
                        request = new HttpRequest("http://destagram.zz.mu/media.php?token="+ URLEncoder.encode(token, "UTF-8")+"&id=" + mediaId);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        // on traduit la réponse en objet JSON
                        final JSONObject json = new JSONObject(request.getResponse());

                        error = json.getBoolean("error");
                        // si il n'y a pas d'erreur
                        if (!error) {
                            // récupération des informations sur le média
                            JSONObject jsonMedia = json.getJSONObject("media");
                            final Media media = new Media(jsonMedia);
                            final String mediaUsername = json.getJSONObject("author").getString("username");
                            final String nbLikes = json.getInt("likes")+"";
                            final boolean isLike = json.getBoolean("isLike");

                            // composants affichant les informations textuelles
                            final TextView mediaTitle = (TextView) getActivity().findViewById(R.id.mediaTitle);
                            final TextView mediaUser = (TextView) getActivity().findViewById(R.id.mediaUser);
                            final TextView mediaDate = (TextView) getActivity().findViewById(R.id.mediaDate);
                            final TextView mediaDescription = (TextView) getActivity().findViewById(R.id.mediaDescription);
                            final TextView mediaNbLikes = (TextView) getActivity().findViewById(R.id.mediaNbLikes);
                            final Button buttonLike = (Button) getActivity().findViewById(R.id.mediaLike);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                     // modification de ces composants
                                    mediaTitle.setText(media.getTitre());
                                    mediaUser.setText(mediaUsername);
                                    mediaDate.setText(DateFunctions.diffDate(media.getDate()));
                                    mediaDescription.setText(media.getDescription());
                                    mediaNbLikes.setText(nbLikes);

                                    if (isLike) {
                                        buttonLike.setText(R.string.dislike);
                                    } else {
                                        buttonLike.setText(R.string.like);
                                    }
                                }
                            });

                            // évènement sur clic sur le bouton like
                            buttonLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                new HttpRequest("http://destagram.zz.mu/like.php" +
                                                        "?token=" + URLEncoder.encode(token, "UTF-8")
                                                        + "&id=" + mediaId
                                                        + "&like=" + !isLike);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());
                                        }
                                    });
                                    thread.start();
                                }
                            });

                            // composant qui affichera le média (image)
                            final ImageView imageView = (ImageView) getActivity().findViewById(R.id.mediaFrame);
                            // téléchargement en arrière-plan de l'image
                            new DownloadImageTask(imageView).execute("http://destagram.zz.mu/uploads/" + media.getName() + "." + media.getExtension());
                        }
                        else
                        {
                            toast.setText(R.string.error_general);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            toast.setText(R.string.error_internet);
            toast.show();
        }

        return rootView;
    }

}