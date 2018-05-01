package com.iiens.net.model;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class HomeItem implements Comparable<HomeItem> {

    abstract public String getItemContent ();
    abstract public String getItemIcon ();
    abstract public String getCompareDate();

    public int compareTo ( @NonNull HomeItem o ) {

        SimpleDateFormat isoFormat = new SimpleDateFormat( "yyyy-MM-dd", Locale.FRENCH );
        int result = 0;

        try {

            Date date1 = isoFormat.parse( this.getCompareDate() );
            Date date2 = isoFormat.parse( o.getCompareDate() );

            result = date1.compareTo( date2 );

        } catch ( Exception e ) {

            e.printStackTrace();
        }

        return - result;
    }
}
