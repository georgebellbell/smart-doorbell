package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

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
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SettingsPageTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	// BEFORE RUNNING THESE TESTS MAKE SURE TO HAVE LOGGED IN WITH THE TestingApp ACCOUNT (See README.txt)
	@Before
	public void moveToSettings() throws InterruptedException {
		SignupInstrumentedTest.accountCreatedSuccessfully();
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));

		Thread.sleep(5000);

		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00001"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL1"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
	}

	@Test
	public void failToAddADoorbell() throws InterruptedException {
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


	@Test
	public void successfullyAddADoorbell() throws InterruptedException {
		onView(withId(R.id.spinnerID)).perform(click());
		onView(withText("TESTDOORBELL2")).check(doesNotExist());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
		onView(withId(R.id.etDoorbellConnect)).perform(typeText("00003"),closeSoftKeyboard());
		onView(withId(R.id.etDoorbellConnectName)).perform(typeText("TESTDOORBELL2"),closeSoftKeyboard());
		onView(withId(R.id.btnDoorbellConnect)).perform(click());
		Thread.sleep(750);
		onView(withId(R.id.spinnerID)).perform(click());
		onView(withText("TESTDOORBELL2")).check(matches(isDisplayed()));
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
	}

	@Test
	public void removeDoorbell() throws InterruptedException {
		successfullyAddADoorbell();
		onView(withId(R.id.spinnerID)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL2"))).perform(click());
		onView(withId(R.id.btnRemoveDoorbell)).perform(click());
		onView(withId(R.id.btnConfirmRemove)).check(matches(isDisplayed()));
		onView(withId(R.id.btnConfirmRemove)).perform(click());
		Thread.sleep(1500);
		onView(withId(R.id.spinnerID)).perform(click());
		onView(withText("TESTDOORBELL2")).check(doesNotExist());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL1"))).perform(click());
	}

	// change email include bad emails

	//EMAIL TESTS
	// TEST FAILED
	/*
	@Test
	public void emailIsNotUnique() {
		onView(withId(R.id.etChangeEmail)).perform(typeText("g.bell1@newcastle.ac.uk"), closeSoftKeyboard());
		onView(withId(R.id.btnChangeEmail)).perform(click());
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}
	*/

	@Test
	public void emailIsNotValid() {
		onView(withId(R.id.etChangeEmail)).perform(typeText("TestUser.com"), closeSoftKeyboard());
		onView(withId(R.id.btnChangeEmail)).perform(click());
		onView(withId(R.id.ivEmailConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	//PASSWORD TESTS
	@Test
	public void passwordIsTooShort(){
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("Pass1"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

	}
	/* TEST FAILED
	@Test
	public void passwordWithASpace() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("Pass word 123"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}
	*/
	@Test
	public void passwordWithNoLowerCaseCharacters() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("PASSWORD123"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	@Test
	public void passwordWithNoUpperCaseCharacters() throws InterruptedException {
		onView(withId(R.id.pwdChangePassword)).perform(scrollTo()).perform(typeText("password123"), closeSoftKeyboard());
		onView(withId(R.id.btnChangePassword)).perform(click());
		Thread.sleep(500);
		onView(withId(R.id.ivPasswordConfirmed)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	@After
	public void deleteAccount() throws InterruptedException {
		onView(withId(R.id.btnDeleteAccount)).perform(scrollTo()).perform(click());
		onView(withId(R.id.btnConfirmDeletion)).perform(click());
		Thread.sleep(2500);
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));
	}
}
