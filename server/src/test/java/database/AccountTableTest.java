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
	private Doorbell doorbell;

	private String newEmail;
	private String newUsername;

	@BeforeEach
	void setUp() {
		accountTable = new AccountTable();
		doorbellTable = new DoorbellTable();
		passwordManager = new PasswordManager();

		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		newEmail = "quicksolutions@gmail.com";
		newUsername = "Test1";

		doorbellTable.addNewDoorbell(doorbell);
	}

	@AfterEach
	void tearDown() {
		accountTable.deleteRecord(user);
		accountTable.deleteRecord(newUsername);
		doorbellTable.deleteDoorbell(doorbell);
	}

	@Test
	void addRecord() {
		assertTrue(accountTable.addRecord(user));
	}

	@Test
	void getTotalUsers() {
		assertEquals(1, accountTable.getTotalUsers("admin"));
	}

	@Test
	void getEmailByUsername() {
		addRecord();
		String expectedEmail = accountTable.getEmailByUsername(user.getUsername());
		assertEquals(expectedEmail, user.getEmail());
	}

	@Test
	void getEmailByDoorbell() {
		addRecord();
		ArrayList<String> expectedEmail = accountTable.getEmailByDoorbell(doorbell.getId());
		for (int i = 0; i < expectedEmail.size(); i++) {
			assertEquals(expectedEmail.get(i), user.getEmail());
		}
	}

	@Test
	void getAllEmails() {
		addRecord();
		ArrayList<String> allEmails = accountTable.getAllEmails();
		assertTrue(allEmails.contains(user.getEmail()));
	}

	@Test
	void getPassword() {
		addRecord();
		String storedPassword = accountTable.getPassword(user.getUsername());
		String passwordReturned = passwordManager.checkPasswords(storedPassword, user.getPassword());
		assertEquals(storedPassword, passwordReturned);
	}

	@Test
	void getLogin() {
		addRecord();
		assertTrue(accountTable.getLogin(user.getUsername(), user.getPassword(), user.getRole()));
	}

	@Test
	void getDeviceID() {
		ArrayList<String> doorbellsAssigned = accountTable.getDeviceID(user.getUsername());
		for (String doorbell : doorbellsAssigned)
			assertEquals(doorbell, this.doorbell.getId());
	}

	@Test
	void getRecord() {
		addRecord();
		assertEquals(accountTable.getRecord(user.getUsername()), user);
	}

	@Test
	void changePassword() {
		addRecord();
		String newPassword = "newPassword";
		assertTrue(accountTable.changePassword(user.getUsername(), newPassword));
	}

	@Test
	void changeEmail() {
		addRecord();
		assertTrue(accountTable.changeEmail(user.getUsername(), newEmail));
	}

	@Test
	void changeDetails() {
		addRecord();
		assertTrue(accountTable.changeDetails(user.getUsername(), newUsername, newEmail));
	}

	@Test
	void deleteRecord() {
		assertTrue(accountTable.deleteRecord(user.getUsername()));
	}
}
