package com.iiens.net.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Gère la création des tables de la base de données
 */
class DatabaseHelper extends SQLiteOpenHelper {

    /* Table emploi du temps */
    static final String TABLE_EDT = "EDT";
    static final String EDT_ID = "_id";
    static final String EDT_TITRE = "titre";
    static final String EDT_TYPE = "type";
    static final String EDT_HOTE = "hote";
    static final String EDT_LIEU = "lieu";
    static final String EDT_GROUPE = "groupe";
    static final String EDT_HEURE_DEBUT = "heure_debut";
    static final String EDT_HEURE_FIN = "heure_fin";
    static final String EDT_DUREE = "duree";
    static final String EDT_DATE = "date";

    private static final String DATABASE_NAME = "IIEns.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la table edt
    private static final String CREATE_EDT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EDT
                    + "(" + EDT_ID + " integer primary key autoincrement, "
                    + EDT_TITRE + " text not null, "
                    + EDT_TYPE + " text not null, "
                    + EDT_HOTE + " text not null, "
                    + EDT_GROUPE + " text default null, "
                    + EDT_LIEU + " integer not null, "
                    + EDT_DATE + " text not null, "
                    + EDT_HEURE_DEBUT + " hour not null, "
                    + EDT_HEURE_FIN + " hour not null, "
                    + EDT_DUREE + " integer not null"
                    + ");";

    DatabaseHelper ( Context context ) {

        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate ( SQLiteDatabase database ) {

        createDb( database );
        Log.d( "DatabaseHelper", "Création DB" );
    }

    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {

        Log.d( "DatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data" );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_EDT );
        createDb( db );
    }

    private void createDb ( SQLiteDatabase db ) {

        SQLiteDatabase database = (db == null) ? super.getWritableDatabase() : db;
        database.execSQL( CREATE_EDT );
    }

}
