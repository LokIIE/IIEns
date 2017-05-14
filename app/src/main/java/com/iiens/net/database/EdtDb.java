package com.iiens.net.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iiens.net.model.EdtItem;

/**
 * Gestion de la table Edt dans la bdd
 */
public class EdtDb extends BaseDb<EdtItem> {

    // Champs de la base de données
    private String[] allColumns = {
            DatabaseHelper.EDT_ID,
            DatabaseHelper.EDT_TITRE,
            DatabaseHelper.EDT_TYPE,
            DatabaseHelper.EDT_HOTE,
            DatabaseHelper.EDT_LIEU,
            DatabaseHelper.EDT_GROUPE,
            DatabaseHelper.EDT_DATE,
            DatabaseHelper.EDT_HEURE_DEBUT,
            DatabaseHelper.EDT_HEURE_FIN,
            DatabaseHelper.EDT_DUREE
    };

    public EdtDb ( Context context ) {

        super( context, DatabaseHelper.TABLE_EDT );
        super.tableColumns = allColumns;
    }

    public EdtItem readCursor ( Cursor cursor ) {

        EdtItem item = new EdtItem();
        return item;
    }

    @Override
    public long findItemId ( EdtItem item ) {

        return 0;
    }

    @Override
    public EdtItem getItem ( long id ) {

        return null;
    }

    @Override
    public EdtItem getFirstItem () {

        EdtItem result = null;

        this.open();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_EDT,
                tableColumns,
                null,
                null,
                null,
                null,
                DatabaseHelper.EDT_ID + " ASC"
        );

        if ( cursor.getCount() > 0 ) {

            cursor.moveToFirst();
            result = readCursor( cursor );
        }

        cursor.close();
        this.close();

        return result;
    }

    public boolean createItem ( EdtItem item ) {

        ContentValues values = new ContentValues();
        long insertId;

        // Parametres de la requete
        values.put( DatabaseHelper.EDT_TITRE, item.getTitre() );
        values.put( DatabaseHelper.EDT_TYPE, item.getType() );
        values.put( DatabaseHelper.EDT_HOTE, item.getAuteur() );
        values.put( DatabaseHelper.EDT_GROUPE, item.getGroupe() );
        values.put( DatabaseHelper.EDT_LIEU, item.getLieu() );
        values.put( DatabaseHelper.EDT_DATE, item.getJour() );
        values.put( DatabaseHelper.EDT_HEURE_DEBUT, item.getHeure() );
        values.put( DatabaseHelper.EDT_HEURE_FIN, item.getHeure() );
        values.put( DatabaseHelper.EDT_DUREE, item.getDuree() );

        this.open();

        insertId = database.insert(
                DatabaseHelper.TABLE_EDT,
                null,
                values
        );

        this.close();

        return insertId > 0;
    }

    @Override
    public boolean updateItem ( EdtItem item ) {

        return false;
    }

    public void deleteItem ( long id ) {

        database.delete(
                DatabaseHelper.TABLE_EDT,
                DatabaseHelper.EDT_ID + " = " + id,
                null
        );
        System.out.println( "Item avec l'id: " + id + " supprimé de la table " + DatabaseHelper.TABLE_EDT );
    }

    @Override
    public void cleanTable () {}
}
