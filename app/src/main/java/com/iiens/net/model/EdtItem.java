package com.iiens.net.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * EdtItem
 * Modèle d'un évènement de l'emploi du temps
 */

public class EdtItem {
    private long id;
    private String jour; // "2014-12-31"
    private String titre; // Intitulé de l'événement
    private String auteur; // Cours : prof assurant le cours / Club : club faisant l'evenement
    private Integer heure; // Heure de debut en quarts d'heure a partir de minuit (ex : 0h30 <=> 2)
    private Integer duree; // Nombre de quarts d'heure
    private String type; // Cours : T.D., T.P., Cours_td, controle, Cours / Club : assoce
    private String groupe; // Cours : groupe du cours si defini / Club : vide
    private String lieu; // Lieu du cours ou de l'evenement

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public String getJour() {
        return jour;
    }

    private void setJour(String jour) {
        this.jour = jour;
    }

    public String getTitre() {
        return titre;
    }

    private void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    private void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Integer getHeure() {
        return heure;
    }

    private void setHeure(Integer heure) {
        this.heure = heure;
    }

    public Integer getDuree() {
        return duree;
    }

    private void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getType() {
        return type;
    }

    private void setType(String nom_type) {
        this.type = nom_type;
    }

    public String getGroupe() {
        return groupe;
    }

    private void setGroupe(String groupe) {
        this.groupe = groupe;
    }

    public String getLieu() {
        return lieu;
    }

    private void setLieu(String salle) {
        this.lieu = salle;
        if (salle.equals("2")) {
            this.lieu = "amphi " + salle;
        }
    }

    public void mapJsonObject(JSONObject json_data) {
        try {
            setJour(json_data.getString("jour"));
            setTitre(json_data.getString("titre"));
            setAuteur(json_data.getString("auteur"));
            setHeure(json_data.getInt("heure"));
            setDuree(json_data.getInt("duree"));
            setType(json_data.getString("nom_type"));
            setGroupe(json_data.getString("groupe"));
            setLieu(json_data.getString("lieu"));
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }
}