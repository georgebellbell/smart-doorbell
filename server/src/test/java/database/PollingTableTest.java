package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PollingTableTest {
	private PollingTable pollingTable;
	private DoorbellTable doorbellTable;
	private Doorbell doorbell;
	private String message;

	@BeforeEach
	void setUp() {
		pollingTable = new PollingTable();
		doorbellTable = new DoorbellTable();

		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		message = "OPEN DOOR";

		doorbellTable.addNewDoorbell(doorbell);
	}

	@AfterEach
	void tearDown() {
		pollingTable.deletePolls(doorbell.getId());
	}

	@Test
	void createPoll() {
		assertTrue(pollingTable.createPoll(doorbell.getId(), message));
	}

	@Test
	void testGetPolls() {
		ArrayList<String> messages = pollingTable.getPolls(doorbell.getId());
		for (int i = 0; i < messages.size(); i++) {
			assertEquals(message, messages.get(i));
		}
	}

	@Test
	void testDeletePolls() {
		assertTrue(pollingTable.deletePolls(doorbell.getId()));
	}
}