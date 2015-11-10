package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.FormEdtItem;

/**
 * Gestion de la table FormEdt dans la bdd
 */
public class FormEdtDb extends BaseDb<FormEdtItem>{
    // Champs de la base de donnees
    private String[] allColumns = {
            DatabaseHelper.FORM_ID,
            DatabaseHelper.FORM_CODE,
            DatabaseHelper.FORM_LABEL,
            DatabaseHelper.FORM_ANNEE,
            DatabaseHelper.FORM_OPTION_GROUP,
            DatabaseHelper.FORM_OPTION_NUM};

    public FormEdtDb(Context context) {
        super(context, DatabaseHelper.TABLE_FORM_EDT);
        super.tableColumns = allColumns;
    }

    public FormEdtItem readCursor(Cursor cursor) {
        FormEdtItem item = new FormEdtItem();
        return item;
    }

    @Override
    public long findItemId(FormEdtItem item) {
        return 0;
    }

    @Override
    public FormEdtItem getItem(long id) {
        return null;
    }

    @Override
    public FormEdtItem getFirstItem() {
        FormEdtItem result = null;

        // Ouverture de la connexion
        this.open();
        // Execution de la requete
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_FORM_EDT,
                tableColumns,
                null,
                null,
                null,
                null,
                DatabaseHelper.FORM_ID + " ASC");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor);
        }
        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    public boolean createItem(FormEdtItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        // Parametres de la requete
        values.put(DatabaseHelper.FORM_CODE, item.getCode());
        values.put(DatabaseHelper.FORM_LABEL, item.getLabel());
        values.put(DatabaseHelper.FORM_ANNEE, item.getAnnee());
        values.put(DatabaseHelper.FORM_OPTION_GROUP, item.getOption_group());
        values.put(DatabaseHelper.FORM_OPTION_NUM, item.getOption_num());

        // Ouverture de la connexion
        this.open();
        // Insertion en base
        insertId = database.insert(
                DatabaseHelper.TABLE_FORM_EDT,
                null,
                values);
        // Fermeture de la connexion
        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem(FormEdtItem item) {
        return false;
    }

    public void deleteItem(long id) {
        database.delete(
                DatabaseHelper.TABLE_FORM_EDT,
                DatabaseHelper.FORM_ID + " = " + id,
                null);
        System.out.println("Item avec l'id: " + id + " supprime de la table " + DatabaseHelper.TABLE_FORM_EDT);
    }

    @Override
    public void cleanTable() {

    }
}
