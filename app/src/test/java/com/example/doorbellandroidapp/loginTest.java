package com.example.doorbellandroidapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class loginTest {
	LoginActivity login = new LoginActivity();
	@Test
	public void loginSuccessful(){
		login.authenticate("user","password");
		assertTrue(login.isValid);
	}
	@Test
	public void loginFailedWithOneAttemptLeft(){
		login.attempts = 1;
		login.authenticate("quick","doorbell");
		assertEquals(0,(int)login.attempts);
		assertFalse(login.checkAttempts(login.attempts));
	}
	@Test
	public void loginFailedWithMultipleAttemptsLeft(){
		login.authenticate("quick","doorbell");
		assertEquals(4,(int)login.attempts);
		assertTrue(login.checkAttempts(login.attempts));
	}
	@Test
	public void inputIsEmpty(){
		assertFalse(login.inputValidation("",""));
	}

}