package com.pauphilet_romero.destagram.models;

import com.pauphilet_romero.destagram.utils.DateFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Modèle Media
 * Created by Jimmy on 16/12/2014.
 */
public class Media {

    private int id;
    private String name;
    private String extension;
    private String titre;
    private String description;
    private int user_id;
    private Date date;

    /***
     * Constructeur permettant de convertir un objet JSON en une instance Java
     * @param object
     */
    public Media(JSONObject object){
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.extension = object.getString("extension");
            this.titre = object.getString("titre");
            this.description = object.getString("description");
            this.user_id = object.getInt("user_id");
            this.date = DateFunctions.dateConvert(object.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * Conversion d'un tableau d'objets JSON en liste d'objets
     * @param jsonObjects
     * @return
     */
    public static ArrayList<Media> fromJson(JSONArray jsonObjects) {
        ArrayList<Media> medias = new ArrayList<Media>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                medias.add(new Media(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return medias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
