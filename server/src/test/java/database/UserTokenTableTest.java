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

	@BeforeEach
	void setUp() {
		userTokenTable = new UserTokenTable();
		accountTable = new AccountTable();
		doorbellTable = new DoorbellTable();

		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		token = "Testing123";
		doorbell = new Doorbell("QS-12345", "TestDoorbell");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
	}

	@AfterEach
	void tearDown() {
		accountTable.deleteRecord(user);
		doorbellTable.unassignDoorbell(doorbell.getId(), user.getUsername());
		doorbellTable.deleteDoorbell(doorbell);
	}

	@Test
	void addToken() {
		assertTrue(userTokenTable.addToken(token, user.getUsername()));
	}

	@Test
	void getTokens() {
		addToken();
		ArrayList<String> tokens = userTokenTable.getTokens(user.getUsername());
		for (int i = 0; i < tokens.size(); i++) {
			assertEquals(tokens.get(i), token);
		}
	}

	@Test
	void getTokensByDoorbell() {
		addToken();
		ArrayList<String> tokens = userTokenTable.getTokensByDoorbell(doorbell.getId());
		for (int i = 0; i < tokens.size(); i++) {
			assertEquals(tokens.get(i), token);
		}
	}

	@Test
	void getUserByToken() {
		addToken();
		assertEquals(userTokenTable.getUserByToken(token), user);
	}

	@Test
	void deleteToken() {
		assertTrue(userTokenTable.deleteToken(user.getUsername()));
	}

	@Test
	void deleteByToken() {
		assertTrue(userTokenTable.deleteByToken(token));
	}
}