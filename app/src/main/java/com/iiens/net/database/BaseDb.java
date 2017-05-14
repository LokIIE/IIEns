package com.iiens.net.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Classe abstraite de gestion des tables
 */
public abstract class BaseDb<T> {
    /**
     * Connexion � la base de donn�es
     */
    protected SQLiteDatabase database;

    /**
     * Param�tres de la base de donn�es
     */
    protected DatabaseHelper dbHelper;

    /**
     * Nom de la table
     */
    protected String tableName;

    /**
     * Liste des noms des colonnes de la table
     * La premi�re colonne de la liste est la colonne ID de la table
     */
    protected String[] tableColumns;

    /**
     * Constructeur de la classe BaseDb
     * @param context contexte
     * @param tableName nom de la table
     */
    public BaseDb ( Context context, String tableName ) {

        this.dbHelper = new DatabaseHelper( context );
        this.tableName = tableName;
    }

    /**
     * Ouverture de la connexion � la base de donn�es
     */
    public void open () {

        this.database = dbHelper.getWritableDatabase();
    }

    /**
     * Fermeture de la connexion � la base de donn�es
     */
    public void close () {

        this.database.close();
    }

    /**
     * R�f�rence de la connexion � la base de donn�es
     * @return R�f�rence de la connexion � la base de donn�es
     */
    public SQLiteDatabase getDbConnexion () {

        return database;
    }

    /**
     * R�cup�ration d'un item � partir de son id
     * @param id Id de l'item � r�cup�rer
     * @return Mod�le d'objet aliment�
     */
    public T getItem ( long id ) {

        T result = null;

        // Connexion � la base de donn�es et ex�cution de la requ�te
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                tableColumns[0] + " = " + id,
                null,
                null,
                null,
                null
        );

        // Lecture des r�sultats
        cursor.moveToFirst();
        if ( cursor.getCount() > 0 ) {

            result = readCursor( cursor );
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    /**
     * R�cup�ration du premier objet de la table
     * @return Mod�le d'objet aliment�
     */
    public T getFirstItem () {

        T result = null;

        // Connexion � la base de donn�es et ex�cution de la requ�te
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                tableColumns[0] + " ASC");

        // Lecture des r�sultats
        if ( cursor != null && cursor.getCount() > 0 ) {

            cursor.moveToFirst();
            result = readCursor( cursor );
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    /**
     * R�cup�ration du dernier objet de la table
     * @return Mod�le d'objet aliment�
     */
    public T getLastItem () {

        T result = null;

        // Connexion � la base de donn�es et ex�cution de la requ�te
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                tableColumns[0] + " DESC");

        // Lecture des r�sultats
        if ( cursor.getCount() > 0 ) {

            cursor.moveToFirst();
            result = readCursor( cursor );
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    /**
     * R�cup�ration des tous les items d'une table
     * @return Liste des objets de la table
     */
    public ArrayList<T> getAllItems () {

        ArrayList<T> itemArrayList = new ArrayList<>();

        // Connexion � la base de donn�es et ex�cution de la requ�te
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des r�sultats
        cursor.moveToFirst();
        while ( !cursor.isAfterLast() ) {

            T item = readCursor(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return itemArrayList;
    }

    /**
     * Lecture de l'emplacement du curseur
     * @param cursor Curseur de la requ�te
     */
    public abstract T readCursor ( Cursor cursor );

    /**
     * R�cup�ration de l'id d'un item
     * @param item Item � rechercher
     */
    public abstract long findItemId ( T item );

    /**
     * Insertion d'un item
     * @param item Item � cr�er
     */
    public abstract boolean createItem ( T item );

    /**
     * Mise � jour d'un item
     * @param item Item mis � jour
     */
    public abstract boolean updateItem ( T item );

    /**
     * Suppression d'un item
     * @param id Id de l'objet � supprimer
     */
    public void deleteItem ( long id ) {

        this.open();
        database.delete(
                tableName,
                tableColumns[0] + " = " + id,
                null);
        this.close();
        System.out.println( "Item avec l'id: " + id + " supprim� de la table " + tableName );
    }

    /**
     * Suppression des valeurs inutiles de la tables
     */
    public abstract void cleanTable ();

    /**
     * Suppression de toutes les valeurs de la table
     */
    public void deleteAll () {

        this.open();
        database.delete(
                tableName,
                null,
                null
        );
        this.close();
        System.out.println( "Tous les objets de la table : " + tableName + " ont �t� supprim�s" );
    }
}
