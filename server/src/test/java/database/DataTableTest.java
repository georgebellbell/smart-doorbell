package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataTableTest {
	private DataTable dataTable;
	private Data data;

	@BeforeEach
	void setUp() {
		dataTable = new DataTable();
		dataTable.connect();
		data = new Data("Dom", "dom.jpg", "Dom's mum");
	}

	@AfterEach
	public void afterEach() {
		dataTable.deleteRecord(data.getUsername(), data.getPerson_name());
		dataTable.disconnect();
	}

	@Test
	void testAddRecord() {
		assertTrue(dataTable.addRecord(data));
	}

	@Test
	void testGetRecord() {
		dataTable.addRecord(data);
		assertEquals(dataTable.getRecord(data.getUsername()), data);
	}

	@Test
	void testDeleteRecord() {
		dataTable.addRecord(data);
		assertTrue(dataTable.deleteRecord(data.getUsername(), data.getPerson_name()));
	}
}