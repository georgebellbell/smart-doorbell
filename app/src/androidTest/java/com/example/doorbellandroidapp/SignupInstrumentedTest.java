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
public class SignupInstrumentedTest {

	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	public static void accountCreatedSuccessfully() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("TestUser"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withId(R.id.btnSignUp)).perform(click());
		Thread.sleep(2000);
		onView(withId(R.id.tvLastFace)).check(matches(isDisplayed()));
	}

	@Test
	public void createAccount() throws InterruptedException {
		accountCreatedSuccessfully();
	}
	@After
	public void deleteNewAccount() throws InterruptedException {
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_settings));
		onView(withId(R.id.btnDeleteAccount)).perform(scrollTo()).perform(click());
		onView(withId(R.id.btnConfirmDeletion)).perform(click());
		Thread.sleep(1000);
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));
	}

	//USERNAME TESTS
	@Test
	public void usernameIsNotUnique() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("george"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		Thread.sleep(2000);
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
	@Test
	public void usernameIsTooShort() {
		onView(withId(R.id.etUsername)).perform(typeText("t"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("t@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.shortUsernameError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));

	}
	@Test
	public void noUsernameInput()  {
		onView(withId(R.id.etUsername)).perform(typeText(""), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("nothing@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.noUppercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
	@Test
	public void usernameWithSpace() {
		onView(withId(R.id.etUsername)).perform(typeText("    "), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("space@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.spacesError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	//EMAIL TESTS
	@Test
	public void emailIsNotUnique() throws InterruptedException {
		onView(withId(R.id.etUsername)).perform(typeText("NotUnique"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		Thread.sleep(2000);
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
	@Test
	public void emailIsNotValid() {
		onView(withId(R.id.etUsername)).perform(typeText("BadEmail"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("TestUser.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Password123"), closeSoftKeyboard());
		onView(withText(R.string.validEmailError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	//PASSWORD TESTS
	@Test
	public void passwordIsTooShort(){
		onView(withId(R.id.etUsername)).perform(typeText("shortPassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("shortPassword@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Pass1"), closeSoftKeyboard());
		onView(withText(R.string.shortPasswordError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));

	}
	@Test
	public void passwordWithASpace() {
		onView(withId(R.id.etUsername)).perform(typeText("spacePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("spacePassword@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("Pass word 123"), closeSoftKeyboard());
		onView(withText(R.string.spacesError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	@Test
	public void passwordWithNoLowerCaseCharacters() {
		onView(withId(R.id.etUsername)).perform(typeText("uppercasePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("cap@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("PASSWORD123"), closeSoftKeyboard());
		onView(withText(R.string.noLowercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}

	@Test
	public void passwordWithNoUpperCaseCharacters() {
		onView(withId(R.id.etUsername)).perform(typeText("lowercasePassword"), closeSoftKeyboard());
		onView(withId(R.id.etEmailAddress)).perform(typeText("nocap@gmail.com"), closeSoftKeyboard());
		onView(withId(R.id.pwdPassword)).perform(typeText("password123"), closeSoftKeyboard());
		onView(withText(R.string.noUppercaseError));
		onView(withId(R.id.tvSignUp)).check(matches(isDisplayed()));
	}
}
