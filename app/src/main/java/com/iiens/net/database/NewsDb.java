package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iiens.net.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion de la table News dans la bdd
 */
public class NewsDb extends BaseDb<NewsItem> {
    // Champs de la base de données
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.EDT_ID,
            DatabaseHelper.EDT_TITRE,
            DatabaseHelper.EDT_TYPE,
            DatabaseHelper.EDT_PROF,
            DatabaseHelper.EDT_CLUB,
            DatabaseHelper.EDT_SALLE,
            DatabaseHelper.EDT_HEURE_DEBUT,
            DatabaseHelper.EDT_HEURE_FIN,
            DatabaseHelper.EDT_DUREE};

    public NewsDb(Context context) {
        super(context);
    }

    public NewsItem createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ANNIV_NOM, comment);
        long insertId = database.insert(DatabaseHelper.TABLE_EDT, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, DatabaseHelper.ANNIV_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        NewsItem newComment = cursorToItem(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteItem(NewsItem item) {
        long id = 0;
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_EDT, DatabaseHelper.ANNIV_ID
                + " = " + id, null);
    }

    public List<NewsItem> getAllComments() {
        List<NewsItem> comments = new ArrayList<NewsItem>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NewsItem item = cursorToItem(cursor);
            comments.add(item);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return comments;
    }

    public NewsItem cursorToItem(Cursor cursor) {
        NewsItem item = new NewsItem();
        return item;
    }
}