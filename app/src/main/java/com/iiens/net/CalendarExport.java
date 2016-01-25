package com.iiens.net;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import com.iiens.net.model.EdtItem;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Classes contenant les méthodes d'export pour l'agenda personnel
 */
public class CalendarExport {
    private Context context;
    private final String[] minutes = {"00", "15", "30", "45"};

    public CalendarExport(Context context) {
        this.context = context;
    }

    /**
     * Ajout de l'item au calendrier
     * @param item Item à exporter
     */
    public void addItemToCalendar(EdtItem item) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // Event start time
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),     // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1, // month
                Integer.valueOf(item.getJour().substring(8, 10)),    // day
                item.getHeure() / 4 - 1,                             // hour
                Integer.valueOf(minutes[item.getHeure() % 4]),       // minutes
                0                                                    // seconds
        );
        // getTimeInMillis takes a few hundred milliseconds to compute, and it is added to the wanted value of dTStart
        // To correct it, I use Math.floor that uses a double as argument, that's why I divide by 1000
        long dTStart = (long) Math.floor(cal.getTimeInMillis() / 1000) * 1000;
        // Event end time
        cal.set(
                Integer.valueOf(item.getJour().substring(0, 4)),                  // year
                Integer.valueOf(item.getJour().substring(5, 7)) - 1,               // month
                Integer.valueOf(item.getJour().substring(8, 10)),                  // day
                (item.getHeure() + item.getDuree()) / 4 % 24 - 1,                  // hour
                Integer.valueOf(minutes[(item.getHeure() + item.getDuree()) % 4]), // minutes
                0                                                                  // seconds
        );
        // Same as dTStart
        long dTEnd = (long) Math.floor(cal.getTimeInMillis() / 1000) * 1000;
        String titre = (item.getType().equals("assoce")) ? item.getAuteur() + " - " + item.getTitre() : item.getType() + " - " + item.getTitre();
        if (!eventAlreadyExists(titre, dTStart, dTEnd)) {
            // Construct event details
            String lieu = (item.getLieu().length() == 3) ? "ENSIIE - salle : " + item.getLieu() : item.getLieu();
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, dTStart);
            values.put(CalendarContract.Events.DTEND, dTEnd);
            values.put(CalendarContract.Events.TITLE, titre);
            values.put(CalendarContract.Events.DESCRIPTION, lieu);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(CalendarContract.Events.CALENDAR_ID, 1); // Calendar to which the events will be added (1 is the default)
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);

            // Insert Event
            cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

    private boolean eventAlreadyExists(String title, long eventTBegin, long eventTEnd) {
        // determine which fields we want in our events, like before
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };

        // retrieve the ContentResolver
        ContentResolver resolver = context.getContentResolver();

        // get the query uri
        Uri uri = CalendarContract.Events.CONTENT_URI;

        // filter the selection, like before
        String selection = "((" // add filters
                + CalendarContract.Events.TITLE + " = ? ) AND ( "
                + CalendarContract.Events.DTSTART + " = ? ) AND ( "
                + CalendarContract.Events.DTEND + " = ? ) "
                + ")";
        String[] selectionArgs = new String[]{ // add the filters value
                title,
                String.valueOf(eventTBegin),
                String.valueOf(eventTEnd),
        };

        // resolve the query, this time also including a sort option
        Cursor eventCursor = resolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        int nEvents = 0;
        if (eventCursor != null) {
            nEvents = eventCursor.getCount();
            eventCursor.close(); // to free resources
        }

        return (nEvents > 0);
    }
}
