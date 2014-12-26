package com.pauphilet_romero.destagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.MediasAdapter;
import com.pauphilet_romero.destagram.models.Media;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Fragment pour l'onglet "Home"
 */
public class HomeFragment extends Fragment {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private MediasAdapter adapter;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        gridView = (GridView) rootView.findViewById(R.id.listMedias);

        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        final Intent intent = getActivity().getIntent();
        final String token = intent.getStringExtra("token");
        gridView = (GridView) rootView.findViewById(R.id.listMedias);
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
                    request = new HttpRequest("http://destagram.zz.mu/medias.php?token="+ URLEncoder.encode(token, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    // on traduit la réponse en objet JSON
                    JSONObject json = new JSONObject(request.getResponse());

                    error = json.getBoolean("error");
                    // si il n'y a pas d'erreur
                    if (!error) {
                        JSONArray jsonMedias = json.getJSONArray("medias");
                        final ArrayList<Media> medias = Media.fromJson(jsonMedias);

                        adapter = new MediasAdapter(getActivity(), medias);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            // on lie l'adapter à la ListView
                            gridView.setAdapter(adapter);

                            // au clic sur un média, on lance la MediaActivity correspondante
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                // on récupère le média sélectionné
                                Media media = (Media) gridView.getItemAtPosition(position);

                                // On instancie l'intent
                                Intent intent = new Intent(getActivity(), MediaActivity.class);
                                // On y place les données souhaitées
                                intent.putExtra("mediaId", media.getId());
                                intent.putExtra("mediaTitle", media.getTitre());
                                intent.putExtra("token", token);

                                // On démarre la nouvelle activité
                                getActivity().startActivity(intent);
                                }
                            });
                        }
                        });
                    }
                    else
                    {
                        toast.setText(R.string.error_connect);
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