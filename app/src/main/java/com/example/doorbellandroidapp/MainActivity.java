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
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout drawer;
	private NavigationView navigationView;
	private CoordinatorLayout coordLay;

	private Fragment fragment;
	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar toolbar = getSupportActionBar();

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		coordLay = (CoordinatorLayout) findViewById(R.id.coordLay);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, R.string.app_name, R.string.app_name);
		//drawer.setDrawerListener(toggle);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		navigationView = (NavigationView) findViewById(R.id.navigationView);
		navigationView.setNavigationItemSelectedListener(this);

		// display home button for actionbar
		toolbar.setDisplayHomeAsUpEnabled(true);

		// navigation view select home menu by default
		//navigationView.getMenu().getItem(0).setChecked(true);
		navigationView.setCheckedItem(R.id.nav_home);

		fm = getSupportFragmentManager();
		if (savedInstanceState == null) {

			FragmentTransaction t = fm.beginTransaction();
			fragment = new HomeFragment();
			t.replace(R.id.content_frame, fragment);
			t.commit();
		} else {
			fragment = (Fragment) fm.findFragmentById(R.id.content_frame);
		}

	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				if(drawer.isOpen()){
					drawer.closeDrawers();
				} else {
					drawer.openDrawer(Gravity.LEFT);
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

		// Handle navigation view item clicks here.
		int id = menuItem.getItemId();
		FragmentTransaction t = fm.beginTransaction();

		switch (id){
			case R.id.nav_home:

				fragment = new HomeFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();

				navigationView.setCheckedItem(id);
				break;

			case R.id.nav_faces:

				fragment = new FacesFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();

				navigationView.setCheckedItem(id);
				break;

			case R.id.nav_settings:

				fragment = new SettingsFragment();
				t.replace(R.id.content_frame, fragment);
				t.commit();

				navigationView.setCheckedItem(id);
				break;

			case R.id.nav_logout:

				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);

		}

		// close drawer after clicking the menu item
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return false;
	}



}