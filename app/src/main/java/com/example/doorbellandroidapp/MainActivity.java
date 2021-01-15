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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout drawer;
	private NavigationView navigationView;
	private CoordinatorLayout coordLay;

	private Fragment fragment;
	private FragmentManager fm;

	private SharedPreferences preferences;
	private String currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		currentUser= preferences.getString("currentUser",null);
		if (currentUser!=null){
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
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


		fm = getSupportFragmentManager();
		if (savedInstanceState == null) {
			FragmentTransaction t = fm.beginTransaction();
			String currentTask = preferences.getString("currentTask",null);
			if (currentTask!=null)
			{
				navigationView.setCheckedItem(R.id.nav_faces);
				preferences.edit().putString("currentTask",null).apply();
				fragment = new FacesFragment();
			}
			else{
				navigationView.setCheckedItem(R.id.nav_home);
				fragment = new HomeFragment();
			}
			t.replace(R.id.content_frame, fragment);
			t.commit();
		} else {
			fragment = (Fragment) fm.findFragmentById(R.id.content_frame);
		}

	}

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

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

		// Handle navigation view item clicks here.
		int id = menuItem.getItemId();
		FragmentTransaction t = fm.beginTransaction();
		Intent intent;

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
				logout();


		}

		// close drawer after clicking the menu item
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return false;
	}


	void logout(){
		// Client to handle login response from server
		Client client = new Client(this) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						preferences.edit().clear().apply();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(intent);
						break;
					case "fail":
						Toast.makeText(MainActivity.this, "FATAL LOGOUT ERROR", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","logout");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();
	}



}