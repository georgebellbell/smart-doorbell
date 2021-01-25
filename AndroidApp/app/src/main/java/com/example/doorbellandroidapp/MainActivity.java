/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * Main basis for the app, controls app navigation and is the template for the different fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout drawer;
	private NavigationView navigationView;
	private CoordinatorLayout coordLay;

	private Fragment fragment;
	private FragmentManager fm;

	private SharedPreferences preferences;
	private String currentUser, currentTask;

	/**
	 * When first created, app will create navigation bar and load the home page
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		currentUser= preferences.getString("currentUser",null);
		currentTask = preferences.getString("currentTask",null);

		//checks if user has logged in before, if not they are sent to the login page
		if (currentUser==null){
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//creates the navigation bar for moving between the fragments and logging out
		generateNavigationBar();

		//loads a fragment depending where the user has just been
		selectFragment(savedInstanceState);
	}

	/**
	 * depending on previous actions in phone, will load different parts of app
	 * @param savedInstanceState data for phone
	 */
	public void selectFragment(Bundle savedInstanceState){
		fm = getSupportFragmentManager();
		if (savedInstanceState == null) {
			FragmentTransaction t = fm.beginTransaction();

			//default is to set the fragment to the home page
			navigationView.setCheckedItem(R.id.nav_home);
			fragment = new HomeFragment();

			//this will only occur when refreshing the page
			if (currentTask!=null){
				//reloads the faces page
				if (currentTask.equals("faces")) {
					navigationView.setCheckedItem(R.id.nav_faces);
					preferences.edit().putString("currentTask",null).apply();
					fragment = new FacesFragment();
				}
				//reloads the settings page
				else if (currentTask.equals("settings")) {
					navigationView.setCheckedItem(R.id.nav_settings);
					preferences.edit().putString("currentTask",null).apply();
					fragment = new SettingsFragment();
				}
			}
			//applies the set fragment
			t.replace(R.id.content_frame, fragment);
			t.commit();
		} else {
			fragment = (Fragment) fm.findFragmentById(R.id.content_frame);
		}

	}

	/**
	 * initialises the navigation bar for moving around the app
	 */
	public void generateNavigationBar(){
		ActionBar toolbar = getSupportActionBar();
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		coordLay = (CoordinatorLayout) findViewById(R.id.coordLay);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, R.string.app_name, R.string.app_name);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		navigationView = (NavigationView) findViewById(R.id.navigationView);
		navigationView.setNavigationItemSelectedListener(this);
		// display home button for actionbar
		toolbar.setDisplayHomeAsUpEnabled(true);

	}

	/**
	 * uses swipe to open menu from side
	 * @param item the side menu for navigating
	 * @return whether menu has been intereacted with
	 */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (drawer.isOpen()) {
				drawer.closeDrawers();
			} else {
				drawer.openDrawer(Gravity.LEFT);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Gives functionality to different elements of menu - moving around and logging out
	 * @param menuItem menu for navigating app
	 * @return false, closing menu after navigating to selected area
	 */
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

		// Handle navigation view item clicks here.
		int id = menuItem.getItemId();
		FragmentTransaction t = fm.beginTransaction();

		switch (id){
			//move to home page
			case R.id.nav_home:
				fragment = new HomeFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();
				navigationView.setCheckedItem(id);
				break;
			//move to the faces page
			case R.id.nav_faces:
				fragment = new FacesFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();
				navigationView.setCheckedItem(id);
				break;
			//move to the settings page
			case R.id.nav_settings:
				fragment = new SettingsFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();
				navigationView.setCheckedItem(id);
				break;
			//log out of current account
			case R.id.nav_logout:
				Popups.logoutConfirmation(this);
		}

		// close drawer after clicking the menu item
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return false;
	}
}