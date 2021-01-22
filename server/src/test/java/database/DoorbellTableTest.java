package database;

import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DoorbellTableTest {
	private DoorbellTable doorbellTable;
	private AccountTable accountTable;
	private DataTable dataTable;

	private User user;
	private Doorbell doorbell;
	private Doorbell doorbell2;
	private Data data;

	@BeforeEach
	void setUp() {
		doorbellTable = new DoorbellTable();
		accountTable = new AccountTable();
		dataTable = new DataTable();

		user = new User("TestUser", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		doorbell2 = new Doorbell("QS-67891", "TestDoorbell2");
		data = new Data(doorbell.getId(), null, "Test");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
	}

	@AfterEach
	void tearDown() {
		dataTable.deleteRecordById(data.getImageID());
		accountTable.deleteRecord(user);
		doorbellTable.deleteDoorbell(doorbell);
		doorbellTable.deleteDoorbell(doorbell2);
	}

	@Test
	void setDoorbell() {
		assertTrue(doorbellTable.setDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void addNewDoorbell() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2));
	}

	@Test
	void testAddNewDoorbell() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2.getId()));
	}

	@Test
	void testAddNewDoorbell1() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2.getId(), doorbell2.getName()));
	}

	@Test
	void getDoorbellName() {
		assertEquals(doorbellTable.getDoorbellName(doorbell.getId()), doorbell.getName());
	}

	@Test
	void getUsers() {
		ArrayList<String> users = doorbellTable.getUsers(doorbell.getId());
		assertEquals(users.get(0), user.getUsername());
	}

	@Test
	void getDoorbells() {
		String id = doorbellTable.getDoorbells(user.getUsername()).getJSONObject(0).getString("id");
		assertEquals(id, doorbell.getId());
	}

	@Test
	void doorbellExists() {
		assertTrue(doorbellTable.doorbellExists(doorbell.getId()));
	}

	@Test
	void isUserAssignedDoorbell() {
		assertTrue(doorbellTable.isUserAssignedDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void updateDoorbell() {
		assertTrue(doorbellTable.updateDoorbell(doorbell.getId(), "newName"));
	}

	@Test
	void unassignDoorbell() {
		assertTrue(doorbellTable.unassignDoorbell(doorbell.getId(), user.getUsername()));
	}

	@Test
	void deleteUserDoorbells() {
		doorbellTable.deleteUserDoorbells(user.getUsername());
	}

	@Test
	void deleteUsersFromDoorbell() {
		assertTrue(doorbellTable.deleteUsersFromDoorbell(doorbell.getId()));
	}

	@Test
	void deleteDoorbell() {
		assertTrue(doorbellTable.deleteDoorbell(doorbell.getId()));
	}

	@Test
	void testDeleteDoorbell() {
		assertTrue(doorbellTable.deleteDoorbell(doorbell));
	}
}