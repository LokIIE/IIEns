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
        return item;
    }

    @Override
    public long findItemId(NewsItem item) {
        return 0;
    }

    public ArrayList<NewsItem> getAllItems() {
        ArrayList<NewsItem> itemArrayList = new ArrayList<>();

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
        cursor.close();

        return itemArrayList;
    }

    @Override
    public NewsItem getItem(long id) {
        return null;
    }

    @Override
    public NewsItem getFirstItem() {
        NewsItem result = null;

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ANNIVERSAIRES,
                allColumns,
                null,
                null,
                null,
                null,
                DatabaseHelper.ANNIV_ID + " ASC");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor);
        }

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

        // Insertion en base
        insertId = database.insert(DatabaseHelper.TABLE_NEWS, null,
                values);

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