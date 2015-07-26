package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iiens.net.model.EdtItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion de la table Edt dans la bdd
 */
public class EdtDb extends BaseDb<EdtItem>{
    // Champs de la base de données
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.EDT_ID,
            DatabaseHelper.EDT_TITRE,
            DatabaseHelper.EDT_TYPE,
            DatabaseHelper.EDT_PROF,
            DatabaseHelper.EDT_CLUB,
            DatabaseHelper.EDT_SALLE,
            DatabaseHelper.EDT_HEURE_DEBUT,
            DatabaseHelper.EDT_HEURE_FIN,
            DatabaseHelper.EDT_DUREE};

    public EdtDb(Context context) {
        super(context);
    }

    public EdtItem createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ANNIV_NOM, comment);
        long insertId = database.insert(DatabaseHelper.TABLE_EDT, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, DatabaseHelper.ANNIV_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        EdtItem newComment = cursorToItem(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteItem(EdtItem item) {
        long id = 0;
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_EDT, DatabaseHelper.ANNIV_ID
                + " = " + id, null);
    }

    public List<EdtItem> getAllComments() {
        List<EdtItem> comments = new ArrayList<EdtItem>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_EDT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EdtItem item = cursorToItem(cursor);
            comments.add(item);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return comments;
    }

    public EdtItem cursorToItem(Cursor cursor) {
        EdtItem item = new EdtItem();
        return item;
    }
}
