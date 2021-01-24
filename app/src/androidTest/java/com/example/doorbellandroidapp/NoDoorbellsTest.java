/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */
package com.example.doorbellandroidapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NoDoorbellsTest {
	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	/**
	 * Set up test by creating new account and navigating to faces page
	 */
	@Before
	public void setup() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
		TestHelper.moveToFaces();
	}

	/**
	 * Test if attempting to add a new face without a linked doorbell fails
	 */
	@Test
	public void noDoorbellsToAddFacesTo() throws InterruptedException {
		onView(withId(R.id.ivAddFace)).perform(click());
		Thread.sleep(2000);
		onView(withId(R.id.ivNewFace)).check(doesNotExist());
	}

	/**
	 * After test delete the account
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}
}
