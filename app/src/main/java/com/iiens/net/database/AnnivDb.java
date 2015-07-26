package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iiens.net.model.AnnivItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion de la table Anniv dans la bdd
 */
public class AnnivDb extends BaseDb<AnnivItem>{
    // Champs de la base de données
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.ANNIV_ID,
            DatabaseHelper.ANNIV_NOM,
            DatabaseHelper.ANNIV_PRENOM,
            DatabaseHelper.ANNIV_SURNOM,
            DatabaseHelper.ANNIV_AGE,
            DatabaseHelper.ANNIV_DATE};

    public AnnivDb(Context context) {
        super(context);
    }

    public AnnivItem createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ANNIV_NOM, comment);
        long insertId = database.insert(DatabaseHelper.TABLE_ANNIVERSAIRES, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_ANNIVERSAIRES,
                allColumns, DatabaseHelper.ANNIV_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        AnnivItem newComment = cursorToItem(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteItem(AnnivItem item) {
        long id = 0;
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_ANNIVERSAIRES, DatabaseHelper.ANNIV_ID
                + " = " + id, null);
    }

    public List<AnnivItem> getAllComments() {
        List<AnnivItem> comments = new ArrayList<AnnivItem>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_ANNIVERSAIRES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AnnivItem item = cursorToItem(cursor);
            comments.add(item);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return comments;
    }

    public AnnivItem cursorToItem(Cursor cursor) {
        AnnivItem item = new AnnivItem();
        return item;
    }
}
