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
    public static final String TABLE_ANNIVERSAIRES = "ANNIVERSAIRES";
    public static final String ANNIV_ID = "_id";
    public static final String ANNIV_NOM = "nom";
    public static final String ANNIV_PRENOM = "prenom";
    public static final String ANNIV_SURNOM = "surnom";
    public static final String ANNIV_AGE = "age";
    public static final String ANNIV_DATE = "date";

    /* Table emploi du temps */
    public static final String TABLE_EDT = "EDT";
    public static final String EDT_ID = "_id";
    public static final String EDT_TITRE = "titre";
    public static final String EDT_TYPE = "type";
    public static final String EDT_HOTE = "hote";
    public static final String EDT_LIEU = "lieu";
    public static final String EDT_GROUPE = "groupe";
    public static final String EDT_HEURE_DEBUT = "heure_debut";
    public static final String EDT_HEURE_FIN = "heure_fin";
    public static final String EDT_DUREE = "duree";
    public static final String EDT_DATE = "date";

    /* Table formulaire emploi du temps */
    public static final String TABLE_EDTFORM = "EDTFORM";
    public static final String EDTFORM_ID = "_id";
    public static final String EDTFORM_NAME = "nom";
    public static final String EDTFORM_PROMO = "promo";

    /* Table options de l'emploi du temps */
    public static final String TABLE_EDTOPT = "EDTOPT";
    public static final String EDTOPT_ID = "_id";
    public static final String EDTOPT_NAME = "nom";
    public static final String EDTOPT_CODE = "code";
    public static final String FK_EDTFORM = "fk_edtForm";

    /* Table news */
    public static final String TABLE_NEWS = "NEWS";
    public static final String NEWS_ID = "_id";
    public static final String NEWS_TITRE = "titre";
    public static final String NEWS_CONTENU = "contenu";
    public static final String NEWS_AUTEUR = "auteur";
    public static final String NEWS_DATE_EVENT = "date_event";
    public static final String NEWS_DATE_PUBLICATION = "date_publication";

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

    // Commande sql pour la création de la table edtOpt
    private static final String CREATE_EDTOPT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EDTOPT
                    + "(" + EDTOPT_ID + " integer primary key autoincrement, "
                    + EDTOPT_NAME + " text not null, "
                    + EDTOPT_CODE + " text not null, "
                    + FK_EDTFORM + " integer not null"
                    + ");";

    // Commande sql pour la création de la table edtForm
    private static final String CREATE_EDTFORM =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EDTFORM
                    + "(" + EDTFORM_ID + " integer primary key autoincrement, "
                    + EDTFORM_NAME + " text not null, "
                    + EDTFORM_PROMO + " integer not null"
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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createDb(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(this.getClass().getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNIVERSAIRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDTOPT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDTFORM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        this.onCreate(db);
    }

    public void createDb(SQLiteDatabase db) {
        SQLiteDatabase database = (db == null) ? super.getWritableDatabase() : db;
        database.execSQL(CREATE_ANNIV);
        database.execSQL(CREATE_EDTFORM);
        database.execSQL(CREATE_EDTOPT);
        database.execSQL(CREATE_EDT);
        database.execSQL(CREATE_NEWS);
    }

}
