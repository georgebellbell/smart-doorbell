package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTableTest {
	private AccountTable accountTable;
	private User user;

	@BeforeEach
	void setup() {
		accountTable = new AccountTable();
		accountTable.getDatabaseConnection().connectToDatabase();
		user = new User("John", "john@jeff.com", "password", "salt", "role");
	}

	@AfterEach
	public void afterEach() {
		accountTable.deleteRecord(user.getUsername());
		accountTable.getDatabaseConnection().closeConnection();
	}

	@Test
	void testAddRecord() {
		assertTrue(accountTable.addRecord(user));
	}

	@Test
	void testGetRecord() {
		accountTable.addRecord(user);
		assertEquals(accountTable.getRecord(user.getUsername()), user);
	}

	@Test
	void testDeleteRecord() {
		accountTable.addRecord(user);
		assertTrue(accountTable.deleteRecord(user.getUsername()));
	}
}