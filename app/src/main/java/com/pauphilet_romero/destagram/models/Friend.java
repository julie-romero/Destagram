package com.pauphilet_romero.destagram.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Julie on 15/12/2014.
 */
public class Friend {
    private int id;
    private String pseudo;

    // Constructeur permettant de convertir un objet JSON en une instance Java
    public Friend(JSONObject object){
        try {
            this.id = object.getInt("id");
            this.pseudo = object.getString("pseudo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Conversion d'un tableau d'objets JSON en liste d'objets
    public static ArrayList<Friend> fromJson(JSONArray jsonObjects) {
        ArrayList<Friend> friends = new ArrayList<Friend>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                friends.add(new Friend(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {

        return pseudo;
    }

    public int getId() {
        return id;
    }
}
