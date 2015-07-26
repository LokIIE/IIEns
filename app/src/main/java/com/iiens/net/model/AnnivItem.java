package com.iiens.net.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * AnnivItem
 * Modèle d'un anniversaire
 */

public class AnnivItem {
    private String nom = "";
    private String prenom = "";
    private String pseudo = "";
    private String anniv = "";
    private String age = "";

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

    public void mapJsonObject(JSONObject json_data) {
        try {
            setNom(json_data.getString("nom"));
            setPrenom(json_data.getString("prenom"));
            setPseudo(json_data.getString("surnom"));
            setAnniv(json_data.getString("anniv"));
            setAge(json_data.getString("age"));
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> result = new ArrayList<>();

        result.add(nom);
        result.add(prenom);
        result.add(pseudo);
        result.add(anniv);
        result.add(age);

        return result;
    }
}