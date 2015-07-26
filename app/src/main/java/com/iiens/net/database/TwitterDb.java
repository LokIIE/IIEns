package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iiens.net.model.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion de la table Twitter dans la bdd
 */
public class TwitterDb extends BaseDb<Tweet> {
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

    public TwitterDb(Context context) {
        super(context);
    }

    public Tweet createComment(String comment) {
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

    public List<Tweet> getAllComments() {
        List<Tweet> comments = new ArrayList<Tweet>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tweet item = cursorToItem(cursor);
            comments.add(item);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return comments;
    }

    public Tweet cursorToItem(Cursor cursor) {
        Tweet item = new Tweet();
        return item;
    }
}