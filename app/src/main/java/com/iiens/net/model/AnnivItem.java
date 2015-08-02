package com.iiens.net.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AnnivItem
 * Modèle d'un anniversaire
 */

public class AnnivItem {
    private long id = 0;
    private String nom = "";
    private String prenom = "";
    private String pseudo = "";
    private String anniv = "";
    private String age = "";

    public void setId (long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getAnniv() {
        return anniv;
    }

    public void setAnniv(String anniv) {
        this.anniv = anniv;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public AnnivItem() {
    }

    public AnnivItem(String nom, String prenom, String pseudo, String anniv, String age) {
        setNom(nom);
        setPrenom(prenom);
        setPseudo(pseudo);
        setAnniv(anniv);
        setAge(age);
    }

    public static AnnivItem load(JSONObject json_data) {
        AnnivItem item = new AnnivItem();
        try {
            item.nom = json_data.getString("nom");
            item.prenom = json_data.getString("prenom");
            item.pseudo = json_data.getString("surnom");
            item.anniv = json_data.getString("anniv");
            item.age = json_data.getString("age");
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return item;
    }
}