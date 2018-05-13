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
import android.os.Handler;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iiens.net.tasks.sync.TaskSyncBirthdays;
import com.iiens.net.tasks.sync.TaskSyncEdtSearch;
import com.iiens.net.tasks.sync.TaskSyncNews;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private GlobalState appContext;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FrameLayout mainContainer;
    private NavigationView navDrawer;
    private BottomNavigationView bottomNav;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private int currentSelectedId = 0;

    private boolean doubleBackToExitPressedOnce = false;
    private Toast tMsg;

    public long enqueue;
    private DownloadManager dm;

    private SharedPreferences prefs;

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

                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                            if ( DownloadManager.STATUS_SUCCESSFUL == c.getInt( columnIndex ) ) {

                                String uriString = c.getString( c.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
                                appContext.openPdf( uriString );
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

        appContext = (GlobalState) this.getApplicationContext();
        prefs = appContext.getPreferences();

        setTheme( prefs.getBoolean( getString( R.string.pref_mode_nuit_key ), false ) ?
                R.style.IIEnsTheme_Dark
                : R.style.IIEnsTheme_Light
        );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mainContainer = findViewById( R.id.content_container );

        initToolbar();
        initNavigationControls();

        if ( savedInstanceState != null ) {

            actionBarDrawerToggle.syncState();
        }

        try {

            if( appContext.isOnline() && appContext.isAriseAvailable() ) {

                ( new TaskSyncNews( appContext, null ) ).execute();
                ( new TaskSyncBirthdays( appContext, null ) ).execute();
                ( new TaskSyncEdtSearch( appContext ) ).execute();
            }

        } catch ( ExecutionException e ) {
            e.printStackTrace();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart () {

        super.onStart();

        registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );

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

    @Override
    public void onBackPressed () {

        if ( getFragmentManager().getBackStackEntryCount() > 0 ) {

            getFragmentManager().popBackStack();

        } else if ( ! doubleBackToExitPressedOnce ) {

            this.doubleBackToExitPressedOnce = true;
            tMsg = Toast.makeText( this, getString( R.string.back_exit_warning ), Toast.LENGTH_SHORT );
            tMsg.show();

            new Handler().postDelayed( () -> {
                tMsg = null;
                doubleBackToExitPressedOnce = false;
            }, 2000 );

        } else {

            super.onBackPressed();
            ((GlobalState) getApplicationContext()).setOauthConnected( false );
            tMsg.cancel();
        }
    }

    @Override
    public void onDestroy () {

        super.onDestroy();
        unregisterReceiver( onComplete );
    }

    public boolean onNavigationItemSelected ( @NonNull MenuItem menuItem ) {

        int id = menuItem.getItemId();

        if( Integer.compare( id, currentSelectedId ) != 0 ) {

            switch ( id ) {

                case R.id.action_home:
                    openFragment( Home.class.getName(), null );
                    currentSelectedId = id;
                    break;

                case R.id.action_edt:
                    openFragment( EdtSearch.class.getName(), null );
                    currentSelectedId = id;
                    break;

                case R.id.action_twitter:
                    openFragment( Twitter.class.getName(), null );
                    currentSelectedId = id;
                    break;

                case R.id.action_nav_itvtube:
                    startActivity( new Intent( this, Itvtube.class ) );
                    break;

                case R.id.action_nav_breviaire:
                    openBreviaire();
                    break;

                case R.id.action_nav_arise:
                    startActivity(
                        ( new Intent( this, ClubPage.class ) )
                                .putExtra( "url", getString( R.string.url_arise ) )
                    );
                    break;

                case R.id.action_nav_etranger:
                    startActivity(
                        ( new Intent( this, ClubPage.class ) )
                                .putExtra( "url", getString( R.string.url_etranger ) )
                    );
                    break;

                case R.id.action_nav_parametres:
                    openFragment( Settings.class.getName(), null );
                    break;

                default:
                    break;
            }
        }

        if( drawerLayout.isShown() ) drawerLayout.closeDrawer( Gravity.START );

        return true;
    }

    public void onSharedPreferenceChanged ( SharedPreferences sharedPreferences, String key ) {

        if( key.equals( getString( R.string.pref_mode_nuit_key ) ) ) {

            recreate();
        }
    }

    private void openFragment ( Fragment frag ) {

        if( getFragmentManager().getBackStackEntryCount() > 0 ) {

            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .replace( R.id.content_container, frag, frag.getClass().getName() )
                .commit();

        appContext.setCurrentFragment( frag );
    }

    private void openFragment ( String fragClass, Bundle params ) {

        FragmentManager fManager = getFragmentManager();
        Fragment fragment = fManager.findFragmentByTag( fragClass );

        if( fragment == null ) fragment = Fragment.instantiate( this, fragClass );

        if( fragment.isVisible() ) return;

        if( params != null ) fragment.setArguments( params );

        openFragment( fragment );
    }

    private void initToolbar () {

        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();

        if( actionBar != null ) {

            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.logo_notification );
        }
    }

    private void initNavigationControls () {

        // Navigation drawer
        navDrawer = findViewById( R.id.navigation_drawer );
        drawerLayout = findViewById( R.id.main_drawer );

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
        bottomNav = findViewById( R.id.bottom_navigation );
        bottomNav.setOnNavigationItemSelectedListener( this );

        View navDrawerHeader = navDrawer.getHeaderView( 0 );
        navDrawerHeader.findViewById( R.id.nav_connect ).setOnClickListener( v -> onNavConnectClicked( v ) );

        if( appContext.isOauthConnected() ) {

            navDrawerHeader.findViewById( R.id.nav_connect ).setVisibility( View.GONE );
            navDrawerHeader.findViewById( R.id.nav_user_infos ).setVisibility( View.VISIBLE );

            ((TextView) navDrawerHeader.findViewById( R.id.nav_pseudo ))
                    .setText( appContext.getUserInfo( "surnom" ) );
            ((TextView) navDrawerHeader.findViewById( R.id.nav_full_name ))
                    .setText( appContext.getUserInfo( "prenom" ).concat( " " ).concat( appContext.getUserInfo( "nom" ) ) );
            ((TextView) navDrawerHeader.findViewById( R.id.nav_promo ))
                    .setText( String.format( "Promo %s", appContext.getUserInfo( "promo" ) ) );
        }
    }

    public void openBreviaire () {

        final Uri uri = Uri.parse( appContext.getApiURL() + appContext.getString(R.string.api_breviaire ) );
        File breviaire = new File( getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "breviaire.pdf" );

        if( breviaire.exists() ) {

            appContext.openPdf( Uri.fromFile( breviaire ).toString() );

        } else {

            dm = (DownloadManager) getSystemService( DOWNLOAD_SERVICE );
            final DownloadManager.Request request = new DownloadManager.Request( uri );
            request.setDestinationInExternalFilesDir( this, Environment.DIRECTORY_DOWNLOADS, "breviaire.pdf" )
                    .setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED );

            new Thread() {
                public void run() {
                    enqueue = dm.enqueue( request );
                }
            }.start();
        }
    }

    public void onNavConnectClicked ( View v ) {

        startActivity( new Intent( Main.this, Login.class ) );
        finish();
    }

    public void changeBottomNavigationVisibility ( Boolean visibility ) {

        int animRef = ( visibility ) ? R.anim.bottom_in : R.anim.bottom_out;
        Animation anim = AnimationUtils.loadAnimation( getApplicationContext(), animRef );
        bottomNav.startAnimation( anim );
    }
}
