package com.iiens.net;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** AnnivItem
	Classe permettant de stocker les infos concernant un anniversaire
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class AnnivItem {
	String nom;
	String prenom;
	String pseudo;
	String anniv;
	String age;

	public String getNom() {return nom;}
	public String getPrenom() {return prenom;}
	public String getPseudo() {return pseudo;}
	public String getAnniv() {return anniv;}
	public String getAge() {return age;}

	private void setNom(String nom) {this.nom = nom;}
	private void setPrenom(String prenom) {this.prenom = prenom;}
	private void setPseudo(String pseudo) {this.pseudo = pseudo;}
	private void setAnniv(String anniv) {this.anniv = anniv;}
	private void setAge(String age) {this.age = age;}

	public AnnivItem(){}

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
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
	}

	public ArrayList<String> toArrayList() {
		ArrayList<String> result = new ArrayList<String>();

		result.add(nom);
		result.add(prenom);
		result.add(pseudo);
		result.add(anniv);
		result.add(age);

		return result;
	}
}