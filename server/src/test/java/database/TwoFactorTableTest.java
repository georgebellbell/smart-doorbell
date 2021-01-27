/**
 * @author Dominykas Makarovas
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import authentication.TwoFactorAuthentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorTableTest {
	private TwoFactorTable twoFactorTable;
	private TwoFactorAuthentication twoFactor;
	private User user;
	private String code;

	@BeforeEach
	void setUp() {
		twoFactorTable = new TwoFactorTable();
		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "Salt", "User");
		code = "123456";
	}

	@AfterEach
	void tearDown() {
		twoFactorTable.deleteRecord(user);
	}

	@Test
	void testAddRecord() {
		assertTrue(twoFactorTable.addRecord(user, code));
	}

	@Test
	void testGetCode() {
		testAddRecord();
		assertEquals(code, twoFactorTable.getCode(user));
	}

	@Test
	void testDeleteRecord() {
		assertTrue(twoFactorTable.deleteRecord(user));
	}
}
