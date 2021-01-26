/**
 * @author Dominykas Makarovas
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import authentication.PasswordManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AccountTableTest {
	private AccountTable accountTable;
	private DoorbellTable doorbellTable;
	private PasswordManager passwordManager;

	private User user;
	private User testUser;
	private Doorbell doorbell;

	private String newEmail;
	private String newUsername;
	private String newPassword;

	@BeforeEach
	void setUp() {
		accountTable = new AccountTable();
		doorbellTable = new DoorbellTable();
		passwordManager = new PasswordManager();

		user = new User("TestUser", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		testUser = new User("TestUser123", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		newEmail = "quicksolutions@gmail.com";
		newUsername = "Test1";
		newPassword = "newPassword";

		doorbellTable.addNewDoorbell(doorbell);
		assertTrue(accountTable.addRecord(user));
	}

	@AfterEach
	void tearDown() {
		accountTable.deleteRecord(user);
		accountTable.deleteRecord(newUsername);
		doorbellTable.deleteDoorbell(doorbell);
		accountTable.deleteRecord(testUser);
	}

	@Test
	void testAddRecord() {
		assertTrue(accountTable.addRecord(testUser));
	}

	@Test
	void testNotAbleToAddMultipleSameUser() {
		assertFalse(accountTable.addRecord(user));
	}

	@Test
	void testUserAddedCorrectlyToDatabase() {
		accountTable.addRecord(testUser);
		User fromDB = accountTable.getRecord(testUser.getUsername());
		assertEquals(testUser, fromDB);
	}

	@Test
	void testGetTotalUsers() {
		assertEquals(1, accountTable.getTotalUsers("admin"));
	}

	@Test
	void testGetEmailByUsername() {
		String email = accountTable.getEmailByUsername(user.getUsername());
		assertEquals(user.getEmail(), email);
	}

	@Test
	void testGetEmailByInvalidUsername() { assertNull(accountTable.getEmailByUsername("InvalidUser")); }

	@Test
	void testGetEmailByDoorbell() {
		ArrayList<String> email = accountTable.getEmailByDoorbell(doorbell.getId());
		for (String s : email) {
			assertEquals(user.getEmail(), s);
		}
	}

	@Test
	void testGetAllEmails() {
		ArrayList<String> allEmails = accountTable.getAllEmails();
		assertTrue(allEmails.contains(user.getEmail()));
	}

	@Test
	void testGetPassword() {
		String storedPassword = accountTable.getPassword(user.getUsername());
		String passwordReturned = passwordManager.checkPasswords(storedPassword, user.getPassword());
		assertEquals(storedPassword, passwordReturned);
	}

	@Test
	void testGetUserNotExistingPassword() { assertNull(accountTable.getPassword("InvalidUser")); }

	@Test
	void testGetLogin() {
		assertTrue(accountTable.getLogin(user.getUsername(), user.getPassword(), user.getRole()));
	}

	@Test
	void testGetInvalidLogin() { assertFalse(accountTable.getLogin("InvalidUser", user.getPassword(), user.getRole())); }

	@Test
	void testGetDeviceID() {
		ArrayList<String> doorbellsAssigned = accountTable.getDeviceID(user.getUsername());
		for (String doorbell : doorbellsAssigned)
			assertEquals(this.doorbell.getId(), doorbell);
	}

	@Test
	void testGetRecord() { assertEquals(user, accountTable.getRecord(user.getUsername())); }

	@Test
	void testGetInvalidRecord() { assertNotEquals(user, accountTable.getRecord("InvalidUser")); }

	@Test
	void testChangePassword() { assertTrue(accountTable.changePassword(user.getUsername(), newPassword)); }

	@Test
	void testChangeEmail() {
		assertTrue(accountTable.changeEmail(user.getUsername(), newEmail));
	}

	@Test
	void testChangeDetails() {
		assertTrue(accountTable.changeDetails(user.getUsername(), newUsername, newEmail));
	}

	@Test
	void testDeleteRecord() {
		assertTrue(accountTable.deleteRecord(user.getUsername()));
	}
}
