package com.iiens.net.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Classe abstraite de gestion des tables
 */
public abstract class BaseDb<T> {
    // Champs de la base de données
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public BaseDb(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public abstract T createComment(String comment);

    public abstract void deleteItem(T item);

    public abstract List<T> getAllComments();

    public abstract T cursorToItem(Cursor cursor);
}
