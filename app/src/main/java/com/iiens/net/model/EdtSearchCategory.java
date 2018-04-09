package com.iiens.net.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "edtSearchCategories")
public class EdtSearchCategory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String label;
    private String value;
    private String promo;

    public EdtSearchCategory () {}

    public EdtSearchCategory ( String name, String label, String value ) {

        this.setName( name );
        this.setLabel( label );
        this.setValue( value );
    }

    public int getId () {

        return id;
    }

    public void setId ( int id ) {

        this.id = id;
    }

    public String getName () {

        return name;
    }

    public void setName ( String name ) {

        this.name = name;
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

    public String getPromo () {

        return promo;
    }

    public void setPromo ( String promo ) {

        this.promo = promo;
    }
}