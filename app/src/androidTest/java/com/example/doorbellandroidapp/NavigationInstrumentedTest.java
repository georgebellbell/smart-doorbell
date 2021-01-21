package com.example.doorbellandroidapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.constraintlayout.utils.widget.MockView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


@RunWith(JUnit4.class)
public class NavigationInstrumentedTest {
	private static final String TAG = "Testing";
	
	
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
		onView(withId(R.id.tvLastFace)).check(matches(isDisplayed()));
	}
	/**
	 * Checks to see if user is sent to Faces Activity via navigation
	 */
	@Test
	public void navigateToFaces(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_faces));
		onView(withId(R.id.tvFaces)).check(matches(isDisplayed()));
	}
	/**
	 * Checks to see if user is sent to Settings Fragment via navigation
	 */
	@Test
	public void navigateToSettings(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		onView(withId(R.id.tvSettings)).check(matches(isDisplayed()));
	}

}
