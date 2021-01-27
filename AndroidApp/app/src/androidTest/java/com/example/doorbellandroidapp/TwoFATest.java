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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TwoFATest {

	@Rule
	public ActivityScenarioRule<TwoFactorAuthActivity> twoFactorAuthActivity = new ActivityScenarioRule<>(TwoFactorAuthActivity.class);

	/**
	 * Test to for an invalid code input with characters that aren't integers
	 */
	@Test
	public void testCodeContainsNonIntegerValue() {
		onView(withId(R.id.etInputDigits)).perform(typeText("george"), closeSoftKeyboard());
		onView(withId(R.id.btnSubmitDigits)).perform(click());
		onView(withText("Make sure your code only uses numbers")).check(matches(isDisplayed()));
	}

	/**
	 * Test to for an invalid code input with a length less than 6
	 */
	@Test
	public void testCodeIsLessThanSixDigits() {
		onView(withId(R.id.etInputDigits)).perform(typeText("1234"), closeSoftKeyboard());
		onView(withId(R.id.btnSubmitDigits)).perform(click());
		onView(withText("Your code needs to be six digits long")).check(matches(isDisplayed()));
	}

	/**
	 * Test to for an invalid code input with a length greater than 6
	 */
	@Test
	public void testCodeIsMoreThanSixDigits() {
		onView(withId(R.id.etInputDigits)).perform(typeText("1234567"), closeSoftKeyboard());
		onView(withId(R.id.btnSubmitDigits)).perform(click());
		onView(withText("Your code needs to be six digits long")).check(matches(isDisplayed()));
	}

	/**
	 * Check to see if user can return to sign in page
	 */
	@Test
	public void testReturnToLogin(){
		onView(withId(R.id.btnReturn)).perform((click()));
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));
	}

}
