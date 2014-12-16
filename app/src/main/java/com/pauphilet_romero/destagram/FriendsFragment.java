package com.pauphilet_romero.destagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pauphilet_romero.destagram.adapters.FriendsAdapter;
import com.pauphilet_romero.destagram.models.Friend;
import com.pauphilet_romero.destagram.utils.ConnectionDetector;
import com.pauphilet_romero.destagram.utils.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    // booléen déterminant si une erreur est apparue lors de la connexion
    private Boolean error = true;
    private ArrayList<Friend> pseudos;
    private FriendsAdapter adapter;
    // liste des amis
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        pseudos = new ArrayList<Friend>();
        // création d'un toast pour afficher les erreurs
        final Toast toast = Toast.makeText(getActivity(), R.string.error_empty_friend, Toast.LENGTH_SHORT);
        final Intent intent = getActivity().getIntent();
        final String token = intent.getStringExtra("token");
        listView = (ListView) rootView.findViewById(R.id.listFriends);
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
                        request = new HttpRequest("http://destagram.zz.mu/friends.php?token="+ URLEncoder.encode(token, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        // on traduit la réponse en objet JSON
                        JSONObject json = new JSONObject(request.getResponse());

                        error = json.getBoolean("error");
                        // si il n'y a pas d'erreur
                        if (!error) {
                            JSONArray jsonContacts = json.getJSONArray("friends");
                            final ArrayList<Friend> friends = Friend.fromJson(jsonContacts);
                            pseudos.addAll(friends);
                            adapter = new FriendsAdapter(getActivity(), pseudos);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // on lie l'adapter Ã  la ListView
                                    listView.setAdapter(adapter);
                                }
                            });

                        } else if(json.getInt("code")==1){
                            toast.setText(R.string.error_not_existing_account);
                            toast.show();
                        }
                        else if(json.getInt("code")==2) {
                            toast.setText(R.string.error_friend_in_list);
                            toast.show();
                        }
                        else if(json.getInt("code")==3) {
                            toast.setText(R.string.error_add_myself);
                            toast.show();
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