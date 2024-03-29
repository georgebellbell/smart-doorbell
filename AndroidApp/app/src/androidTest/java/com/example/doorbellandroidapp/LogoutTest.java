/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
public class LogoutTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);


	/**
	 * Test if pressing the logout button creates logout confirm popup
	 */
	@Test
	public void testOpenLogoutConfirmationPopup() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
		onView(withId(R.id.btnConfirmLogout)).check(matches(isDisplayed()));
		onView(withId(R.id.btnCancelLogout)).perform(click());
		TestHelper.deleteAccount();
	}


	/**
	 * Confirm cancelling logout will get rid of popup
	 */
	@Test
	public void testLogoutCancel() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
		onView(withId(R.id.btnCancelLogout)).perform(click());
		onView(withId(R.id.btnConfirmLogout)).check(doesNotExist());
		TestHelper.deleteAccount();
	}

	/**
	 * Creates account just for testing log out
	 * Due to limitations of testing, this account will have to be deleted manually before running test again
	 */
	public void createLogOutAccount() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("Logout"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("Logout@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withId(R.id.btnSignUp)).perform(click());
		Thread.sleep(3000);
		onView(withId(R.id.tvLastFace)).check(matches(isDisplayed()));
	}


	/**
	 * Confirm logging out works as intended and sends user back to login page
	 * WARNING AFTER RUNNING THIS TEST YOU WILL HAVE TO MANUALLY DELETE ACCOUNT
	 */
	@Test
	public void testLogoutConfirm() throws InterruptedException {
		createLogOutAccount();
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
		onView(withId(R.id.btnConfirmLogout)).perform(click());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));

	}
}
