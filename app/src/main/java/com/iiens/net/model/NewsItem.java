package com.iiens.net.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "news")
public class NewsItem extends HomeItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String titre;
    private String contenu;
    private String auteur;
    private String datePublication;
    private String dateEvent;

    public int getId () { return this.id; }

    public void setId ( int id ) { this.id = id; }

    public String getTitre () { return this.titre; }

    public void setTitre ( String titre ) { this.titre = titre; }

    public String getContenu () { return this.contenu; }

    public void setContenu ( String contenu ) { this.contenu = contenu; }

    public String getAuteur () { return this.auteur; }

    public void setAuteur ( String auteur ) { this.auteur = auteur; }

    public String getDatePublication () { return this.datePublication; }

    public void setDatePublication ( String datePublication ) { this.datePublication = datePublication; }

    public String getDateEvent () { return this.dateEvent; }

    public void setDateEvent ( String dateEvent ) { this.dateEvent = dateEvent; }

    public static NewsItem from ( JSONObject json_data ) {

        NewsItem item = new NewsItem();
        try {

            item.setTitre( json_data.getString( "titre" ) );
            item.setContenu( item.toHtml(json_data.getString( "contenu" ) ) );
            item.setAuteur( json_data.getString( "par" ) );
            item.setDatePublication( json_data.getString( "poste" ) );
            item.setDateEvent( json_data.getString( "calDate" ) );

        } catch ( JSONException e ) {

            Log.e( "NewsItem", "Error parsing data " + e.toString() );
        }

        return item;
    }

    public JSONObject toJsonObject () {

        JSONObject jObject = new JSONObject();

        try {

            jObject.put( "titre", titre );
            jObject.put( "contenu", contenu );
            jObject.put( "par", auteur );
            jObject.put( "poste", datePublication );
            jObject.put( "calDate", dateEvent );

        } catch ( JSONException e ) {

            Log.e( "NewsItem", "Error parsing data " + e.toString() );
        }

        return jObject;
    }

    protected String toHtml ( String contenu ) {

        String formatContenu = contenu;

        String pRed = "(?i)(:red.*?:)(.+?)(:red:)";
        formatContenu = formatContenu.replaceAll( pRed, "<font color=\"red\">" + "$2" + "</font>" );
        String pBlue = "(?i)(:blue.*?:)(.+?)(:blue:)";
        formatContenu = formatContenu.replaceAll( pBlue, "<font color=\"blue\">" + "$2" + "</font>" );
        String pGreen = "(?i)(:green.*?:)(.+?)(:green:)";
        formatContenu = formatContenu.replaceAll( pGreen, "<font color=\"green\">" + "$2" + "</font>" );
        String pOrange = "(?i)(:orange.*?:)(.+?)(:orange:)";
        formatContenu = formatContenu.replaceAll( pOrange, "<font color=\"orange\">" + "$2" + "</font>" );
        String pPurple = "(?i)(:purple.*?:)(.+?)(:purple:)";
        formatContenu = formatContenu.replaceAll( pPurple, "<font color=\"purple\">" + "$2" + "</font>" );

        String pUnderlined = "(?i)(:underlined.*?:)(.+?)(:underlined:)";
        String pBold = "(?i)(:bold.*?:)(.+?)(:bold:)";
        String pItalic = "(?i):italic:(.+?):italic:";
        formatContenu = formatContenu.replaceAll( pUnderlined, "<u>" + "$2" + "</u>" );
        formatContenu = formatContenu.replaceAll( pBold, "<b>" + "$2" + "</b>" );
        formatContenu = formatContenu.replaceAll( pItalic, "<i>" + "$1" + "</i>" );

        String pUrlLinkText = "(?i)(:urllink.*?:)(.+?)(:urllink:)(:urltext*?:)(.+?)(:urltext:)";
        String pUrl = "(?i)(:url.*?:)(.+?)(:url:)";
        formatContenu = formatContenu.replaceAll( pUrlLinkText, "<a href =\"" + "$2" + "\">" + "$5" + "</a>" );
        formatContenu = formatContenu.replaceAll( pUrl, "<a href =\"" + "$2" + "\">Ici</a>" );

        return formatContenu;
    }

    public String getItemContent () {

        return "<strong>" + this.getTitre() + "</strong>";
    }

    public String getItemIcon () {

        return this.getAuteur();
    }

    public String getCompareDate () {

        return this.getDatePublication();
    }
}