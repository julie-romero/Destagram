package com.pauphilet_romero.destagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.CommentsAdapter;
import com.pauphilet_romero.destagram.adapters.MediasAdapter;
import com.pauphilet_romero.destagram.models.Comment;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MediaTabsCommentFragment extends android.support.v4.app.Fragment {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private CommentsAdapter adapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_media_tabs_comment, container, false);

        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        final Intent intent = getActivity().getIntent();
        final String token = intent.getStringExtra("token");
        final Integer mediaId = intent.getIntExtra("mediaId", 0);
        listView = (ListView) rootView.findViewById(R.id.listComments);
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
                        JSONObject json = new JSONObject(request.getResponse());

                        error = json.getBoolean("error");
                        // si il n'y a pas d'erreur
                        if (!error) {
                            JSONArray jsonComments = json.getJSONArray("comments");
                            final ArrayList<Comment> comments = Comment.fromJson(jsonComments);

                            adapter = new CommentsAdapter(getActivity(), comments);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // on lie l'adapter à la ListView
                                    listView.setAdapter(adapter);
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
