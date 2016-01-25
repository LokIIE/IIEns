package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.R;
import com.iiens.net.model.EdtOptItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion de la table EdtForm dans la bdd
 */
public class EdtOptDb extends BaseDb<EdtOptItem>{
    // Champs de la base de donnees
    private String[] allColumns = {
            DatabaseHelper.EDTOPT_ID,
            DatabaseHelper.EDTOPT_NAME,
            DatabaseHelper.EDTOPT_CODE,
            DatabaseHelper.FK_EDTFORM
    };

    private Context context;

    public EdtOptDb(Context context) {
        super(context, DatabaseHelper.TABLE_EDTOPT);
        super.tableColumns = allColumns;
        this.context = context;
    }

    public EdtOptItem readCursor(Cursor cursor) {
        EdtOptItem item = new EdtOptItem();
        item.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.EDTOPT_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EDTOPT_NAME)));
        item.setCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EDTOPT_CODE)));
        item.setFk_edtForm(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FK_EDTFORM)));
        return item;
    }

    @Override
    public long findItemId(EdtOptItem item) {
        long result = 0;

        // Ouverture de la connexion
        this.open();
        // Execution de la requete
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_EDTOPT,
                tableColumns,
                String.format("%s = \"%s\" AND %s = \"%s\" AND %s = \"%s\"",
                        DatabaseHelper.EDTOPT_NAME, item.getName(),
                        DatabaseHelper.EDTOPT_CODE, item.getCode(),
                        DatabaseHelper.FK_EDTFORM, item.getFk_edtForm()),
                null,
                null,
                null,
                DatabaseHelper.EDTOPT_ID + " ASC");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = readCursor(cursor).getId();
        }
        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    @Override
    public EdtOptItem getItem(long id) {
        return null;
    }

    public List<EdtOptItem> getSpinnerItems(int idForm) {
        List<EdtOptItem> result = new ArrayList<>();
        result.add(EdtOptItem.getEmptyOptItem(
                context.getResources().getString(R.string.edtForm_emptyOp)
        ));

        // Ouverture de la connexion
        this.open();
        // Execution de la requete
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_EDTOPT,
                tableColumns,
                DatabaseHelper.FK_EDTFORM + " = " + idForm,
                null,
                null,
                null,
                DatabaseHelper.EDTOPT_ID + " ASC");

        while (cursor.moveToNext()) {
            result.add(readCursor(cursor));
        }
        // Fermeture de la connexion
        cursor.close();
        this.close();

        return result;
    }

    @Override
    public EdtOptItem getFirstItem() {
        return null;
    }

    public boolean createItem(EdtOptItem item) {
        ContentValues values = new ContentValues();
        long insertId;

        if (findItemId(item) > 0) {
            return false;
        }

        // Parametres de la requete
        values.put(DatabaseHelper.EDTOPT_NAME, item.getName());
        values.put(DatabaseHelper.EDTOPT_CODE, item.getCode());
        values.put(DatabaseHelper.FK_EDTFORM, item.getFk_edtForm());

        // Ouverture de la connexion
        this.open();
        // Insertion en base
        insertId = database.insert(
                DatabaseHelper.TABLE_EDTOPT,
                null,
                values);
        // Fermeture de la connexion
        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem(EdtOptItem item) {
        return false;
    }

    public void deleteItem(long id) {}

    @Override
    public void cleanTable() {
        database.delete(DatabaseHelper.TABLE_EDTOPT, null, null);
    }
}
