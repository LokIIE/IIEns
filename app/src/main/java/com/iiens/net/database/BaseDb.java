package com.iiens.net.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Classe abstraite de gestion des tables
 */
public abstract class BaseDb<T> {
    // Champs de la base de données
    protected SQLiteDatabase database;
    protected DatabaseHelper dbHelper;

    public BaseDb(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public SQLiteDatabase open() {
        this.database = dbHelper.getWritableDatabase();
        return this.database;
    }

    public void close() {
        database.close();
    }

    public SQLiteDatabase getDb() {
        return database;
    }

    public abstract boolean createItem(T item);

    public abstract void deleteItem(long id);

    public abstract ArrayList<T> getAllItems();

    public abstract T cursorToItem(Cursor cursor);
}
