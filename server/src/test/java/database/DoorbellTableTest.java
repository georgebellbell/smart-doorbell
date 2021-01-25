package database;

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
	private ImageData imageData;

	@BeforeEach
	void setUp() {
		doorbellTable = new DoorbellTable();
		accountTable = new AccountTable();
		dataTable = new DataTable();

		user = new User("TestUser", "quicksolutions.doorbell@gmail.com",
				"Password", "user");
		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		doorbell2 = new Doorbell("QS-67891", "TestDoorbell2");
		imageData = new ImageData(doorbell.getId(), null, "Test");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
	}

	@AfterEach
	void tearDown() {
		dataTable.deleteRecordById(imageData.getImageID());
		accountTable.deleteRecord(user);
		doorbellTable.deleteDoorbell(doorbell);
		doorbellTable.deleteDoorbell(doorbell2);
	}

	@Test
	void testSetDoorbell() {
		assertTrue(doorbellTable.setDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void testAddNewDoorbell() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2));
	}

	@Test
	void testAddNewDoorbellById() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2.getId()));
	}

	@Test
	void testAddNewDoorbellByIdAndName() {
		assertTrue(doorbellTable.addNewDoorbell(doorbell2.getId(), doorbell2.getName()));
	}

	@Test
	void testGetDoorbellName() {
		assertEquals(doorbell.getName(), doorbellTable.getDoorbellName(doorbell.getId()));
	}

	@Test
	void testGetUsers() {
		ArrayList<String> users = doorbellTable.getUsers(doorbell.getId());
		assertEquals(user.getUsername(), users.get(0));
	}

	@Test
	void testGetDoorbells() {
		String id = doorbellTable.getDoorbells(user.getUsername()).getJSONObject(0).getString("id");
		assertEquals(doorbell.getId(), id);
	}

	@Test
	void testDoorbellExists() {
		assertTrue(doorbellTable.doorbellExists(doorbell.getId()));
	}

	@Test
	void testIsUserAssignedDoorbell() {
		assertTrue(doorbellTable.isUserAssignedDoorbell(user.getUsername(), doorbell.getId()));
	}

	@Test
	void testUpdateDoorbell() {
		assertTrue(doorbellTable.updateDoorbell(doorbell.getId(), "newName"));
	}

	@Test
	void testUnassignDoorbell() {
		assertTrue(doorbellTable.unassignDoorbell(doorbell.getId(), user.getUsername()));
	}

	@Test
	void testDeleteUserDoorbells() { assertTrue(doorbellTable.deleteUserDoorbells(user.getUsername())); }

	@Test
	void testDeleteUsersFromDoorbell() {
		assertTrue(doorbellTable.deleteUsersFromDoorbell(doorbell.getId()));
	}

	@Test
	void testDeleteDoorbellById() {
		assertTrue(doorbellTable.deleteDoorbell(doorbell.getId()));
	}

	@Test
	void testDeleteDoorbell() {
		assertTrue(doorbellTable.deleteDoorbell(doorbell));
	}
}