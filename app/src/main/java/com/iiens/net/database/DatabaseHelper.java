package com.iiens.net.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Gère la création des tables de la base de données
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /* Table anniversaires */
    public static final String TABLE_ANNIVERSAIRES = "anniversaires";
    public static final String ANNIV_ID = "_id";
    public static final String ANNIV_NOM = "nom";
    public static final String ANNIV_PRENOM = "prenom";
    public static final String ANNIV_SURNOM = "surnom";
    public static final String ANNIV_AGE = "age";
    public static final String ANNIV_DATE = "date";

    /* Table emploi du temps */
    public static final String TABLE_EDT = "anniversaires";
    public static final String EDT_ID = "_id";
    public static final String EDT_TITRE = "titre";
    public static final String EDT_TYPE = "type";
    public static final String EDT_HOTE = "hôte";
    public static final String EDT_LIEU = "lieu";
    public static final String EDT_GROUPE = "groupe";
    public static final String EDT_HEURE_DEBUT = "heure_debut";
    public static final String EDT_HEURE_FIN = "heure_fin";
    public static final String EDT_DUREE = "duree";
    public static final String EDT_DATE = "date";

    /* Table news */
    public static final String TABLE_NEWS = "news";
    public static final String NEWS_ID = "_id";
    public static final String NEWS_TITRE = "titre";
    public static final String NEWS_CONTENU = "contenu";
    public static final String NEWS_AUTEUR = "auteur";
    public static final String NEWS_DATE_EVENT = "date_event";
    public static final String NEWS_DATE_PUBLICATION = "date_publication";

    /* Table twitter */
//    public static final String TABLE_TWITTER = "twitter";
//    public static final String TWITTER_ID = "_id";
//    public static final String TWITTER_USERNAME = "username";
//    public static final String TWITTER_NOM = "nom";
//    public static final String TWITTER_DATE_PUBLICATION = "date_publication";
//    public static final String TWITTER_CONTENU = "contenu";
//    public static final String TWITTER_PROFILE_PICTURE_URL = "profile_picture_url";

    private static final String DATABASE_NAME = "IIEns.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la table anniv
    private static final String CREATE_ANNIV =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ANNIVERSAIRES
                    + "(" + ANNIV_ID + " integer primary key autoincrement, "
                    + ANNIV_NOM + " text not null, "
                    + ANNIV_PRENOM + " text not null, "
                    + ANNIV_SURNOM + " text null, "
                    + ANNIV_AGE + " integer not null, "
                    + ANNIV_DATE + " text not null"
                    + ");";

    // Commande sql pour la création d'un trigger sur la table anniv
    private static final String CREATE_TRIGGER_ANNIV =
            "CREATE TRIGGER IF NOT EXISTS remove_anniversaires_passes "
                    + "BEFORE INSERT ON " + TABLE_ANNIVERSAIRES
                    + " BEGIN"
                        + " DELETE FROM " + TABLE_ANNIVERSAIRES
                        + " WHERE " + ANNIV_ID + " IN ("
                        + " SELECT " + ANNIV_ID + " FROM " + TABLE_ANNIVERSAIRES
                        + " WHERE " + ANNIV_DATE + " < GETDATE());"
                    + " END";

    // Commande sql pour la création de la table edt
    private static final String CREATE_EDT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EDT
                    + "(" + EDT_ID + " integer primary key autoincrement, "
                    + EDT_TITRE + " text not null, "
                    + EDT_TYPE + " text not null, "
                    + EDT_HOTE + " text not null, "
                    + EDT_GROUPE + " text null, "
                    + EDT_LIEU + " integer not null, "
                    + EDT_DATE + " text not null, "
                    + EDT_HEURE_DEBUT + " hour not null, "
                    + EDT_HEURE_FIN + " hour not null, "
                    + EDT_DUREE + " integer not null"
                    + ");";

    // Commande sql pour la création de la table news
    private static final String CREATE_NEWS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NEWS
                    + "(" + NEWS_ID + " integer primary key autoincrement, "
                    + NEWS_TITRE + " text not null, "
                    + NEWS_CONTENU + " text not null, "
                    + NEWS_AUTEUR + " text null, "
                    + NEWS_DATE_EVENT + " text not null, "
                    + NEWS_DATE_PUBLICATION + " text not null"
                    + ");";

    // Commande sql pour la création de la table twitter
//    private static final String CREATE_TWITTER =
//            "CREATE TABLE IF NOT EXISTS " + TABLE_TWITTER
//                    + "(" + TWITTER_ID + " integer primary key autoincrement, "
//                    + TWITTER_USERNAME + " text not null, "
//                    + TWITTER_NOM + " text not null, "
//                    + TWITTER_DATE_PUBLICATION + " text not null, "
//                    + TWITTER_CONTENU + " text not null, "
//                    + TWITTER_PROFILE_PICTURE_URL + " text not null"
//                    + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_ANNIV);
        // database.execSQL(CREATE_TRIGGER_ANNIV);
        database.execSQL(CREATE_EDT);
        database.execSQL(CREATE_NEWS);
//        database.execSQL(CREATE_TWITTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNIVERSAIRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWITTER);
        onCreate(db);
    }
}
