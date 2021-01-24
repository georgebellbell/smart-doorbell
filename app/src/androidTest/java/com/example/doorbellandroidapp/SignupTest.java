package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static androidx.test.espresso.action.ViewActions.scrollTo;
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
public class SignupTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	/**
	 * Input valid details and create a new account
	 */
	@Test
	public void successfullyCreateAccount() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withId(R.id.btnSignUp)).perform(click());
		Thread.sleep(2000);
		onView(withId(R.id.tvLastFace)).check(matches(isDisplayed()));
		Thread.sleep(1000);
		TestHelper.deleteAccount();
	}

	/**
	 * Move from the signup page to the login page
	 */
	@Test
	public void moveToLogin(){
		onView(withId(R.id.tvGoToLogin)).perform(click());
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));
	}

	//USERNAME TESTS

	/**
	 * Attempting to create an account with a username that has already been taken
	 */
	@Test
	public void usernameIsNotUnique() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("george"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		Thread.sleep(2000);
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	/**
	 * Attempting to create an account with a username that is too short
	 */
	@Test
	public void usernameIsTooShort() {
		onView(withId(R.id.etUsername)).perform(typeText("t"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("t@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.shortUsernameError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));

	}

	/**
	 * Attempting to create an account without giving a username
	 */
	@Test
	public void noUsernameInput()  {
		onView(withId(R.id.etUsername)).perform(typeText(""), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("nothing@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.noUppercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	/**
	 * Attempting to create an account with a space in it
	 */
	@Test
	public void usernameWithSpace() {
		onView(withId(R.id.etUsername)).perform(typeText("    "), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("space@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.spacesError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	//EMAIL TESTS

	/**
	 * Attempting to create an account with an email that is already taken
	 */
	@Test
	public void emailIsNotUnique() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("NotUnique"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("g.bell1@newcastle.ac.uk"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		Thread.sleep(2000);
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	/**
	 * Attempting to create an account with an email that isn't in a valid format
	 */
	@Test
	public void emailIsNotValid() {
		onView(withId(R.id.etUsername)).perform(typeText("BadEmail"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.validEmailError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	//PASSWORD TESTS
	/**
	 * Attempting to create an account with a too short password
	 */
	@Test
	public void passwordIsTooShort(){
		onView(withId(R.id.etUsername)).perform(typeText("shortPassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("shortPassword@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Pass1"), closeSoftKeyboard());
		onView(withText(R.string.shortPasswordError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));

	}

	/**
	 * Attempting to create an account with a password with a space in it
	 */
	@Test
	public void passwordWithASpace() {
		onView(withId(R.id.etUsername)).perform(typeText("spacePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("spacePassword@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Pass word 123"), closeSoftKeyboard());
		onView(withText(R.string.spacesError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	/**
	 * Attempting to create an account with a password with no lower case characters
	 */
	@Test
	public void passwordWithNoLowerCaseCharacters() {
		onView(withId(R.id.etUsername)).perform(typeText("uppercasePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("cap@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("PASSWORD123"), closeSoftKeyboard());
		onView(withText(R.string.noLowercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	/**
	 * Attempting to create an account with a password with no upper case characters
	 */
	@Test
	public void passwordWithNoUpperCaseCharacters() {
		onView(withId(R.id.etUsername)).perform(typeText("lowercasePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("nocap@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("password123"), closeSoftKeyboard());
		onView(withText(R.string.noUppercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
}
