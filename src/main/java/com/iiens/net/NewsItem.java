package com.iiens.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * NewsItem
 * Classe stockant les infos pour une news
 * Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 */

public class NewsItem {
    private String title = "";
    private String description = "";
    private String author = "";
    private String publicationDate = "";

    public NewsItem() {
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void fromJsonObject(JSONObject json_data) {
        try {
            setTitle(json_data.getString("titre"));
            String contenu = json_data.getString("contenu");
            setDescription(toHtml(contenu));
            setAuthor(json_data.getString("par"));
            setPublicationDate(json_data.getString("calDate"));
        } catch (JSONException e) {
            Log.e("newsitem_tag", "Error parsing data " + e.toString());
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jObject = new JSONObject();

        try {
            jObject.put("titre", title);
            jObject.put("contenu", description);
            jObject.put("par", author);
            jObject.put("calDate", publicationDate);
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

    public ArrayList<String> toStringArrayList() {
        ArrayList<String> result = new ArrayList<>();

        result.add(title);
        result.add(description);
        result.add(author);
        result.add(publicationDate);

        return result;
    }

    public NewsItem fromStringArrayList(ArrayList<String> sArrayList) {
        this.title = sArrayList.get(0);
        this.description = sArrayList.get(1);
        this.author = sArrayList.get(2);
        this.publicationDate = sArrayList.get(3);

        return this;
    }
}