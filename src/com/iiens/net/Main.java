package com.iiens.net;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Main 
	Classe permettant de gérer le menu latéral ainsi que les transitions entre les différentes fonctions de l'appli 
	Auteur : Srivatsan 'Loki' Magadevane, promo 2014
 **/

public class Main extends Activity {

	// Items shown on the menu, each corresponds to a Fragment
	private String[] menuItems = new String[]{
			"News",
			"Emploi du temps",
			"Anniversaires",
			"Twitter"
	};
	// The Fragments corresponding to the items, based on the position in the list
	private Fragment[] menuFragments = new Fragment[]{
			new News(),
			new Edt(),
			new Anniv(),
			new TwitterNews()
	};
	private int defaultFragmentNumber = 0; // Number of the fragment to show first after the SplashScreen
	private String scriptURL = "*****"; // Address of the script to make database queries

	private ListView menu;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private Bundle mainBundle = new Bundle();
	private int currentFragment = defaultFragmentNumber;
	private Fragment frag = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("Main", "onCreate called");
		// Get back all info if the activity is recreated
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			currentFragment = savedInstanceState.getInt("currentFragment");
			mainBundle.putAll(savedInstanceState);
		} else {
			mainBundle.putString("scriptURL", scriptURL);
		}

		setContentView(R.layout.activity_main);

		// Creation of the side menu
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		menu = (ListView) findViewById(R.id.menu);
		createMenu();

	}

	@Override
	protected void onStart() {
		Log.d("Main", "onStart called");
		super.onStart();
		openFragment(currentFragment);
	}

	@Override
	protected void onResume() {
		Log.d("Main", "onResume called");
		super.onResume();
	}

	/* Action after (for ex) the screen orientation has been changed */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		Log.d("Main", "onRestoreInstanceState called");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Main", "onCreateOptionsMenu called");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	} 

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.d("Main", "onOptionsItemsSelected called");
		if (drawerToggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	/* Action when (for ex) the screen orientation changes */
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.d("Main", "onSaveInstanceState called");
		super.onSaveInstanceState(outState);
		outState.putInt("currentFragment", currentFragment);
		frag.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause(){
		Log.d("Main", "onPause called");
		super.onPause();
	}

	@Override
	protected void onStop(){
		Log.d("Main", "onStop called");
		// mainBundle.clear();
		super.onStop();
	}

	/* Create the menu and add the items to it */
	private void createMenu() {
		Log.d("Main", "createMenu called");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
		menu.setAdapter(adapter);

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
				if (currentFragment != position) openFragment(position);
				else drawerLayout.closeDrawer(menu);
			}
		});	
	}

	/* Specify the fragment to open based on the position of the menu item toggled */
	private void openFragment(int position) {
		Log.d("Main", "openFragment called");
		FragmentManager fragmentManager = getFragmentManager();

		frag = menuFragments[position];
		if (frag != null) {
			currentFragment = position;
			if (!frag.isAdded()) frag.setArguments(mainBundle); // Can't setArguments if the fragment is active, isAdded verfies that
			fragmentManager.beginTransaction().replace(R.id.content, frag).commit();

			// Highlight the selected item, update the title, and close the drawer
			menu.setItemChecked(position, true);
			getActionBar().setTitle(menuItems[position]);
		}
		drawerLayout.closeDrawer(menu); 
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
}
