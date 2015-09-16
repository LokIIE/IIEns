package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.NewsItem;

import java.util.ArrayList;

/**
 * Gestion de la table News dans la bdd
 */
public class NewsDb extends BaseDb<NewsItem> {
    // Champs de la base de donn�es
    private String[] allColumns = {
            DatabaseHelper.NEWS_ID,
            DatabaseHelper.NEWS_TITRE,
            DatabaseHelper.NEWS_CONTENU,
            DatabaseHelper.NEWS_AUTEUR,
            DatabaseHelper.NEWS_DATE_EVENT,
            DatabaseHelper.NEWS_DATE_PUBLICATION};

    public NewsDb(Context context) {
        super(context, DatabaseHelper.TABLE_NEWS);
    }

    public NewsItem readCursor(Cursor cursor) {
        NewsItem item = new NewsItem();
        item.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.NEWS_ID)));
        item.setTitre(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEWS_TITRE)));
        item.setAuteur(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEWS_AUTEUR)));
        item.setContenu(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEWS_CONTENU)));
        item.setDatePublication(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEWS_DATE_PUBLICATION)));
        item.setDateEvent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NEWS_DATE_EVENT)));
        return item;
    }

    @Override
    public long findItemId(NewsItem item) {
        long result = 0;

        // Connexion a la base de donnees et execution de la requete
        this.open();
        // TODO : erreur dans l'echappement des caract�res
        Cursor cursor = database.query(
                tableName,
                tableColumns,
                DatabaseHelper.NEWS_TITRE + " = '" + item.getTitre() + "'"
                        + " AND " + DatabaseHelper.NEWS_AUTEUR + " = '" + item.getAuteur() + "'"
                        + " AND " + DatabaseHelper.NEWS_DATE_PUBLICATION + " = '" + item.getDatePublication() + "'",
                null,
                null,
                null,
                null);

        // Lecture des resultats
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor).getId();
        }

        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    public ArrayList<NewsItem> getAllItems() {
        ArrayList<NewsItem> itemArrayList = new ArrayList<>();

        // Connexion a la base de donnees et execution de la requete
        this.open();
        // Ex�cution de la requ�te
        Cursor cursor = database.query(DatabaseHelper.TABLE_NEWS,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des r�sultats
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NewsItem item = readCursor(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }
        // Fermeture de la connexion
        cursor.close();
        this.close();

        return itemArrayList;
    }

    @Override
    public NewsItem getItem(long id) {
        return null;
    }

    @Override
    public NewsItem getFirstItem() {
        NewsItem result = null;

        // Connexion a la base de donnees et execution de la requete
        this.open();
        // Ex�cution de la requ�te
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_NEWS,
                allColumns,
                null,
                null,
                null,
                null,
                DatabaseHelper.NEWS_ID + " ASC");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor);
        }
        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    @Override
    public boolean createItem(NewsItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Param�tres de la requ�te
        values.put(DatabaseHelper.NEWS_TITRE, item.getTitre());
        values.put(DatabaseHelper.NEWS_CONTENU, item.getContenu());
        values.put(DatabaseHelper.NEWS_AUTEUR, item.getAuteur());
        values.put(DatabaseHelper.NEWS_DATE_PUBLICATION, item.getDatePublication());
        values.put(DatabaseHelper.NEWS_DATE_EVENT, "");

        // Connexion � la base de donn�es et ex�cution de la requ�te
        this.open();
        // Insertion en base
        insertId = database.insert(DatabaseHelper.TABLE_NEWS, null,
                values);
        // Fermeture de la connexion
        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem(NewsItem item) {
        return false;
    }

    @Override
    public void cleanTable() {

    }
}