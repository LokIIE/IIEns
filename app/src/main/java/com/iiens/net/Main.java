package com.iiens.net;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GlobalState appContext;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;
    private BottomNavigationView bottomNav;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private int currentSelectedId = 0;

    long enqueue;
    DownloadManager dm;

    BroadcastReceiver onComplete = new BroadcastReceiver () {

        public void onReceive ( Context ctxt, Intent intent ) {
            // check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if( referenceId == enqueue ) {

                try {

                    String action = intent.getAction();

                    if ( DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals( action ) ) {

                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById( enqueue );
                        Cursor c = dm.query( query );

                        if ( c.moveToFirst() ) {

                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);

                            if ( DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt( columnIndex ) ) {

                                String uriString = c.getString(
                                        c.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI )
                                );

                                Uri a = Uri.parse( uriString );
                                Intent intentOpenPdf = new Intent( Intent.ACTION_VIEW );
                                intentOpenPdf.setDataAndType( a, "application/pdf" );
                                intentOpenPdf.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity(intentOpenPdf);
                            }
                        }
                    }
                } catch ( ActivityNotFoundException e ) {

                    Log.e( "onComplete", "ActivityNotFound" );
                }
            }
        }
    };

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        appContext = (GlobalState) this.getApplicationContext();

        initToolbar();
        initNavigationControls();

        if ( savedInstanceState != null ) {

            actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onStart () {

        super.onStart();

        registerReceiver( onComplete,
                new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );

        openFragment( appContext.getCurrentFragment() );
    }

    @Override
    protected void onSaveInstanceState ( Bundle outState ) {

        super.onSaveInstanceState( outState );
    }

    @Override
    protected void onResume () {

        super.onResume();
        openFragment( appContext.getCurrentFragment() );
    }

    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        int id = menuItem.getItemId();

        if( Integer.compare( id, currentSelectedId ) != 0 ) {

            switch ( id ) {

                case R.id.action_nav_news:
                    bottomNav.setSelectedItemId( R.id.action_news );
                    break;

                case R.id.action_news:
                    openFragment( News.class.getName() );
                    navDrawer.setCheckedItem( R.id.action_nav_news );
                    currentSelectedId = id;
                    break;

                case R.id.action_nav_edt:
                    bottomNav.setSelectedItemId( R.id.action_edt );
                    break;

                case R.id.action_edt:
                    openFragment( Edt.class.getName() );
                    navDrawer.setCheckedItem( R.id.action_nav_edt );
                    currentSelectedId = id;
                    break;

                case R.id.action_nav_twitter:
                    bottomNav.setSelectedItemId( R.id.action_twitter );
                    break;

                case R.id.action_twitter:
                    openFragment( Twitter.class.getName() );
                    navDrawer.setCheckedItem( R.id.action_nav_twitter );
                    currentSelectedId = id;
                    break;

                case R.id.action_nav_breviaire:
                    openBreviaire();
                    break;

                case R.id.action_nav_parametres:
                    bottomNav.setSelectedItemId( R.id.action_parametres );
                    break;

                case R.id.action_parametres:
                    openFragment( Settings.class.getName() );
                    navDrawer.setCheckedItem( R.id.action_nav_parametres );
                    currentSelectedId = id;
                    break;

                default:
                    break;
            }
        }

        if( drawerLayout.isShown() ) drawerLayout.closeDrawer( Gravity.START );

        return true;
    }

    public void onSharedPreferenceChanged ( SharedPreferences sharedPreferences, String key ) {

        if( key.equals( getString( R.string.pref_bottom_nav_key ) ) ) setControlsVisibility();
    }

    /* Specify the fragment to open based on the position of the menu item clicked */
    private void openFragment ( Fragment frag ) {

        getFragmentManager().beginTransaction()
                .replace( R.id.content_container, frag, frag.getClass().getName() )
                .addToBackStack(null)
                .commit();

        appContext.setCurrentFragment( frag );
    }

    private void openFragment ( String fragClass ) {

        FragmentManager fManager = getFragmentManager();
        Fragment fragment = fManager.findFragmentByTag( fragClass );

        if( fragment == null ) fragment = Fragment.instantiate( this, fragClass );

        openFragment( fragment );
    }

    @Override
    public void onDestroy () {

        super.onDestroy();
        unregisterReceiver( onComplete );
    }

    private void initToolbar () {

        toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();

        if( actionBar != null ) {

            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.logo_notification );
        }
    }

    private void initNavigationControls () {

        // Navigation drawer
        navDrawer = (NavigationView) findViewById( R.id.navigation_drawer );
        drawerLayout = (DrawerLayout) findViewById( R.id.main_drawer );

        actionBarDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer ) {

            @Override
            public void onDrawerClosed ( View v ) {
                super.onDrawerClosed( v );
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened( v );
            }
        };

        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();

        navDrawer.setNavigationItemSelectedListener( this );

        // Bottom navigation
        bottomNav = (BottomNavigationView) findViewById( R.id.bottom_navigation );
        bottomNav.setOnNavigationItemSelectedListener( this );

        setControlsVisibility();
    }

    private void setControlsVisibility () {

        // Application des paramètres
        SharedPreferences prefs = ((GlobalState) getApplicationContext()).getPreferences();
        navDrawer.getMenu().setGroupVisible(
                R.id.navigation_base,
                ! prefs.getBoolean( getString( R.string.pref_bottom_nav_key ), false )
        );
        bottomNav.setVisibility(
                ( prefs.getBoolean( getString( R.string.pref_bottom_nav_key ), true ) ) ? View.VISIBLE : View.GONE
        );
    }

    public void openBreviaire () {

        dm = (DownloadManager) getSystemService( DOWNLOAD_SERVICE );

        final Uri uri= Uri.parse( appContext.getScriptURL() + appContext.getString(R.string.apiie_breviaire) );

        final DownloadManager.Request request = new DownloadManager.Request( uri );
        request.setDestinationInExternalFilesDir( Main.this, Environment.DIRECTORY_DOWNLOADS, "breviaire.pdf" );

        new Thread() {
            public void run() {
                enqueue = dm.enqueue( request );
            }
        }.start();
    }
}
