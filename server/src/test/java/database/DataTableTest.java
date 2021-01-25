/**
 * @author Dominykas Makarovas
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataTableTest {
	private DataTable dataTable;
	private DoorbellTable doorbellTable;
	private AccountTable accountTable;

	private ImageData imageData;
	private ImageData imageData2;
	private User user;
	private Doorbell doorbell;

	@BeforeEach
	void setUp() {
		dataTable = new DataTable();
		doorbellTable = new DoorbellTable();
		accountTable = new AccountTable();

		byte[] image = java.util.Base64.getEncoder().encode("src/test/resources/testImage.png".getBytes());

		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		imageData = new ImageData(doorbell.getId(), image, "Black Image");
		imageData2 = new ImageData(doorbell.getId(), image, "Black Image2");
		user = new User("TestUser456", "quicksolutions.doorbell@gmail.com",
				"Password", "user");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
		dataTable.addRecord(imageData);
	}

	@AfterEach
	void tearDown() {
		doorbellTable.deleteUsersFromDoorbell(doorbell.getId());
		doorbellTable.deleteDoorbell(doorbell);
		dataTable.deleteRecordById(imageData.getImageID());
		dataTable.deleteRecordById(imageData2.getImageID());
		accountTable.deleteRecord(user);
	}

	@Test
	void testAddRecord() {
		assertTrue(dataTable.addRecord(imageData2));
	}

	@Test
	void testGetRecord() {
		ArrayList<ImageData> allImages = dataTable.getAllImages(doorbell.getId());
		ImageData imageRetrieved = allImages.get(0);
		assertEquals(imageData.getPersonName(), imageRetrieved.getPersonName());
	}

	@Test
	void testGetAllImages() {
		ArrayList<ImageData> allImages = dataTable.getAllImages(doorbell.getId());
		assertEquals(1, allImages.size());
	}

	@Test
	void testGetAllImagesByInvalidDoorbell() {
		ArrayList<ImageData> allImages = dataTable.getAllImages("InvalidDoorbell");
		assertTrue(allImages.isEmpty());
	}

	@Test
	void testGetRecentImage() {
		ImageData recentImage = dataTable.getAllImages(doorbell.getId()).get(0);
		Integer imageID = recentImage.getImageID();
		dataTable.updateData(imageID);
		assertEquals(recentImage.getImageID(), imageID);
	}

	@Test
	void testGetRecentImageByInvalidDoorbell() {
		 assertTrue(dataTable.getAllImages("InvalidDoorbell").isEmpty());

	}

	@Test
	void testUpdateData() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		dataTable.updateData(imageID);
		ImageData updatedImage = dataTable.getAllImages(doorbell.getId()).get(0);
		String[] updateTime = updatedImage.getLastUsed().split(" ");
		String[] currentTime  = LocalDateTime
				.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).split(" ");

		assertEquals(currentTime[0], updateTime[0]);
	}

	@Test
	void testChangeName() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		dataTable.changeName(imageID, "New Person");
		assertEquals("New Person", dataTable.getRecord(imageID).getPersonName());
	}

	@Test
	void testDeleteRecordById() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		assertTrue(dataTable.deleteRecordById(imageID));
	}
}
