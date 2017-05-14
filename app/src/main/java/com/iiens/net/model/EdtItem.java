package com.iiens.net.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modèle d'un évènement de l'emploi du temps
 */

public class EdtItem {

    /**
     * Liste des minutes pour la correspondance avec l'heure et la durée
     */
    public static final String[] minutes = {"00", "15", "30", "45"};

    /**
     * Identifiant de l'événement
     */
    private long id;

    /**
     * Jour de l'événement (format AAAA-MM-JJ)
     */
    private String jour;

    /**
     * Intitulé de l'événement
     */
    private String titre;

    /**
     * Personne ou club assurant l'événement
     */
    private String auteur;

    /**
     * Heure de début de l'événement représenté sous forme de quarts d'heure depuis 0h00
     */
    private Integer heure;

    /**
     * Durée en nombre de quarts d'heure
     */
    private Integer duree;

    /**
     * Type d'événement
     */
    private String type;

    /**
     * Groupe concerné par l'événement
     */
    private String groupe;

    /**
     * Lieu de l'événement
     */
    private String lieu;

    public long getId () { return id; }

    public void setId ( long id ) {
        this.id = id;
    }

    public String getJour () {
        return jour;
    }

    private void setJour ( String jour ) {
        this.jour = jour;
    }

    public String getTitre () {
        return titre;
    }

    private void setTitre ( String titre ) {
        this.titre = titre;
    }

    public String getAuteur () {
        return auteur;
    }

    private void setAuteur ( String auteur ) {
        this.auteur = auteur;
    }

    public Integer getHeure () { return heure; }

    private void setHeure ( Integer heure ) {
        this.heure = heure;
    }

    public Integer getDuree () {
        return duree;
    }

    private void setDuree ( Integer duree ) {
        this.duree = duree;
    }

    public String getType () {
        return type;
    }

    private void setType ( String nom_type ) {
        this.type = nom_type;
    }

    public String getGroupe () {
        return groupe;
    }

    private void setGroupe ( String groupe ) {
        this.groupe = groupe;
    }

    public String getLieu () {
        return lieu;
    }

    private void setLieu ( String salle ) {

        this.lieu = salle;
        if ( salle.equals("2") ) this.lieu = "amphi " + salle;
    }

    public static EdtItem mapJsonObject ( JSONObject json_data ) {

        EdtItem item = new EdtItem();

        try {

            item.setJour( json_data.getString("jour") );
            item.setTitre( json_data.getString("titre") );
            item.setAuteur( json_data.getString("auteur") );
            item.setHeure( json_data.getInt("heure") );
            item.setDuree( json_data.getInt("duree") );
            item.setType( json_data.getString("nom_type") );
            item.setGroupe( json_data.getString("groupe") );
            item.setLieu( json_data.getString("lieu") );

        } catch ( JSONException e ) {

            Log.e( "log_tag", "Error parsing data " + e.toString() );
        }

        return item;
    }
    public String getHeureDebut () {

        return String.valueOf( this.heure / 4 ) + "h" + minutes[ this.heure % 4 ];
    }

    public String getHeureFin () {

        return String.valueOf( (this.heure + this.duree) / 4 % 24 ) + "h" + minutes[ (this.heure + this.duree) % 4 ];
    }
}