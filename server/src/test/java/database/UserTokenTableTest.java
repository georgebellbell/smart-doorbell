/**
 * @author Dominykas Makarovas
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTokenTableTest {
	private UserTokenTable userTokenTable;
	private AccountTable accountTable;
	private DoorbellTable doorbellTable;
	private User user;
	private Doorbell doorbell;
	private String token;
	private String token2;

	@BeforeEach
	void setUp() {
		userTokenTable = new UserTokenTable();
		accountTable = new AccountTable();
		doorbellTable = new DoorbellTable();

		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		token = "Testing123";
		token2 = "Testing123456";
		doorbell = new Doorbell("QS-12345", "TestDoorbell");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
		userTokenTable.addToken(token, user.getUsername());
	}

	@AfterEach
	void tearDown() {
		userTokenTable.deleteByToken(token);
		userTokenTable.deleteByToken(token2);
		accountTable.deleteRecord(user);
		doorbellTable.unassignDoorbell(doorbell.getId(), user.getUsername());
		doorbellTable.deleteDoorbell(doorbell);
	}

	@Test
	void testAddToken() {
		assertTrue(userTokenTable.addToken(token2, user.getUsername()));
	}

	@Test
	void testGetTokens() {
		ArrayList<String> tokens = userTokenTable.getTokens(user.getUsername());
		for (String s : tokens) {
			assertEquals(token, s);
		}
	}

	@Test
	void testGetTokensByDoorbell() {
		ArrayList<String> tokens = userTokenTable.getTokensByDoorbell(doorbell.getId());
		for (String s : tokens) {
			assertEquals(token, s);
		}
	}

	@Test
	void testGetUserByToken() {
		assertEquals(user, userTokenTable.getUserByToken(token));
	}

	@Test
	void testDeleteToken() {
		assertTrue(userTokenTable.deleteToken(user.getUsername()));
	}

	@Test
	void testDeleteByToken() {
		assertTrue(userTokenTable.deleteByToken(token));
	}
}
