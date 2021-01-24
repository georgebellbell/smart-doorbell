/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;



@RunWith(JUnit4.class)
public class NavigationTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	/**
	 * Before each test create a new account
	 */
	@Before
	public void setup() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
	}

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

	/**
	 * After each test delete the account
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}

}
