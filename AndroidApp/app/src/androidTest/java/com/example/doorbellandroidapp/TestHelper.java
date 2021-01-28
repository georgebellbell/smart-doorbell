/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */
package com.example.doorbellandroidapp;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;

import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class TestHelper {

	/**
	 * Allows edit button for a face on the faces page to be pressed during test
	 * @param id the identifier for an object within the view holder object, in this instance the edit button
	 * @return action of pressing the edit button
	 */
	public static ViewAction clickChildViewWithId(final int id) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return null;
			}

			@Override
			public String getDescription() {
				return "Click on a child view with specified id.";
			}

			@Override
			public void perform(UiController uiController, View view) {
				View v = view.findViewById(id);
				v.performClick();
			}
		};
	}

	/**
	 * Creates a new account for running the test
	 */
	public static void accountCreatedSuccessfully() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withId(R.id.btnSignUp)).perform(click());
		Thread.sleep(3000);
		onView(withId(R.id.tvLastFace)).check(matches(isDisplayed()));
	}

	/**
	 * User is moved to the home page
	 */
	public static void moveToHome(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_home));
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * User is moved to the faces page
	 */
	public static void moveToFaces(){
		try {
			onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
			onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_faces));
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * User is moved to the settings page
	 */
	public static void moveToSettings(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to settings page and adds a couple doorbells to the account
	 */
	public static void addDoorbells() throws InterruptedException {
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		Thread.sleep(1000);
		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00001"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL1"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(2000);
		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00002"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL2"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(1000);
	}

	/**
	 * After each test, delete the account
	 */
	public static void deleteAccount() throws InterruptedException {
		Thread.sleep(1500);
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		Thread.sleep(1000);
		onView(withId(R.id.btnDeleteAccount)).perform(scrollTo()).perform(click());
		onView(withId(R.id.btnConfirmDeletion)).perform(click());
		Thread.sleep(2500);
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));
	}

}
