package com.iiens.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;


/** EdtItem
	Classe permettant stocker les infos concernant un élément de l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiItem {
	private String nom = "";
	private String promo = "";
	private Bitmap photo = null;
	private String photoURL = "";
	private String origine = "";
	private String filiere = "";
	private String naissance = "";
	private String telFixe = "";
	private String telPortable = "";
	private String mailEnsiie = "";
	private String mailPerso = "";
	private String antenne = "";
	private String groupe = "";
	private String assoces = "";
	private String citation = "";

	public String getNom() {return nom;}
	public String getPromo() {return promo;}
	public Bitmap getPhoto() {return photo;}
	public String getPhotoURL() {return photoURL;}
	public String getOrigine() {return origine;}
	public String getFiliere() {return filiere;}
	public String getNaissance() {return naissance;}
	public String getTelFixe() {return telFixe;}
	public String getTelPortable() {return telPortable;}
	public String getMailEnsiie() {return mailEnsiie;}
	public String getMailPerso() {return mailPerso;}
	public String getAntenne() {return antenne;}
	public String getGroupe() {return groupe;}
	public String getAssoces() {return assoces;}
	public String getCitation() {return citation;}

	public void setNom(String nom) {this.nom = nom;}
	public void setPromo(String promo) {this.promo = promo;}
	public void setPhoto(Bitmap photo) {this.photo = photo;}
	public void setPhotoURL(String photoURL) {this.photoURL = photoURL;}
	public void setOrigine(String origine) {this.origine = origine;}
	public void setFiliere(String filiere) {this.filiere = filiere;}
	public void setNaissance(String naissance) {this.naissance = naissance;}
	public void setTelFixe(String telFixe) {this.telFixe = telFixe;}
	public void setTelPortable(String telPortable) {this.telPortable = telPortable;}
	public void setMailEnsiie(String mailEnsiie) {this.mailEnsiie = mailEnsiie;}
	public void setMailPerso(String mailPerso) {this.mailPerso= mailPerso;}
	public void setAntenne(String antenne) {this.antenne = antenne;}
	public void setGroupe(String groupe) {this.groupe = groupe;}
	public void setAssoces(String assoces) {this.assoces = assoces;}
	public void setCitation(String citation) {this.citation = citation;}
	
	public JSONObject toJSONObject() {
		JSONObject jObject = new JSONObject();
		
		try {
			jObject.put("nom", getNom());
			jObject.put("promo", getPromo());
			jObject.put("photoURL", getPhotoURL());
			jObject.put("origine", getOrigine());
			jObject.put("filiere", getFiliere());
			jObject.put("naissance", getNaissance());
			jObject.put("tel_fixe", getTelFixe());
			jObject.put("tel_portable", getTelPortable());
			jObject.put("mailIIE", getMailEnsiie());
			jObject.put("mailPerso", getMailPerso());
			jObject.put("antenne", getAntenne());
			jObject.put("groupe", getGroupe());
			jObject.put("assoces", getAssoces());
			jObject.put("citation", getCitation());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jObject;
	}
	
	public TrombiItem fromJSONObject(JSONObject jObject) {
		TrombiItem item = new TrombiItem();
		try {
			setNom(jObject.getString("nom"));
			setPromo(jObject.getString("promo"));
			setPhotoURL(jObject.getString("photoURL"));
			setOrigine(jObject.getString("origine"));
			setFiliere(jObject.getString("filiere"));
			setNaissance(jObject.getString("naissance"));
			setTelFixe(jObject.getString("tel_fixe"));
			setTelPortable(jObject.getString("tel_portable"));
			setMailEnsiie(jObject.getString("mailIIE"));
			setMailPerso(jObject.getString("mailPerso"));
			setAntenne(jObject.getString("antenne"));
			setGroupe(jObject.getString("groupe"));
			setAssoces(jObject.getString("assoces"));
			setCitation(jObject.getString("citation"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return item;
	}

}