package com.iiens.net;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrombiItem {
	private String nom;
	private String prenom;
	private String pseudo;
	private String promo;
	private URL photo;
	private String etablissement;
	private String filiere;
	private String dateNaissance;
	private String telephone;
	private String portable;
	private String adresse;
	private String mailIIE;
	private String mailPerso;
	private String antenne;
	private String groupe;
	private List<String> president = new ArrayList<String>();
	private List<String> admin = new ArrayList<String>();
	private List<String> membre = new ArrayList<String>();
	private String citation;

	public String getNom(){return nom;}	
	public String getPrenom(){return prenom;}
	public String getPseudo(){return pseudo;}
	public String getPromo(){return promo;}
	public URL getPhoto(){return photo;}
	public String getEtablissement(){return etablissement;}
	public String getFiliere(){return filiere;}
	public String getNaissance(){return dateNaissance;}
	public String getTelephone(){return telephone;}
	public String getPortable(){return portable;}
	public String getAdresse(){return adresse;}
	public String getMailIIE(){return mailIIE;}
	public String getMailPerso(){return mailPerso;}
	public String getAntenne(){return antenne;}
	public String getGroupe(){return groupe;}
	public List<String> getPresident(){return president;}
	public List<String> getAdmin(){return admin;}
	public List<String> getMembre(){return membre;}
	public String getCitation(){return citation;}

	private void setNom(String nom) {this.nom = nom;}	
	private void setPrenom(String prenom) {this.prenom = prenom;}
	private void setPseudo(String pseudo) {this.pseudo = pseudo;}
	private void setPromo(String promo) {this.promo = promo;}
	private void setPhoto(URL photo) {this.photo = photo;}
	private void setEtablissement(String etablissement) {this.etablissement = etablissement;}
	private void setFiliere(String filiere) {this.filiere = filiere;}
	private void setNaissance(String dateNaissance) {this.dateNaissance = dateNaissance;}
	private void setTelephone(String telephone) {this.telephone = telephone;}
	private void setPortable(String portable) {this.portable = portable;}
	private void setAdresse(String adresse) {this.adresse = adresse;}
	private void setMailIIE(String mailIIE) {this.mailIIE = mailIIE;}
	private void setMailPerso(String mailPerso) {this.mailPerso = mailPerso;}
	private void setAntenne(String antenne) {this.antenne = antenne;}
	private void setGroupe(String groupe) {this.groupe = groupe;}
	private void setPresident(List<String> president) {this.president = president;}
	private void setAdmin(List<String> admin) {this.admin = admin;}
	private void setMembre(List<String> membre) {this.membre = membre;}
	private void setCitation(String citation) {this.citation = citation;}
}
