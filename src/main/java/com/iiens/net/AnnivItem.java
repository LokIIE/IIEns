package com.iiens.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * AnnivItem
 * Classe permettant de stocker les infos concernant un anniversaire
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

public class AnnivItem {
    private String nom = "";
    private String prenom = "";
    private String pseudo = "";
    private String anniv = "";
    private String age = "";

    public AnnivItem() {
    }

    public AnnivItem(String nom, String prenom, String pseudo, String anniv, String age) {
        setNom(nom);
        setPrenom(prenom);
        setPseudo(pseudo);
        setAnniv(anniv);
        setAge(age);
    }

    public String getNom() {
        return nom;
    }

    private void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    private void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    private void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getAnniv() {
        return anniv;
    }

    private void setAnniv(String anniv) {
        this.anniv = anniv;
    }

    public String getAge() {
        return age;
    }

    private void setAge(String age) {
        this.age = age;
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