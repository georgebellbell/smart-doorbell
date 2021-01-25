/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */

package com.example.doorbellandroidapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {

	@Rule
	public ActivityScenarioRule<LoginActivity> loginActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

	/**
	 * Checks to see if user is able to log in correctly and be sent to Two Factor Authentication Activity
	 */
	@Test
	public void loginSuccessful() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("george"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("password"), closeSoftKeyboard());
		onView(withId(R.id.btnLogin)).perform(click());
		Thread.sleep(2000);
		onView(withId(R.id.tv2FA)).check(matches(isDisplayed()));
	}

	/**
	 * Checks to see if user loses an attempt if login details are incorrect
	 */
	@Test
	public void loginFailedWithAttemptsRemaining() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("Quick"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Doorbell"), closeSoftKeyboard());
		onView(withId(R.id.btnLogin)).perform(click());
		Thread.sleep(2000);
		onView(withText("No of attempts remaining: 4")).check(matches(isDisplayed()));
	}

	/**
	 * Checks to see if login attempts are prevented after 5 wrong attempts
	 */
	@Test
	public void loginFailedWithNoAttemptsRemaining() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("Quick"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Doorbell"), closeSoftKeyboard());
		for (int i = 0; i < 5; i++) {
			onView(withId(R.id.btnLogin)).perform(click());
			Thread.sleep(2000);
			onView(withText("No of attempts remaining: "+ (4-i))).check(matches(isDisplayed()));
		}
		onView(withId(R.id.btnLogin)).check(matches(not(isEnabled())));
	}

	/**
	 * Move to the signup page
	 */
	@Test
	public void moveToCreateAccount(){
		onView(withId(R.id.tvGoToSignUp)).perform(click());
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
}