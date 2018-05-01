package com.iiens.net.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "anniversaires")
public class AnnivItem extends HomeItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nom;
    private String prenom;
    private String pseudo;
    private String anniv;
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

    public String getAnniv () {
        return anniv;
    }

    public void setAnniv ( String anniv ) {
        this.anniv = anniv;
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
            item.anniv = data.getString( "anniv" );
            item.date = data.getString( "date" );
            item.age = data.getString( "age" );

        } catch (JSONException e) {

            Log.e( "log_tag", "Error parsing data " + e.toString() );
        }

        return item;
    }

    public String getItemContent () {

        String res = this.getDate() + " : ";
        res += this.getPrenom() + " " + this.getPseudo() + " " + this.getNom();
        res += " (" + this.getAge().trim() + " ans)";

        return res;
    }

    public String getItemIcon () {

        return "fa_birthday_cake";
    }

    public String getCompareDate () {

        return this.getDate();
    }
}