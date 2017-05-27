package com.iiens.net;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Settings extends PreferenceFragment {

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        addPreferencesFromResource( R.xml.preferences );

        getPreferenceManager().setSharedPreferencesName( getString( R.string.app_settings ) );
        getPreferenceManager().setSharedPreferencesMode( Context.MODE_PRIVATE );

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener( (Main) getActivity() );
    }
}
