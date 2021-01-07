//package database;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DataTableTest {
//	private DataTable dataTable;
//	private Data data;
//
//	@BeforeEach
//	void setUp() {
//		dataTable = new DataTable();
//		dataTable.connect();
//		//data = new Data("Dom", dataTable.getRecord("Dom").getImage(), "Dom's mum");
//	}
//
//	@AfterEach
//	public void afterEach() {
//		dataTable.deleteRecordById(data.getDeviceID(), data.getPersonName());
//		dataTable.disconnect();
//	}
//
//	@Test
//	void testAddRecord() {
//		assertTrue(dataTable.addRecord(data));
//	}
//
//	@Test
//	void testGetRecord() {
//		dataTable.addRecord(data);
//		assertEquals(dataTable.getRecord(data.getDeviceID()), data);
//	}
//
//	@Test
//	void testDeleteRecord() {
//		dataTable.addRecord(data);
//		assertTrue(dataTable.deleteRecordById(data.getDeviceID(), data.getPersonName()));
//	}
//}