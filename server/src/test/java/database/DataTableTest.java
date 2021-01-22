package database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.codec.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DataTableTest {
	private DataTable dataTable;
	private DoorbellTable doorbellTable;
	private AccountTable accountTable;
	private Data data;
	private Data data2;
	private User user;
	private Doorbell doorbell;

	@BeforeEach
	void setUp() throws SQLException, IOException {
		dataTable = new DataTable();
		doorbellTable = new DoorbellTable();
		accountTable = new AccountTable();

		byte[] image = Base64.encode("src/test/resources/testImage.png".getBytes());
		Connection conn = dataTable.getConn();
		Blob blobImage = conn.createBlob();
		blobImage.setBytes(1, image);

		doorbell = new Doorbell("QS-12345", "TestDoorbell");
		data = new Data(doorbell.getId(), blobImage, "Black Image");
		data2 = new Data(doorbell.getId(), blobImage, "Black Image2");
		user = new User("TestUser456", "quicksolutions.doorbell@gmail.com",
				"Password", "user");

		accountTable.addRecord(user);
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.setDoorbell(user.getUsername(), doorbell.getId());
		dataTable.addRecord(data);
	}

	@AfterEach
	void tearDown() {
		doorbellTable.deleteUsersFromDoorbell(doorbell.getId());
		doorbellTable.deleteDoorbell(doorbell);
		dataTable.deleteRecordById(data.getImageID());
		dataTable.deleteRecordById(data2.getImageID());
		accountTable.deleteRecord(user);
	}

	@Test
	void addRecord() {
		assertTrue(dataTable.addRecord(data2));
	}

	@Test
	void getRecord() {
		ArrayList<Data> allImages = dataTable.getAllImages(doorbell.getId());
		Data imageRetrieved = allImages.get(0);
		assertEquals(imageRetrieved.getPersonName(), data.getPersonName());
	}

	@Test
	void getAllImages() {
		ArrayList<Data> allImages = dataTable.getAllImages(doorbell.getId());
		assertTrue(allImages.size() == 1);
	}

	@Test
	void getRecentImage() {
		Data recentImage = dataTable.getAllImages(doorbell.getId()).get(0);
		Integer imageID = recentImage.getImageID();
		dataTable.updateData(imageID);
		assertEquals(recentImage.getImageID(), imageID);
	}

	@Test
	void updateData() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		dataTable.updateData(imageID);
		Data updatedImage = dataTable.getAllImages(doorbell.getId()).get(0);
		String[] updateTime = updatedImage.getCreatedAt().split(" ");
		String[] currentTime  = LocalDateTime
				.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).split(" ");

		assertEquals(updateTime[0], currentTime[0]);
	}

	@Test
	void changeName() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		dataTable.changeName(imageID, "New Person");
		assertTrue(dataTable.getRecord(imageID).getPersonName().equals("New Person"));
	}

	@Test
	void deleteRecordById() {
		Integer imageID = dataTable.getAllImages(doorbell.getId()).get(0).getImageID();
		assertTrue(dataTable.deleteRecordById(imageID));
	}
}