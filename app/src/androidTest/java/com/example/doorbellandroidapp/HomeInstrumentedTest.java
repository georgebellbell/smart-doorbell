package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HomeInstrumentedTest {
	@Rule
	public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

	@Test
	public void pressOpenDoorButton(){
		onView(withId(R.id.btnOpenDoor)).perform(click());
		onView(withText("You opened the door!")).check(matches(isDisplayed()));
	}
	@Test
	public void pressKeepDoorClosedButton(){
		onView(withId(R.id.btnLeaveClosed)).perform(click());
		onView(withText("You chose not to open the door")).check(matches(isDisplayed()));
	}
}
