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
	private Data data;

	@BeforeEach
	void setUp() {
		doorbellTable = new DoorbellTable();
		accountTable = new AccountTable();
		dataTable = new DataTable();

		user = new User("Test", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		data = new Data(doorbell.getId(), null, "Test");

		accountTable.addRecord(user);
	}

	@AfterEach
	void tearDown() {
		dataTable.deleteRecordById(data.getImageID());
		accountTable.deleteRecord(user);
		doorbellTable.deleteDoorbell(doorbell);
	}

	@Test
	void setDoorbell() {
		addNewDoorbell();
		assertTrue(doorbellTable.setDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void addNewDoorbell() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell));
	}

	@Test
	void testAddNewDoorbell() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell.getId()));
	}

	@Test
	void testAddNewDoorbell1() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell.getId(), doorbell.getName()));
	}

	@Test
	void getDoorbellName() {
		addNewDoorbell();
		assertEquals(doorbellTable.getDoorbellName(doorbell.getId()), doorbell.getName());
	}

	@Test
	void getUsers() {
		setDoorbell();
		ArrayList<String> users = doorbellTable.getUsers(doorbell.getId());
		assertEquals(users.get(0), user.getUsername());
	}

	@Test
	void getTotalDoorbells() {

	}

	@Test
	void getDoorbellPieData() {
	}

	@Test
	void getDoorbells() {
		setDoorbell();
		String id = doorbellTable.getDoorbells(user.getUsername()).getJSONObject(0).getString("id");
		assertEquals(id, doorbell.getId());
	}

	@Test
	void doorbellExists() {
		addNewDoorbell();
		assertTrue(doorbellTable.doorbellExists(doorbell.getId()));
	}

	@Test
	void isUserAssignedDoorbell() {
		setDoorbell();
		assertTrue(doorbellTable.isUserAssignedDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void updateDoorbell() {
		addNewDoorbell();
		assertTrue(doorbellTable.updateDoorbell(doorbell.getId(), "newName"));
	}

	@Test
	void unassignDoorbell() {
		addNewDoorbell();
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