package database;

import authentication.PasswordManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTableTest {
	private AccountTable accountTable;
	private User user;
	private PasswordManager passwordManager;

	@BeforeEach
	void setup() {
		accountTable = new AccountTable();
		accountTable.connect();
		user = new User("John", "john@jeff.com", "password", "role");
	}

	@AfterEach
	public void afterEach() {
		accountTable.deleteRecord(user.getUsername());
		accountTable.disconnect();
	}

	@Test
	void testAddRecord() {
		assertTrue(accountTable.addRecord(user));
	}

	@Test
	void testGetInvalidRecord() {
		assertNull(accountTable.getRecord("Invalid_name"));
	}

	@Test
	void testDeleteRecord() {
		accountTable.addRecord(user);
		assertTrue(accountTable.deleteRecord(user.getUsername()));
	}

	@Test
	void testGetLogin() {
		accountTable.addRecord(user);
		assertTrue(accountTable.getLogin(user.getUsername(), user.getPassword(), user.getRole()));
	}
}