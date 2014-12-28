package com.pauphilet_romero.destagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.CommentsAdapter;
import com.pauphilet_romero.destagram.models.Comment;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Onglet des commentaires
 */
public class MediaTabsCommentFragment extends android.support.v4.app.Fragment {

    // Booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    // Adapter des commentaires
    private CommentsAdapter adapter;
    // Composant ListView affichant les commentaires
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_media_tabs_comment, container, false);

        // Création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        // Récupération de l'intent
        final Intent intent = getActivity().getIntent();
        // Récupération du token de connexion
        final String token = intent.getStringExtra("token");
        // Récupération de l'identifiant du média
        final Integer mediaId = intent.getIntExtra("mediaId", 0);

        listView = (ListView) rootView.findViewById(R.id.listComments);

        // Vérification de la connexion Internet
        ConnectionDetector connection = new ConnectionDetector(getActivity());
        if (connection.isConnectingToInternet()) {
            // Thread exécutant la requête HTTP
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpRequest request = null;
                    try {
                        // Requête HTTP appelant le webservice media.php
                        request = new HttpRequest("http://destagram.zz.mu/media.php?token="+ URLEncoder.encode(token, "UTF-8")+"&id=" + mediaId);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        // Récupération de la réponse JSON du webservice
                        JSONObject json = new JSONObject(request.getResponse());
                        error = json.getBoolean("error");

                        // si le webservice ne retourne pas d'erreur
                        if (!error) {
                            // Récupération des commentaires
                            JSONArray jsonComments = json.getJSONArray("comments");
                            final ArrayList<Comment> comments = Comment.fromJson(jsonComments);

                            // Utilisation d'un adapter pour faire le lien entre les commentaires
                            // et le composant ListView
                            adapter = new CommentsAdapter(getActivity(), comments);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setAdapter(adapter);
                                }
                            });

                            // envoi d'un commentaire
                            final Button sendButton = (Button) getActivity().findViewById(R.id.sendComment);
                            sendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            EditText message = (EditText) getActivity().findViewById(R.id.commentMessage);

                                            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                                            nameValuePairs.add(new BasicNameValuePair("token", token));
                                            nameValuePairs.add(new BasicNameValuePair("comment", message.getText().toString() ));
                                            nameValuePairs.add(new BasicNameValuePair("id", mediaId+""));

                                            new HttpRequest("http://destagram.zz.mu/comment.php", nameValuePairs);

                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());
                                        }
                                    });
                                    thread.start();
                                }
                            });
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
