package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.AnnivItem;

/**
 * Gestion de la table des anniversaires dans la bdd
 */
public class AnnivDb extends BaseDb<AnnivItem>{
    // Champs de la base de données
    protected String[] allColumns = {
            DatabaseHelper.ANNIV_ID,
            DatabaseHelper.ANNIV_NOM,
            DatabaseHelper.ANNIV_PRENOM,
            DatabaseHelper.ANNIV_SURNOM,
            DatabaseHelper.ANNIV_AGE,
            DatabaseHelper.ANNIV_DATE};

    public AnnivDb(Context context) {
        super(context, DatabaseHelper.TABLE_ANNIVERSAIRES);
        super.tableColumns = allColumns;
    }

    @Override
    public AnnivItem readCursor(Cursor cursor) {
        AnnivItem item = new AnnivItem();
        item.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ANNIV_ID)));
        item.setNom(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNIV_NOM)));
        item.setPrenom(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNIV_PRENOM)));
        item.setPseudo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNIV_SURNOM)));
        item.setAge(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNIV_AGE)));
        item.setAnniv(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNIV_DATE)));
        return item;
    }

    @Override
    public long findItemId(AnnivItem item) {
        long result = 0;

        // Connexion à la base de données et exécution de la requête
        this.open();
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                DatabaseHelper.ANNIV_NOM + " = " + item.getNom()
                + " AND " + DatabaseHelper.ANNIV_PRENOM + " = " + item.getPrenom()
                + " AND " + DatabaseHelper.ANNIV_SURNOM + " = " + item.getPseudo(),
                null,
                null,
                null,
                null);

        // Lecture des résultats
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor).getId();
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    @Override
    public boolean createItem(AnnivItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Paramètres requête
        values.put(DatabaseHelper.ANNIV_NOM, item.getNom());
        values.put(DatabaseHelper.ANNIV_PRENOM, item.getPrenom());
        values.put(DatabaseHelper.ANNIV_SURNOM, item.getPseudo());
        values.put(DatabaseHelper.ANNIV_AGE, item.getAge());
        values.put(DatabaseHelper.ANNIV_DATE, item.getAnniv());

        // Connexion à la base de données et exécution de la requête
        this.open();
        insertId = database.insert(
                tableName,
                null,
                values);

        // Fermeture de la connexion
        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem(AnnivItem item) {
        return false;
    }

    @Override
    public void cleanTable() {
        open();
        database.delete(
                tableName,
                DatabaseHelper.ANNIV_DATE + " < DATE(CURRENT_DATE)",
                null);
        close();
        System.out.println("Table: " + tableName + " nettoyée");
    }
}
