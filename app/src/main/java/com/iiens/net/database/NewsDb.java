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
    // Champs de la base de données
    private String[] allColumns = {
            DatabaseHelper.NEWS_ID,
            DatabaseHelper.NEWS_TITRE,
            DatabaseHelper.NEWS_CONTENU,
            DatabaseHelper.NEWS_AUTEUR,
            DatabaseHelper.NEWS_DATE_EVENT,
            DatabaseHelper.NEWS_DATE_PUBLICATION};

    public NewsDb(Context context) {
        super(context);
    }

    public NewsItem cursorToItem(Cursor cursor) {
        NewsItem item = new NewsItem();
        return item;
    }

    public ArrayList<NewsItem> getAllItems() {
        ArrayList<NewsItem> itemArrayList = new ArrayList<>();

        // Exécution de la requête
        Cursor cursor = database.query(DatabaseHelper.TABLE_NEWS,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        // Lecture des résultats
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NewsItem item = cursorToItem(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return itemArrayList;
    }

    public boolean createItem(NewsItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Paramètres de la requête
        values.put(DatabaseHelper.NEWS_TITRE, item.getTitre());
        values.put(DatabaseHelper.NEWS_CONTENU, item.getContenu());
        values.put(DatabaseHelper.NEWS_AUTEUR, item.getAuteur());
        values.put(DatabaseHelper.NEWS_DATE_PUBLICATION, item.getDatePublication());
        values.put(DatabaseHelper.NEWS_DATE_EVENT, "");

        // Insertion en base
        insertId = database.insert(DatabaseHelper.TABLE_NEWS, null,
                values);

        return insertId > 0;
    }

    public void deleteItem(long id) {
        database.delete(
                DatabaseHelper.TABLE_NEWS,
                DatabaseHelper.NEWS_ID + " = " + id,
                null);
        System.out.println("Item avec l'id: " + id + " supprimé de la table " + DatabaseHelper.TABLE_NEWS);
    }
}