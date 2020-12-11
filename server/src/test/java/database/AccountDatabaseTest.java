package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDatabaseTest {
	private AccountDatabase accountDatabase;
	private User user;

	@BeforeEach
	void setup() {
		accountDatabase = new AccountDatabase();
		accountDatabase.databaseConnection.connectToDatabase();
		user = new User("John", "john@jeff.com", "password", "salt", "role");
	}

	@AfterEach
	public void afterEach() {
		accountDatabase.databaseConnection.closeConnection();
	}

	@Test
	void addRecord() {
		assertTrue(accountDatabase.addRecord(user));
		accountDatabase.deleteRecord(user.getUsername());
	}

	@Test
	void getRecord() {
		accountDatabase.addRecord(user);
		assertEquals(accountDatabase.getRecord(user.getUsername()), user);
		accountDatabase.deleteRecord(user.getUsername());
	}

	@Test
	void deleteRecord() {
		accountDatabase.addRecord(user);
		assertTrue(accountDatabase.deleteRecord(user.getUsername()));
	}
}