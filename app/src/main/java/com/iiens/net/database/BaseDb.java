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
     * Connexion à la base de données
     */
    protected SQLiteDatabase database;

    /**
     * Paramètres de la base de données
     */
    protected DatabaseHelper dbHelper;

    /**
     * Nom de la table
     */
    protected String tableName;

    /**
     * Liste des noms des colonnes de la table
     * La première colonne de la liste est la colonne ID de la table
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
     * Ouverture de la connexion à la base de données
     */
    public void open () {

        this.database = dbHelper.getWritableDatabase();
    }

    /**
     * Fermeture de la connexion à la base de données
     */
    public void close () {

        this.database.close();
    }

    /**
     * Référence de la connexion à la base de données
     * @return Référence de la connexion à la base de données
     */
    public SQLiteDatabase getDbConnexion () {

        return database;
    }

    /**
     * Récupération d'un item à partir de son id
     * @param id Id de l'item à récupérer
     * @return Modèle d'objet alimenté
     */
    public T getItem ( long id ) {

        T result = null;

        // Connexion à la base de données et exécution de la requête
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

        // Lecture des résultats
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
     * Récupération du premier objet de la table
     * @return Modèle d'objet alimenté
     */
    public T getFirstItem () {

        T result = null;

        // Connexion à la base de données et exécution de la requête
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                tableColumns[0] + " ASC");

        // Lecture des résultats
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
     * Récupération du dernier objet de la table
     * @return Modèle d'objet alimenté
     */
    public T getLastItem () {

        T result = null;

        // Connexion à la base de données et exécution de la requête
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                tableColumns[0] + " DESC");

        // Lecture des résultats
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
     * Récupération des tous les items d'une table
     * @return Liste des objets de la table
     */
    public ArrayList<T> getAllItems () {

        ArrayList<T> itemArrayList = new ArrayList<>();

        // Connexion à la base de données et exécution de la requête
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des résultats
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
     * @param cursor Curseur de la requête
     */
    public abstract T readCursor ( Cursor cursor );

    /**
     * Récupération de l'id d'un item
     * @param item Item à rechercher
     */
    public abstract long findItemId ( T item );

    /**
     * Insertion d'un item
     * @param item Item à créer
     */
    public abstract boolean createItem ( T item );

    /**
     * Mise à jour d'un item
     * @param item Item mis à jour
     */
    public abstract boolean updateItem ( T item );

    /**
     * Suppression d'un item
     * @param id Id de l'objet à supprimer
     */
    public void deleteItem ( long id ) {

        this.open();
        database.delete(
                tableName,
                tableColumns[0] + " = " + id,
                null);
        this.close();
        System.out.println( "Item avec l'id: " + id + " supprimé de la table " + tableName );
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
        System.out.println( "Tous les objets de la table : " + tableName + " ont été supprimés" );
    }
}
