package com.pauphilet_romero.destagram.models;

import com.pauphilet_romero.destagram.utils.DateFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jimmy on 16/12/2014.
 */
public class Comment {

    private int id;
    private String message;
    private int userId;
    private String pseudo;
    private int mediaId;
    private Date date;

    /***
     * Constructeur permettant de convertir un objet JSON en une instance Java
     * @param object
     */
    public Comment(JSONObject object){
        try {
            this.id = object.getInt("id");
            this.message = object.getString("message");
            this.mediaId = object.getInt("media_id");
            this.userId = object.getInt("user_id");
            this.pseudo = object.getString("pseudo");
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
    public static ArrayList<Comment> fromJson(JSONArray jsonObjects) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                comments.add(new Comment(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
