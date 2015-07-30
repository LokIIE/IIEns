package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.AnnivItem;

import java.util.ArrayList;

/**
 * Gestion de la table Anniv dans la bdd
 */
public class AnnivDb extends BaseDb<AnnivItem>{
    // Champs de la base de données
    private String[] allColumns = {
            DatabaseHelper.ANNIV_ID,
            DatabaseHelper.ANNIV_NOM,
            DatabaseHelper.ANNIV_PRENOM,
            DatabaseHelper.ANNIV_SURNOM,
            DatabaseHelper.ANNIV_AGE,
            DatabaseHelper.ANNIV_DATE};

    public AnnivDb(Context context) {
        super(context);
    }

    public AnnivItem cursorToItem(Cursor cursor) {
        AnnivItem item = new AnnivItem();
        return item;
    }

    public ArrayList<AnnivItem> getAllItems() {
        ArrayList<AnnivItem> itemArrayList = new ArrayList<>();

        // Exécution de la requête
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ANNIVERSAIRES,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des résultats
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AnnivItem item = cursorToItem(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return itemArrayList;
    }

    public boolean createItem(AnnivItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Paramètres requête
        values.put(DatabaseHelper.ANNIV_NOM, item.getNom());
        values.put(DatabaseHelper.ANNIV_PRENOM, item.getPrenom());
        values.put(DatabaseHelper.ANNIV_SURNOM, item.getPseudo());
        values.put(DatabaseHelper.ANNIV_AGE, item.getAge());
        values.put(DatabaseHelper.ANNIV_DATE, item.getAnniv());

        // Insertion en base
        open();
        insertId = database.insert(
                DatabaseHelper.TABLE_ANNIVERSAIRES,
                null,
                values);
        close();

        return insertId > 0;
    }

    public void deleteItem(long id) {
        database.delete(
                DatabaseHelper.TABLE_ANNIVERSAIRES,
                DatabaseHelper.ANNIV_ID + " = " + id,
                null);
        System.out.println("Item avec l'id: " + id + " supprimé de la table " + DatabaseHelper.TABLE_ANNIVERSAIRES);
    }
}
