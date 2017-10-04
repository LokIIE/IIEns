package com.iiens.net.model;

public class EdtFormItemv2 {

    private String name;
    private String label;
    private String value;

    public EdtFormItemv2 ( String name, String label, String value ) {

        this.setName( name ).setLabel( label ).setValue( value );
    }

    public String getName () { return this.name; }

    public EdtFormItemv2 setName ( String value ) {

        this.name = value;
        return this;
    }

    public String getLabel () { return this.label; }

    public EdtFormItemv2 setLabel ( String value ) {

        this.label = value;
        return this;
    }

    public String getValue () { return this.value; }

    public EdtFormItemv2 setValue ( String value ) {

        this.value = value;
        return this;
    }

    public String toString () { return this.label; }
}