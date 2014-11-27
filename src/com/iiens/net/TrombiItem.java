package com.iiens.net;

import android.graphics.Bitmap;


/** EdtItem
	Classe permettant stocker les infos concernant un élément de l'edt
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class TrombiItem {
	private String nom;
	private String promo;
	private Bitmap photo;
	private String origine;
	private String filiere;
	private String naissance;
	private String telFixe;
	private String telPortable;
	private String mailEnsiie;
	private String mailPerso;
	private String antenne;
	private String groupe;
	private String[] clubPres;
	private String[] clubAdmin;
	private String[] clubMembre;
	private String citation;

	public String getNom() {return nom;}
	public String getPromo() {return promo;}
	public Bitmap getPhoto() {return photo;}
	public String getOrigine() {return origine;}
	public String getFiliere() {return filiere;}
	public String getNaissance() {return naissance;}
	public String getTelFixe() {return telFixe;}
	public String getTelPortable() {return telPortable;}
	public String getMailEnsiie() {return mailEnsiie;}
	public String getMailPerso() {return mailPerso;}
	public String getAntenne() {return antenne;}
	public String getGroupe() {return groupe;}
	public String[] getClubPres() {return clubPres;}
	public String[] getClubAdmin() {return clubAdmin;}
	public String[] getClubMembre() {return clubMembre;}
	public String getCitation() {return citation;}

	public void setNom(String nom) {this.nom = nom;}
	public void setPromo(String promo) {this.promo = promo;}
	public void setPhoto(Bitmap photo) {this.photo = photo;}
	public void setOrigine(String origine) {this.origine = origine;}
	public void setFiliere(String filiere) {this.filiere = filiere;}
	public void setNaissance(String naissance) {this.naissance = naissance;}
	public void setTelFixe(String telFixe) {this.telFixe = telFixe;}
	public void setTelPortable(String telPortable) {this.telPortable = telPortable;}
	public void setMailEnsiie(String mailEnsiie) {this.mailEnsiie = mailEnsiie;}
	public void setMailPerso(String mailPerso) {this.mailPerso= mailPerso;}
	public void setAntenne(String antenne) {this.antenne = antenne;}
	public void setGroupe(String groupe) {this.groupe = groupe;}
	public void setClubPres(String[] clubPres) {this.clubPres = clubPres;}
	public void setClubAdmin(String[] clubAdmin) {this.clubAdmin = clubAdmin;}
	public void setClubMembre(String[] clubMembre) {this.clubMembre = clubMembre;}
	public void setCitation(String citation) {this.citation = citation;}

}