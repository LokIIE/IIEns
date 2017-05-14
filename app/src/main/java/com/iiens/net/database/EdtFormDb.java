package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.EdtFormItem;

/**
 * Gestion de la table EdtForm dans la bdd
 */
public class EdtFormDb extends BaseDb<EdtFormItem> {

    // Champs de la base de donnees
    private String[] allColumns = {
            DatabaseHelper.EDTFORM_ID,
            DatabaseHelper.EDTFORM_NAME,
            DatabaseHelper.EDTFORM_PROMO
    };

    public EdtFormDb ( Context context ) {

        super( context, DatabaseHelper.TABLE_EDTFORM );
        super.tableColumns = allColumns;
    }

    public EdtFormItem readCursor ( Cursor cursor ) {

        return null;
    }

    @Override
    public long findItemId ( EdtFormItem item ) {

        return 0;
    }

    @Override
    public EdtFormItem getItem ( long id ) {

        return null;
    }

    @Override
    public EdtFormItem getFirstItem () {

        return null;
    }

    public boolean createItem ( EdtFormItem item ) {

        ContentValues values = new ContentValues();
        long insertId;

        values.put( DatabaseHelper.EDTFORM_ID, item.getId() );
        values.put( DatabaseHelper.EDTFORM_NAME, item.getName() );
        values.put( DatabaseHelper.EDTFORM_PROMO, item.getPromo() );

        this.open();

        insertId = database.insert(
                DatabaseHelper.TABLE_EDTFORM,
                null,
                values
        );

        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem ( EdtFormItem item ) {

        return false;
    }

    public void deleteItem ( long id ) {}

    @Override
    public void cleanTable() {

        database.delete( DatabaseHelper.TABLE_EDTFORM, null, null );
    }
}
