package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iiens.net.model.Tweet;

import java.util.ArrayList;

/**
 * Gestion de la table Twitter dans la bdd
 */
public class TwitterDb {//extends BaseDb<Tweet> {
    // Champs de la base de données
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {};

    public TwitterDb(Context context) {
        //super(context);
    }

    public Tweet cursorToItem(Cursor cursor) {
        Tweet item = new Tweet();
        return item;
    }

    public ArrayList<Tweet> getAllItems() {
        ArrayList<Tweet> itemArrayList = new ArrayList<>();

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
            Tweet item = cursorToItem(cursor);
            itemArrayList.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return itemArrayList;
    }
    public Tweet createItem(String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ANNIV_NOM, comment);
        long insertId = database.insert(DatabaseHelper.TABLE_EDT, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, DatabaseHelper.ANNIV_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Tweet newComment = cursorToItem(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteItem(Tweet item) {
        long id = 0;
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_EDT, DatabaseHelper.ANNIV_ID
                + " = " + id, null);
    }
}