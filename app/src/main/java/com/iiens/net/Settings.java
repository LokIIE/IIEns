package com.iiens.net;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Preferences
 * Permet de gérer les préférences utilisateur
 */

public class Settings extends PreferenceFragment {

    /* Called when the activity is first created. */
    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        addPreferencesFromResource( R.xml.preferences );
        if ( getActivity().getActionBar() != null ) {

            getActivity().getActionBar().setTitle( R.string.settings );
        }
    }
}
