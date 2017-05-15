package com.iiens.net;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends Activity {

    private static boolean inSettings = false;
    private final Fragment[] menuFragments = new Fragment[]{
            new News(),
            new Edt(),
            new Anniv(),
            new Twitter(),
            // not in menu
            new EdtResult()
    };
    // Items shown on the menu, each corresponds to a Fragment
    private String[] menuItems;
    private DrawerLayout drawerLayout;
    private ListView menu;
    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager fragmentManager;

    long enqueue;
    DownloadManager dm;

    /**
     * Contexte de l'application
     */
    private GlobalState appContext;

    @Override
    public void onCreate ( Bundle savedInstanceState ) {

        appContext = (GlobalState) this.getApplicationContext();
        Bundle mainBundle = appContext.getBundle();
        fragmentManager = getFragmentManager();

        // Get back all info if the activity is recreated
        super.onCreate( savedInstanceState );
        if ( savedInstanceState != null ) {

            mainBundle.putAll( savedInstanceState );
        }

        setContentView( R.layout.activity_main );
        menuItems = getResources().getStringArray( R.array.main_menu_entries );

        // Creation of the side menu
        drawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawerToggle = new ActionBarDrawerToggle( this, drawerLayout, null,
                R.string.open_menu,
                R.string.close_menu);
        menu = (ListView) findViewById( R.id.drawerMenu );
        createMenu();
    }

    @Override
    protected void onResume () {

        super.onResume();
        openFragment( appContext.getCurrentFragment() );
        if ( inSettings ) goToSettings();
    }

    /* Creates the side menu on the right of the action bar */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {

        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    /* Determines the actions to do when an action bar item is selected */
    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {

        // if the icon of the drawerLayout menu was selected
        if ( drawerToggle.onOptionsItemSelected( item ) ) return true;
        else {

            // if an item of the right action menu was selected, handle it accordingly
            drawerLayout.closeDrawer( menu );

            switch ( item.getItemId() ) {

                case R.id.action_settings:
                    if ( !inSettings ) goToSettings();
                    else backFromSettings();
                    return true;

                case R.id.action_refresh:
                    if ( inSettings ) return true;

                    // Start refreshing the display
                    ( (BaseFragment) menuFragments[ appContext.getCurrentFragment() ] ).refreshDisplay();
                    return true;

                default:
                    return super.onOptionsItemSelected( item );
            }
        }
    }

    /* To sync the toggle state after onRestoreInstanceState has occurred */
    @Override
    protected void onPostCreate( Bundle savedInstanceState ) {

        super.onPostCreate( savedInstanceState );
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged ( Configuration newConfig ) {

        super.onConfigurationChanged( newConfig );
        drawerToggle.onConfigurationChanged( newConfig ); // Pass any configuration change to the drawer toggle
    }

    /* Create the drawer menu and set its components */
    private void createMenu () {

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, menuItems );
        menu.setAdapter( menuAdapter );

        // enabling action bar app icon and behaving it as toggle button
        if ( getActionBar() != null ) {

            getActionBar().setDisplayHomeAsUpEnabled( true );
            getActionBar().setHomeButtonEnabled( true );
        }

        // Menu icon on the action bar
        drawerLayout.setDrawerListener( drawerToggle ); // Link the drawerToggle and the drawerLayout

        // Set the list's click listener
        menu.setOnItemClickListener( new ListView.OnItemClickListener() {
            @Override
            @SuppressWarnings("rawtypes")
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                drawerLayout.closeDrawer( menu ); // Close the menu in all cases

                if( position == 4 ) {

                    openBreviaire( view );

                } else if ( appContext.getCurrentFragment() != position ) {

                    // if an other item is selected in the menu, open it
                    appContext.setCurrentFragment( position );
                    openFragment( position );

                } else if ( inSettings ) {

                    // If we get want to get back from settings to the current fragment
                    backFromSettings();
                }
            }
        });
    }

    /* Specify the fragment to open based on the position of the menu item clicked */
    private void openFragment ( int position ) {

        Fragment frag;

        if ( inSettings ) {

            // if we select an item from the menu while being in app settings
            backFromSettings();
        }

        // if (fragmentManager.beginTransaction().replace(R.id.content, fragmentManager.findFragmentByTag(menuItems[position])).commit() >= 0) {}
        if ( ( frag = menuFragments[ position ] ) != null ) {

            fragmentManager.beginTransaction()
                    .replace( R.id.content_container, frag, menuItems[ position ] )
                    .commit();
            getActionBar().setTitle( menuItems[ position ] );
        }
        drawerLayout.closeDrawer(menu);
    }

    @Override
    public void onBackPressed () {

        if ( inSettings ) {
            backFromSettings();
            return;
        }

        super.onBackPressed();
    }

    private void backFromSettings () {

        inSettings = false;
        getFragmentManager().popBackStack();
        getActionBar().setTitle( menuItems[ appContext.getCurrentFragment() ] );
    }

    private void goToSettings () {

        inSettings = true;
        fragmentManager.beginTransaction()
                .replace( R.id.content_container, new Settings() )
                .addToBackStack( null )
                .commit();
    }


    public void openBreviaire ( View v ) {

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        final Uri uri= Uri.parse( appContext.getString(R.string.url_apiie) + appContext.getString(R.string.apiie_breviaire) );

        final DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir( Main.this, Environment.DIRECTORY_DOWNLOADS, "breviaire.pdf" );

        new Thread() {
            public void run() {
                enqueue = dm.enqueue(request);
            }
        }.start();
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == enqueue) {
                try {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(enqueue);
                        Cursor c = dm.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {

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
                } catch (ActivityNotFoundException e) {
                }
            }
        }
    };

    @Override
    public void onDestroy () {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }
}
