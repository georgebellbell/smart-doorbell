package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class HomeInstrumentedTest {
	@Rule
	public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

	@Test
	public void showHomePageInformationPopup(){
		onView(withId(R.id.ivInfo)).perform(click());
		onView(withText(("How to use the home page"))).perform(click());

	}

	// FOR THESE TESTS USE ACCOUNT WITH RECENT DOORBELL USER

	@Test
	public void pressOpenDoorButton() throws InterruptedException {
		Thread.sleep(3000);
		onView(withId(R.id.btnOpenDoor)).perform(click());
		onView(withText("You opened the door!")).check(matches(isDisplayed()));
	}
	@Test
	public void pressKeepDoorClosedButton() throws InterruptedException {
		Thread.sleep(3000);
		onView(withId(R.id.btnLeaveClosed)).perform(click());
		onView(withText("You chose not to open the door")).check(matches(isDisplayed()));
	}

	// FOR THESE TESTS USE AN ACCOUNT WITH NO RECENT DOORBELL USER/NO DOORBELL ATTACHED TO THAT USER

	@Test
	public void checkIfButtonsAreUsableIfNoRecentFace(){
		onView(withId(R.id.btnOpenDoor)).check(matches(not(isEnabled())));
		onView(withId(R.id.btnLeaveClosed)).check(matches(not(isEnabled())));
	}
}
