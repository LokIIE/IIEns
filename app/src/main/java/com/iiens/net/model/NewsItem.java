package com.iiens.net.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NewsItem
 * Modèle d'une news
 */

public class NewsItem {
    public long id;
    private String titre = "";
    private String contenu = "";
    private String auteur = "";
    private String datePublication = "";

    public long getId() { return this.id; }

    public void setId(long id) { this.id = id; }

    private String dateEvent = "";

    public String getTitre() { return this.titre; }

    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return this.contenu; }

    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getAuteur() { return this.auteur; }

    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getDatePublication() { return this.datePublication; }

    public void setDatePublication(String datePublication) { this.datePublication = datePublication; }

    public String getDateEvent() { return this.dateEvent; }

    public void setDateEvent(String dateEvent) { this.dateEvent = dateEvent; }

    public void fromJsonObject(JSONObject json_data) {
        try {
            setTitre(json_data.getString("titre"));
            setContenu(toHtml(json_data.getString("contenu")));
            setAuteur(json_data.getString("par"));
            setDatePublication(json_data.getString("calDate"));
        } catch (JSONException e) {
            Log.e("newsitem_tag", "Error parsing data " + e.toString());
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jObject = new JSONObject();

        try {
            jObject.put("titre", titre);
            jObject.put("contenu", contenu);
            jObject.put("par", auteur);
            jObject.put("calDate", datePublication);
        } catch (JSONException e) {
            Log.e("newsitem_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }

    private String toHtml(String contenu) {
        String formatContenu = contenu;

        String pRed = "(?i)(:red.*?:)(.+?)(:red:)";
        formatContenu = formatContenu.replaceAll(pRed, "<font color=\"red\">" + "$2" + "</font>");
        String pBlue = "(?i)(:blue.*?:)(.+?)(:blue:)";
        formatContenu = formatContenu.replaceAll(pBlue, "<font color=\"blue\">" + "$2" + "</font>");
        String pGreen = "(?i)(:green.*?:)(.+?)(:green:)";
        formatContenu = formatContenu.replaceAll(pGreen, "<font color=\"green\">" + "$2" + "</font>");
        String pOrange = "(?i)(:orange.*?:)(.+?)(:orange:)";
        formatContenu = formatContenu.replaceAll(pOrange, "<font color=\"orange\">" + "$2" + "</font>");
        String pPurple = "(?i)(:purple.*?:)(.+?)(:purple:)";
        formatContenu = formatContenu.replaceAll(pPurple, "<font color=\"purple\">" + "$2" + "</font>");

        String pUnderlined = "(?i)(:underlined.*?:)(.+?)(:underlined:)";
        String pBold = "(?i)(:bold.*?:)(.+?)(:bold:)";
        String pItalic = "(?i)(:italic.*?:)(.+?)(:italic:)";
        formatContenu = formatContenu.replaceAll(pUnderlined, "<u>" + "$2" + "</u>");
        formatContenu = formatContenu.replaceAll(pBold, "<b>" + "$2" + "</b>");
        formatContenu = formatContenu.replaceAll(pItalic, "<i>" + "$2" + "</i>");

        String pUrlLinkText = "(?i)(:urllink.*?:)(.+?)(:urllink:)(:urltext*?:)(.+?)(:urltext:)";
        String pUrl = "(?i)(:url.*?:)(.+?)(:url:)";
        formatContenu = formatContenu.replaceAll(pUrlLinkText, "<a href =\"" + "$2" + "\">" + "$5" + "</a>");
        formatContenu = formatContenu.replaceAll(pUrl, "<a href =\"" + "$2" + "\">Ici</a>");

        return formatContenu;
    }
}