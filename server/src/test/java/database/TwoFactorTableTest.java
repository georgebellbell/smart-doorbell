package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorTableTest {
	private TwoFactorTable twoFactorTable;
	private User user;
	private String code;

	@BeforeEach
	void setUp() {
		twoFactorTable = new TwoFactorTable();
		twoFactorTable.connectToDatabase();
		user = new User("JohnnyD54143", "johnnyD@dom.com", "password", "salt", "role");
		code = "123456";
	}

	@AfterEach
	public void afterEach() {
		twoFactorTable.deleteRecord(user);
		twoFactorTable.closeConnection();
	}

	@Test
	void addRecord() {
		assertTrue(twoFactorTable.addRecord(user, code));
	}

	@Test
	void getRecord() {
		twoFactorTable.addRecord(user, code);
		assertEquals(code, twoFactorTable.getCode(user));
	}

	@Test
	void deleteRecord() {
		twoFactorTable.addRecord(user, code);
		assertTrue(twoFactorTable.deleteRecord(user));
	}
}