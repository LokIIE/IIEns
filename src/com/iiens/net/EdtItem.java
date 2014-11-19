package com.iiens.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** EdtItem
	Classe permettant stocker les infos concernant un élément de l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class EdtItem {
	private String jour;
	private String titre; // ou intitule événement
	private String auteur; // Cours : prof assurant le cours / Club : club faisant l'événement
	private Integer heure; // Heure de début en quarts d'heure à partir de minuit (ex : 0h30 <=> 2)
	private Integer duree; // Cours : 7 (pour 1h45) / Club : nombre de quart d'heures de l'événement
	private String nom_type; // Cours : T.D., T.P., Cours_td, contrôle, Cours / Club : assoce
	private String groupe; // Cours : groupe du cours si défini / Club : vide
	private String lieu; // Lieu du cours ou de l'événement

	public String getJour() {return jour;}
	public String getTitre() {return titre;}
	public String getAuteur() {return auteur;}
	public Integer getHeure() {return heure;}
	public Integer getDuree() {return duree;}
	public String getType() {return nom_type;}
	public String getGroupe() {return groupe;}
	public String getLieu() {return lieu;}

	private void setJour(String jour) {this.jour = jour;}
	private void setTitre(String titre) {this.titre = titre;}
	private void setAuteur(String auteur) {this.auteur = auteur;}
	private void setHeure(Integer heure) {this.heure = heure;}
	private void setDuree(Integer duree) {this.duree = duree;}
	private void setType(String nom_type) {this.nom_type = nom_type;}
	private void setGroupe(String groupe) {this.groupe = groupe;}
	private void setLieu(String salle) {
		this.lieu = salle;
		if (salle.equals("2")) {this.lieu = "amphi " + salle;}
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
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
	}
}