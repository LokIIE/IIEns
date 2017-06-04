package com.iiens.net;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;

import org.json.JSONArray;


public class Settings extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutId = R.layout.settings;
    }

    @Override
    protected void generateView ( View view ) {

        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace( R.id.settings_container, settingsFragment, "settings" )
                .commit();
    }

    @Override
    protected void displayResult (View view, JSONArray result) {}

    @Override
    protected void apiRequest (final View view) {}

    @Override
    protected void refreshDisplay () {}

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );

            PreferenceManager prefManager = getPreferenceManager();

            prefManager.setSharedPreferencesName( getString( R.string.app_settings ) );
            prefManager.setSharedPreferencesMode( Context.MODE_PRIVATE );

            prefManager.getSharedPreferences().registerOnSharedPreferenceChangeListener( (Main) getActivity() );

            addPreferencesFromResource( R.xml.preferences );
        }
    }

}
