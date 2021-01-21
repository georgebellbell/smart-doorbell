package com.example.doorbellandroidapp;


import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class FacesPageTest {
	@Rule
	public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

	@Before
	public void moveToFaces(){
		onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
		onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_faces));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void showFacesPageInformationPopup(){
		onView(withId(R.id.ivInfo)).perform(click());
		onView(withText(("How to use the faces page"))).perform(click());
	}

	// TEST WITH ACCOUNT THAT HAS NO DOORBELLS

	@Test
	public void noDoorbellsToAddFacesTo() throws InterruptedException {
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivNewFace)).check(doesNotExist());
	}

	// TEST WITH ACCOUNT THAT HAS DOORBELLS AND FACES

	@Test
	public void EditFace() throws InterruptedException {
		onView(ViewMatchers.withId(R.id.recycler_view)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Dale 4.0"))));
		//onView(withId(R.id.ivPopUpDelete)).check(matches(isDisplayed()));
	}

	@Test
	public void addNewFace(){
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivNewFace)).check(matches(isDisplayed()));
	}




}
