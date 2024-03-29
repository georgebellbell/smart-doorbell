/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */
package com.example.doorbellandroidapp;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class SettingsPageTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	/**
	 * Before each test, user creates an account is is move to the settings page
	 */
	@Before
	public void setup() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
		TestHelper.moveToSettings();
	}

	/**
	 * Attempt to add a doorbell without adding a doorbell ID after successfully adding one
	 */
	@Test
	public void testFailToAddADoorbell() throws InterruptedException {
		testSuccessfullyAddADoorbell();
		onView(withId(R.id.spinnerID)).perform(click());
		onView(withText("TESTDOORBELL2")).check(doesNotExist());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL2"), closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(750);
		onView(withId(R.id.spinnerID)).perform(click());
		onView(withText("TESTDOORBELL2")).check(doesNotExist());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
	}

	/**
	 *	Successfully adding a doorbell to the user's account
	 */
	@Test
	public void testSuccessfullyAddADoorbell() throws InterruptedException {
		onView(withText("TESTDOORBELL1")).check(doesNotExist());
		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00001"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL1"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(750);
		onView(withText("TESTDOORBELL1")).check(matches(isDisplayed()));
		Thread.sleep(1000);
	}

	/**
	 * The above test is carried out before the doorbell is then removed
	 */
	@Test
	public void testRemoveDoorbell() throws InterruptedException {
		testSuccessfullyAddADoorbell();
		onView(withId(R.id.spinnerID)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
		onView(withId(R.id.btnRemoveDoorbell)).perform(click());
		onView(withId(R.id.btnConfirmRemove)).check(matches(isDisplayed()));
		onView(withId(R.id.btnConfirmRemove)).perform(click());
		Thread.sleep(1500);
	}

	//EMAIL TESTS

	/**
	 * Successfully changing email address of account
	 */
	@Test
	public void testSuccessfullyChangingEmail() throws InterruptedException {
		onView(withId(R.id.etChangeEmail)).perform(typeText("TestUser2@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.btnChangeEmail)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivEmailConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	/**
	 * Attempting to change email to something not valid
	 */
	@Test
	public void testEmailIsNotValid() throws InterruptedException {
		onView(withId(R.id.etChangeEmail)).perform(typeText("TestUser.com"), closeSoftKeyboard());
		onView(withId(R.id.btnChangeEmail)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivEmailConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	//PASSWORD TESTS
	/**
	 * Successfully changing the password of the account
	 */
	@Test
	public void testSuccessfullyChangingPassword() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("NewPassword1234"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}
	/**
	 * Attempting to change the password to something that is too short
	 */
	@Test
	public void testPasswordIsTooShort() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("Pass1"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	/**
	 * Attempting to change password to something with no lowercase characters
	 */
	@Test
	public void testPasswordWithNoLowerCaseCharacters() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("PASSWORD123"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	/**
	 * Attempting to change password to something with no uppercase characters
	 */
	@Test
	public void testPasswordWithNoUpperCaseCharacters() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("password123"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	/**
	 * After each test delete the account for future tests
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}
}
