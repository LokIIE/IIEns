package com.iiens.net.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "anniversaires")
public class AnnivItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nom;
    private String prenom;
    private String pseudo;
    private String date;
    private String age;

    public void setId ( int id ) {
        this.id = id;
    }

    public int getId () {
        return id;
    }

    public String getNom () {
        return nom;
    }

    public void setNom ( String nom ) {
        this.nom = nom;
    }

    public String getPrenom () {
        return prenom;
    }

    public void setPrenom ( String prenom ) {
        this.prenom = prenom;
    }

    public String getPseudo () {
        return pseudo;
    }

    public void setPseudo ( String pseudo ) {
        this.pseudo = pseudo;
    }

    public String getDate () {
        return date;
    }

    public void setDate ( String date ) {
        this.date = date;
    }

    public String getAge () {
        return age;
    }

    public void setAge ( String age ) {
        this.age = age;
    }

    public AnnivItem () {}

    public static AnnivItem load ( JSONObject data ) {

        AnnivItem item = new AnnivItem();
        try {

            item.nom = data.getString( "nom" );
            item.prenom = data.getString( "prenom" );
            item.pseudo = data.getString( "surnom" );
            item.date = data.getString( "anniv" );
            item.age = data.getString( "age" );

        } catch (JSONException e) {

            Log.e( "log_tag", "Error parsing data " + e.toString() );
        }

        return item;
    }
}