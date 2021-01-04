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

import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TwoFAInstrumentedTest {

	@Rule
	public ActivityScenarioRule<TwoFactorAuthActivity> twoFactorAuthActivity = new ActivityScenarioRule<>(TwoFactorAuthActivity.class);

	@Test
	public void codeContainsNonIntegerValue() throws InterruptedException {

		onView(withId(R.id.etInputDigits)).perform(typeText("george"), closeSoftKeyboard());
		onView(withId(R.id.btnSubmitDigits)).perform(click());
		//Thread.sleep(2000);
		//onView(withText("Make sure your code only uses numbers")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
		onView(withText("Make sure your code only uses numbers")).inRoot(new ToastMatcher())
				.check(matches(isDisplayed()));
	}

	@Test
	public void codeIsNotSixDigits() throws InterruptedException {

		onView(withId(R.id.etInputDigits)).perform(typeText("1234"), closeSoftKeyboard());
		onView(withId(R.id.btnSubmitDigits)).perform(click());
		//Thread.sleep(2000);
		onView(withText("Your code needs to be six digits long")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
	}

	/**
	 * Check to see if user can return to sign in page
	 */
	@Test
	public void returnToLogin(){
		onView(withId(R.id.btnReturn)).perform((click()));
		onView(withId(R.id.tvSignIn)).check(matches(isDisplayed()));
	}


}
