/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class HomeTest {
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
	 * Adds a doorbell to account where we know there is a recent user
	 */
	public void addDoorbellWithRecentUser() throws InterruptedException {
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		Thread.sleep(1000);
		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00000001"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("RecentUserDoorbell"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(2000);
	}
	/**
	 * Test if home information popup appears when information icon is pressed
	 */
	@Test
	public void showHomePageInformationPopup(){
		onView(withId(R.id.ivInfo)).perform(click());
		onView(withText(("How to use the home page"))).perform(click());
		onView(withText("X")).perform(click());
	}

	/**
	 * After creating account and linking to existing doorbell, check opening door works as intended
	 */
	@Test
	public void pressOpenDoorButton() throws InterruptedException {
		addDoorbellWithRecentUser();
		TestHelper.moveToHome();
		onView(withId(R.id.btnOpenDoor)).perform(click());
		onView(withText("You opened the door!")).check(matches(isDisplayed()));
	}

	/**
	 * After creating account and linking to existing doorbell, check close door works as intended
	 */
	@Test
	public void pressKeepDoorClosedButton() throws InterruptedException {
		addDoorbellWithRecentUser();
		TestHelper.moveToHome();
		onView(withId(R.id.btnLeaveClosed)).perform(click());
		onView(withText("You chose not to open the door")).check(matches(isDisplayed()));
	}

	// FOR THESE TESTS USE AN ACCOUNT WITH NO RECENT DOORBELL USER/NO DOORBELL ATTACHED TO THAT USER
	/**
	 * Without linking a doorbell, check if buttons on home page are enabled, they should not be
	 */
	@Test
	public void checkIfButtonsAreUsableIfNoRecentFace(){
		onView(withId(R.id.btnOpenDoor)).check(matches(not(isEnabled())));
		onView(withId(R.id.btnLeaveClosed)).check(matches(not(isEnabled())));
	}

	/**
	 * After each test delete the account created
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}
}
