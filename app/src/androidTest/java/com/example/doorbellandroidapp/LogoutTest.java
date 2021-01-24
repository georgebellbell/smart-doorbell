package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


@RunWith(JUnit4.class)
public class LogoutTest {

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
	 * Test if pressing the logout button creates logout confirm popup
	 */
	@Test
	public void openLogoutConfirmationPopup(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_logout));
		onView(withId(R.id.btnConfirmLogout)).check(matches(isDisplayed()));
	}


	/**
	 * Confirm cancelling logout will get rid of popup
	 */
	@Test
	public void logoutCancel(){
		openLogoutConfirmationPopup();
		onView(withId(R.id.btnCancelLogout)).perform(click());
		onView(withId(R.id.btnConfirmLogout)).check(doesNotExist());
	}

	/**
	 * After each test delete the account
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}

	/**
	 * Confirm logging out works as intended and sends user back to login page
	 * WARNING AFTER RUNNING THIS TEST YOU WILL HAVE TO MANUALLY DELETE ACCOUNT
	 */
	@Test
	public void logoutConfirm(){
		openLogoutConfirmationPopup();
		onView(withId(R.id.btnConfirmLogout)).perform(click());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		onView(withId(R.id.tvLogin)).check(matches(isDisplayed()));

	}
}
