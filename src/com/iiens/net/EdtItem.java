package com.iiens.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class EdtItem {
	private String[] sallesInfo = {"130", "131", "225", "227", "228", "233", "251", "253", "258", "269"};

	private String nom_matiere;
	private String nom_prof;
	private Integer heure;
	private String nom_type;
	private String groupe;
	private String salle;

	public String getMatiere() {return nom_matiere;}
	public String getProf() {return nom_prof;}
	public Integer getHeure() {return heure;}
	public String getType() {return nom_type;}
	public String getGroupe() {return groupe;}
	public String getSalle() {return salle;}

	private void setMatiere(String nom_matiere) {this.nom_matiere = nom_matiere;}
	private void setProf(String nom_prof) {this.nom_prof = nom_prof;}
	private void setHeure(Integer heure) {this.heure = heure;}
	private void setType(String nom_type) {this.nom_type = nom_type;}
	private void setGroupe(String groupe) {this.groupe = groupe;}
	private void setSalle(String salle) {
		this.salle = salle;
		if (salle == "2") {this.salle = "amphi " + salle;}
	}

	public void mapJsonObject(JSONObject json_data) {
		try {
			setMatiere(json_data.getString("nom_matiere"));
			setProf(json_data.getString("nom_prof"));
			setHeure(json_data.getInt("heure"));
			setType(json_data.getString("nom_type"));
			setSalle(json_data.getString("salle"));
			setGroupe(json_data.getString("nom_groupe"));
			if (isInList(salle, sallesInfo)) {
				setType("TP");
			}
		} catch(JSONException e){
			Log.e("log_tag", "Error parsing data " + e.toString());
		}
	}

	private boolean isInList (String value, String[] list) {
		if (value == null) return false;

		for (int i=0; i < list.length; i++) {
			if (list[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
}