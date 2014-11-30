package com.iiens.net;

import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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

	// The different ways of welcoming the user if connected, by replacing "..." by its name
	private String[] helloStrings = new String[]{
			"Bienvenue ... :)",
			"Coucou ... coucou",
			"Sup sup ... o/",
			"...",
			"Hello ... !",
			"..., SALOPE ! tu bois !"
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
		preferences = getSharedPreferences("IIEns_prefs", Context.MODE_PRIVATE);
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
		if (drawerToggle.onOptionsItemSelected(item))
			return true;
		else {
			// Handle item selection
			switch (item.getItemId()) {
			case R.id.action_loginout:
				Intent i;
				if (isConnected) {
					i = new Intent(Main.this, Main.class);
					i.putExtra("isConnected", false);
					editor.remove("login").remove("password").remove("auto_login").apply();
				}
				else i = new Intent(Main.this, Login.class);
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
		backFromSettingsFragment();
		super.onSaveInstanceState(outState);
		outState.putInt("currentFragment", currentFragment);
		outState.putString("scriptURL", scriptURL);
		frag.onSaveInstanceState(outState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/* Create the menu and add the items to it */
	private void createMenu() {
		// If connected, change initial parameters
		if (isConnected) setParametersConnected();

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

		// Link the drawerToggle and the drawerLayout
		drawerLayout.setDrawerListener(drawerToggle);

		// Set the list's click listener
		menu.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override @SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				if (currentFragment != position) {
					currentFragment = position;
					openFragment(position);
				} else if (inSettings && currentFragment == position) {
					backFromSettingsFragment(); 
				}

				drawerLayout.closeDrawer(menu);
			}
		});	
	}

	/* Specify the fragment to open based on the position of the menu item toggled */
	private void openFragment(int position) {
		if (inSettings) {
			backFromSettingsFragment();
		}

		frag = menuFragments[position];
		if (frag != null) {
			mainBundle.putInt("currentFragment", currentFragment);
			if (!frag.isAdded()) frag.setArguments(mainBundle); // Can't setArguments if the fragment is active, isAdded verfies that
			fragmentManager.beginTransaction().replace(R.id.content, frag).commit();

			// Highlight the selected item, update the title, and close the drawer
			menu.setItemChecked(position, true);
			getActionBar().setTitle(menuItems[position]);
		}
		drawerLayout.closeDrawer(menu); 
	}

	/* Change the initial parameters to adapt to connected state */ 	
	private void setParametersConnected() {
		// Add random welcome message in first place and the connected parameters at the end
		String[] newMenuItems = new String[menuItems.length + menuItemsConnected.length + 1];
		for (int i=0; i < menuItems.length; i++) {
			newMenuItems[i+1] = menuItems[i];
		}
		for (int i=0; i < menuItemsConnected.length; i++){
			newMenuItems[i+1+menuItems.length] = menuItemsConnected[i];
		}
		Random r = new Random();
		newMenuItems[0] = helloStrings[r.nextInt(helloStrings.length)].replace("...", mainBundle.getString("nom"));
		menuItems = newMenuItems;

		// Add a null fragment in first place to do nothing when toggled, and the connected parameters at the end
		Fragment[] newMenuFragments = new Fragment[menuFragments.length + menuFragmentsConnected.length + 1];
		for (int i=0; i < menuFragments.length; i++) {
			newMenuFragments[i+1] = menuFragments[i];
		}
		for (int i=0; i < menuFragmentsConnected.length; i++){
			newMenuFragments[i+1+menuFragments.length] = menuFragmentsConnected[i];
		}
		newMenuFragments[0] = null;
		menuFragments = newMenuFragments;

		currentFragment += 1;
	}

	@Override
	public void onBackPressed()
	{
		if (inSettings)
		{
			backFromSettingsFragment();
			return;
		}
		super.onBackPressed();
	}

	private void backFromSettingsFragment()
	{
		inSettings = false;
		getFragmentManager().popBackStack();
		getActionBar().setTitle(menuItems[currentFragment]);
	}
}
