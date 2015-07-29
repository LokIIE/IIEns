package com.iiens.net.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Classe abstraite de gestion des tables
 */
public abstract class BaseDb<T> {
    // Champs de la base de données
    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;

    protected BaseDb(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    protected void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    public abstract boolean createItem(T item);

    public abstract void deleteItem(long id);

    public abstract ArrayList<T> getAllItems();

    public abstract T cursorToItem(Cursor cursor);
}
