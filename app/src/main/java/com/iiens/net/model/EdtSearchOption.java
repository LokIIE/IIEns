package com.iiens.net.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "edtSearchOptions")
public class EdtSearchOption {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ForeignKey(entity = EdtSearchCategory.class,
            parentColumns = "id",
            childColumns = "fkEdtSearchCategory",
            onDelete = CASCADE)
    private int fkEdtSearchCategory;
    private String label;
    private String value;

    public EdtSearchOption () {}

    public EdtSearchOption ( int fkEdtSearchCategory, String label, String value ) {

        this.setFkEdtSearchCategory( fkEdtSearchCategory );
        this.setLabel( label );
        this.setValue( value );
    }

    public int getId () {

        return id;
    }

    public void setId ( int id ) {

        this.id = id;
    }

    public int getFkEdtSearchCategory () {

        return fkEdtSearchCategory;
    }

    public void setFkEdtSearchCategory ( int fkEdtSearchCategory ) {

        this.fkEdtSearchCategory = fkEdtSearchCategory;
    }

    public String getLabel () {

        return label;
    }

    public void setLabel ( String label ) {

        this.label = label;
    }

    public String getValue () {

        return value;
    }

    public void setValue ( String value ) {

        this.value = value;
    }

    /***
     * Renvoie l'item sous forme de String
     * Utile pour déterminer l'affichage de l'item dans un spinner
     */
    public String toString () {
        return getLabel();
    }
}