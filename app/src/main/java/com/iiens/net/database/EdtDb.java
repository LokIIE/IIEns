package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.EdtItem;

import java.util.ArrayList;

/**
 * Gestion de la table Edt dans la bdd
 */
public class EdtDb extends BaseDb<EdtItem>{
    // Champs de la base de données
    private String[] allColumns = {
            DatabaseHelper.EDT_ID,
            DatabaseHelper.EDT_TITRE,
            DatabaseHelper.EDT_TYPE,
            DatabaseHelper.EDT_HOTE,
            DatabaseHelper.EDT_LIEU,
            DatabaseHelper.EDT_GROUPE,
            DatabaseHelper.EDT_DATE,
            DatabaseHelper.EDT_HEURE_DEBUT,
            DatabaseHelper.EDT_HEURE_FIN,
            DatabaseHelper.EDT_DUREE};

    public EdtDb(Context context) {
        super(context);
    }

    public EdtItem cursorToItem(Cursor cursor) {
        EdtItem item = new EdtItem();
        return item;
    }

    public ArrayList<EdtItem> getAllItems() {
        ArrayList<EdtItem> itemArrayList = new ArrayList<>();

        // Exécution de la requête
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_EDT,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des résultats
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EdtItem item = cursorToItem(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return itemArrayList;
    }

    public boolean createItem(EdtItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Paramètres de la requête
        values.put(DatabaseHelper.EDT_TITRE, item.getTitre());
        values.put(DatabaseHelper.EDT_TYPE, item.getType());
        values.put(DatabaseHelper.EDT_HOTE, item.getAuteur());
        values.put(DatabaseHelper.EDT_GROUPE, item.getGroupe());
        values.put(DatabaseHelper.EDT_LIEU, item.getLieu());
        values.put(DatabaseHelper.EDT_DATE, item.getJour());
        values.put(DatabaseHelper.EDT_HEURE_DEBUT, item.getHeure());
        values.put(DatabaseHelper.EDT_HEURE_FIN, item.getHeure());
        values.put(DatabaseHelper.EDT_DUREE, item.getDuree());

        // Insertion en base
        insertId = database.insert(
                DatabaseHelper.TABLE_EDT,
                null,
                values);

        return insertId > 0;
    }

    public void deleteItem(long id) {
        database.delete(
                DatabaseHelper.TABLE_EDT,
                DatabaseHelper.EDT_ID + " = " + id,
                null);
        System.out.println("Item avec l'id: " + id + " supprimé de la table " + DatabaseHelper.TABLE_EDT);
    }
}
