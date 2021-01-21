package com.example.doorbellandroidapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NavigationInstrumentedTest {
	@Rule
	public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

	/**
	 * Checks to see if user is sent to Home Fragment via navigation
	 */
	@Test
	public void navigateToHome() throws InterruptedException {
		navigateToFaces();
		Thread.sleep(100);
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_home));
		onView(withId(R.layout.fragment_home)).check(matches(isDisplayed()));
	}
	/**
	 * Checks to see if user is sent to Faces Activity via navigation
	 */
	@Test
	public void navigateToFaces(){
		Context context = InstrumentationRegistry.getInstrumentation().getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.edit().putString("currentUser","george").apply();
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_faces));
		onView(withId(R.layout.fragment_faces)).check(matches(isDisplayed()));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Checks to see if user is sent to Settings Fragment via navigation
	 */
	@Test
	public void navigateToSettings(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		onView(withId(R.layout.fragment_settings)).check(matches(isDisplayed()));
	}
	/**
	 * Checks to see if user is logged out and sent back to Login Activity
	 */
	@Test
	public void logout(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
		onView(withId(R.id.tv2FA)).check(matches(isDisplayed()));
	}
}
