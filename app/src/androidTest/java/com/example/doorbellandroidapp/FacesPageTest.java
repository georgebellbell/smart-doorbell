/*
 * @author George Bell
 * @version 1.0
 * @since 24/01/2021
 */
package com.example.doorbellandroidapp;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.Espresso.onData;


@RunWith(AndroidJUnit4.class)
public class FacesPageTest {
	@Rule
	public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule = new ActivityScenarioRule<>(SignUpActivity.class);

	/**
	 * Before each test set up the faces page by creating a new account and adding a couple doorbells
	 */
	@Before
	public void setup() throws InterruptedException {
		TestHelper.accountCreatedSuccessfully();
		TestHelper.addDoorbells();
		TestHelper.moveToFaces();
	}

	/**
	 * Test if faces information popup appears when information icon is pressed
	 */
	@Test
	public void showFacesPageInformationPopup(){
		onView(withId(R.id.ivInfo)).perform(click());
		onView(withText(("How to use the faces page"))).perform(click());
		onView(withText("X")).perform(click());
	}

	/**
	 * Test if you can successfully add a new face
	 */
	@Test
	public void addNewFace() throws InterruptedException {
		onView(withText("NewTestFace")).check(doesNotExist());
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivAddPicture)).perform(click()); //CAMERA IS OPENED

		Thread.sleep(10000); // TIME FOR USER TO TAKE PICTURE
		onView(withId(R.id.etEditImageName)).perform(typeText("NewTestFace"), closeSoftKeyboard());
		onView(withId(R.id.btnAddNewFace)).perform(click());
		Thread.sleep(1500);
		onView(withText("NewTestFace")).check(matches(isDisplayed()));
		// Remove face after for future tests
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						1, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		Thread.sleep(1000);
		onView(withId(R.id.ivPopUpDelete)).perform(click());
		onView(withText("NewTestFace")).check(doesNotExist());
	}

	/**
	 * Test if you can remove a face from a specific doorbell
	 */
	@Test
	public void removeFaceFromDoorbell() throws InterruptedException {
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		Thread.sleep(1000);
		onView(withId(R.id.ivPopUpDelete)).perform(click());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		onView(withText("TestFace1")).check(doesNotExist());
		//add face back for other tests
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivAddPicture)).perform(click()); //CAMERA IS OPENED
		Thread.sleep(10000); // TIME FOR USER TO TAKE PICTURE
		onView(withId(R.id.etEditImageName)).perform(typeText("TestFace1"), closeSoftKeyboard());
		onView(withId(R.id.btnAddNewFace)).perform(click());
		Thread.sleep(1500);
		onView(withText("TestFace1")).check(matches(isDisplayed()));

	}

	/**
	 * Test if trying to add a face without taking a picture fails
	 */
	@Test
	public void addFaceWithoutTakingPicture(){
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.etEditImageName)).perform(typeText("TestFace2"), closeSoftKeyboard());
		onView(withId(R.id.btnAddNewFace)).perform(click());
		onView(withId(R.id.btnAddNewFace)).check(matches(isDisplayed()));
		onView(withId(R.id.btnCancelAddNewFace)).perform(click());
		onView(withText("TestFace2")).check(doesNotExist());
	}

	/**
	 * Test if trying to add a face without naming face fails
	 */
	@Test
	public void addFaceWithoutNamingFace() throws InterruptedException {
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivAddPicture)).perform(click()); //CAMERA IS OPENED
		Thread.sleep(10000); // TIME FOR USER TO TAKE PICTURE
		onView(withId(R.id.btnAddNewFace)).perform(click());
		onView(withId(R.id.btnAddNewFace)).check(matches(isDisplayed()));
		onView(withId(R.id.btnCancelAddNewFace)).perform(click());
	}

	/**
	 * Test if adding a face with an illegal name fails
	 */
	@Test
	public void addFaceWithNameUnknown() throws InterruptedException {
		onView(withId(R.id.ivAddFace)).perform(click());
		onView(withId(R.id.ivAddPicture)).perform(click()); //CAMERA IS OPENED
		Thread.sleep(10000); // TIME FOR USER TO TAKE PICTURE
		onView(withId(R.id.etEditImageName)).perform(typeText("Unknown"), closeSoftKeyboard());
		onView(withId(R.id.btnAddNewFace)).perform(click());
		onView(withId(R.id.btnAddNewFace)).check(matches(isDisplayed()));
		onView(withId(R.id.btnCancelAddNewFace)).perform(click());
		Thread.sleep(1000);
	}

	/**
	 * Test if changing a face name to nothing fails
	 */
	@Test
	public void changeFaceNameToNothing(){
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		onView(withId(R.id.etEditImageName)).perform(typeText(""), closeSoftKeyboard());
		onView(withId(R.id.btnSaveAndClose)).perform(click());
		onView(withId(R.id.btnSaveAndClose)).check(matches(isDisplayed()));
		onView(withText("X")).perform(click());
	}

	/**
	 * Test if changing a face name to something illegal fails
	 */
	@Test
	public void changeNameToUnknown() throws InterruptedException {
		Thread.sleep(1500);
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		Thread.sleep(1500);
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		Thread.sleep(1500);
		onView(withId(R.id.etEditImageName)).perform(typeText("Unknown"), closeSoftKeyboard());
		onView(withId(R.id.btnSaveAndClose)).perform(click());
		onView(withId(R.id.btnSaveAndClose)).check(matches(isDisplayed()));
		onView(withText("X")).perform(click());

	}

	/**
	 * Test if changing a face name to something too long fails
	 */
	@Test
	public void changeFaceNameToSomethingTooLong() throws InterruptedException {
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		Thread.sleep(1500);
		onView(withId(R.id.etEditImageName)).perform(typeText("TestFace12345678910"), closeSoftKeyboard());
		onView(withId(R.id.btnSaveAndClose)).perform(click());
		onView(withId(R.id.btnSaveAndClose)).check(matches(isDisplayed()));
		onView(withText("X")).perform(click());
		Thread.sleep(1000);
	}

	/**
	 * Test successfully changing the name of a face
	 */
	@Test
	public void changeFaceName() throws InterruptedException {
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		onView(withId(R.id.etEditImageName)).perform(typeText("Renamed"), closeSoftKeyboard());
		onView(withId(R.id.btnSaveAndClose)).perform(click());
		Thread.sleep(2000);
		//Reset after test
		onView(withText("Renamed")).check(matches(isDisplayed()));
		Thread.sleep(1000);
		onView(withId(R.id.recycler_view))
				.perform(RecyclerViewActions.actionOnItemAtPosition(
						0, TestHelper.clickChildViewWithId(R.id.ivEdit)));
		Thread.sleep(1000);
		onView(withId(R.id.etEditImageName)).perform(typeText("TestFace1"), closeSoftKeyboard());
		onView(withId(R.id.btnSaveAndClose)).perform(click());
		Thread.sleep(1000);
	}

	/**
	 * Test switching between two doorbells, looking for faces unique to each doorbell
	 */
	@Test
	public void switchingBetweenDoorbells() throws InterruptedException {
		Thread.sleep(1000);
		onView(withText("TestFace1")).check(matches(isDisplayed()));
		onView(withText("OtherDoorbell")).check(doesNotExist());
		onView(withId(R.id.spinnerID)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("TESTDOORBELL2"))).perform(click());
		Thread.sleep(1500);
		onView(withText("OtherDoorbell")).check(matches(isDisplayed()));
		onView(withText("TestFace1")).check(doesNotExist());
	}

	/**
	 * After each test delete the account created
	 */
	@After
	public void cleanup() throws InterruptedException {
		TestHelper.deleteAccount();
	}

}
