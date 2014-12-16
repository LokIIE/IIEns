package com.iiens.net;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Main 
	Activité pricipale 
	Permet de gérer le menu latéral, certains paramètres généraux ainsi que les transitions entre les différentes fonctions de l'appli 
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class Main extends Activity {

	// Items shown on the menu, each corresponds to a Fragment
	private String[] menuItems = new String[]{
			"News",
			"Emploi du temps",
			"Anniversaires",
			"Twitter",
	};

	// Items to add when connected
	private String[] menuItemsConnected = new String[]{
			"Trombinoscope"
	};

	// The Fragments corresponding to the items, based on the position in the list
	private Fragment[] menuFragments = new Fragment[]{
			new News(),
			new Edt(),
			new Anniv(),
			new TwitterNews(),
	};

	// Fragments to add if connected
	private Fragment[] menuFragmentsConnected = new Fragment[]{
			new Trombi()
	};

	private int defaultFragmentNumber = 0; // Number of the fragment to show first after the SplashScreen
	private String scriptURL = "*****"; // Address of the script making database queries

	private DrawerLayout drawerLayout;
	private ListView menu;
	private ActionBarDrawerToggle drawerToggle;

	private FragmentManager fragmentManager;
	private Fragment frag;
	private Bundle mainBundle;
	private boolean isConnected = false;
	private boolean inSettings = false;
	private int currentFragment;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		mainBundle = new Bundle();
		mainBundle.putString("scriptURL", scriptURL);
		// If connected, get additional informations
		if (getIntent().getBooleanExtra("isConnected", false)) {
			isConnected = true;
			mainBundle.putString("login", getIntent().getStringExtra("login"));
			mainBundle.putString("pass", getIntent().getStringExtra("pass"));
			mainBundle.putString("nom", getIntent().getStringExtra("nom"));
		}
		currentFragment = defaultFragmentNumber;
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		fragmentManager = getFragmentManager();

		// Get back all info if the activity is recreated
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mainBundle.putAll(savedInstanceState);
		} 

		setContentView(R.layout.activity_main);

		// Creation of the side menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menu = (ListView) findViewById(R.id.menu);
		createMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		openFragment(currentFragment);
	}

	/* Action after (for ex) the screen orientation has been changed */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		currentFragment = savedInstanceState.getInt("currentFragment");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		if (isConnected) {
			menu.getItem(0).setTitle(R.string.action_logout);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item)) // if it is the side menu
			return true;
		else { // if it is the action menu
			// Handle item selection
			switch (item.getItemId()) {
			case R.id.action_loginout:
				Intent i;
				if (isConnected) { // revert everything to offline state
					i = new Intent(Main.this, Main.class);
					i.putExtra("isConnected", false);
					editor.remove("login").remove("password").remove("auto_login").apply();
				} else i = new Intent(Main.this, Login.class); // open the login activity
				
				startActivity(i);
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
				finish();
				return true;
			case R.id.action_settings:
				if (!inSettings) {
					fragmentManager.beginTransaction().replace(R.id.content, new Preferences()).addToBackStack(null).commit();
					inSettings = true;
					getActionBar().setTitle("Préférences");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		backFromSettings(); // if orientation changes while being in settings
		super.onSaveInstanceState(outState);
		outState.putInt("currentFragment", currentFragment);
		outState.putString("scriptURL", scriptURL);
		frag.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState(); // Sync the toggle state after onRestoreInstanceState has occurred.
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig); // Pass any configuration change to the drawer toggle
	}

	/* Create the menu and add the items to it */
	private void createMenu() {
		if (isConnected) setParametersConnected(); // If connected, change initial parameters

		ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
		menu.setAdapter(menuAdapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Menu icon on the action bar
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 
				R.drawable.ic_menu, 
				R.string.open_menu, 
				R.string.close_menu);

		drawerLayout.setDrawerListener(drawerToggle); // Link the drawerToggle and the drawerLayout

		// Set the list's click listener
		menu.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override @SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				drawerLayout.closeDrawer(menu); // Close the menu in all cases

				if (currentFragment != position) { // if an other item is selected in the menu, open it
					currentFragment = position;
					openFragment(position);
				} else if (inSettings && currentFragment == position) { // If we get want to get back from settings to the current fragment
					backFromSettings(); 
				}
			}
		});	
	}

	/* Specify the fragment to open based on the position of the menu item clicked */
	private void openFragment(int position) {
		if (inSettings) { // if we select an item from the menu while being in app settings
			backFromSettings();
		}

		frag = menuFragments[position];
		if (frag != null) {
			mainBundle.putInt("currentFragment", currentFragment);
			if (!frag.isAdded()) frag.setArguments(mainBundle); // Can't setArguments if the fragment is active, isAdded verfies that
			fragmentManager.beginTransaction().replace(R.id.content, frag).commit();

			getActionBar().setTitle(menuItems[position]);
		}
		drawerLayout.closeDrawer(menu); 
	}

	/* Replace the menu with the a new menu containing the items only accessible when connected */ 	
	private void setParametersConnected() {
		// Add user name in first place and the items accessible only while connected at the end
		String[] newMenuItems = new String[menuItems.length + menuItemsConnected.length + 1];
		newMenuItems[0] = mainBundle.getString("nom");
		for (int i = 0; i < menuItems.length; i++) {
			newMenuItems[i+1] = menuItems[i];
		}
		for (int i = 0; i < menuItemsConnected.length; i++){
			newMenuItems[i+1+menuItems.length] = menuItemsConnected[i];
		}
		menuItems = newMenuItems;

		// Add a null fragment in first place to do nothing when toggled, and the fragments accessible only while connected at the end
		Fragment[] newMenuFragments = new Fragment[menuFragments.length + menuFragmentsConnected.length + 1];
		newMenuFragments[0] = null;
		for (int i = 0; i < menuFragments.length; i++) {
			newMenuFragments[i+1] = menuFragments[i];
		}
		for (int i = 0; i < menuFragmentsConnected.length; i++){
			newMenuFragments[i+1+menuFragments.length] = menuFragmentsConnected[i];
		}
		menuFragments = newMenuFragments;
		
		currentFragment += 1; // Because the name in first place shifted everything of 1 place
	}

	@Override
	public void onBackPressed() {
		if (inSettings)	{
			backFromSettings();
			return;
		}
		super.onBackPressed();
	}

	private void backFromSettings() {
		inSettings = false;
		getFragmentManager().popBackStack();
		getActionBar().setTitle(menuItems[currentFragment]);
	}

}
