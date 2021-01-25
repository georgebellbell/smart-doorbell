package facialrecognition;

import database.Data;
import database.DataTable;
import database.Doorbell;
import database.DoorbellTable;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FaceSimilarityTest {
	private FaceSimilarity faceSimilarity;
	private DoorbellTable doorbellTable;
	private DataTable dataTable;

	private Doorbell doorbell1;
	private Doorbell doorbell2;
	private Doorbell doorbell3;
	private Doorbell doorbell4;
	private Data dataDom;
	private Data dataDale;
	private Data dataJack;
	private Data dataGeorge;
	private Data dataZach;
	private Data dataDaleDifferentAngle;
	private Data dataBlank;

	@BeforeEach
	void setUp() throws IOException {
		doorbellTable = new DoorbellTable();
		dataTable = new DataTable();
		faceSimilarity = new FaceSimilarity();

		byte[] dom = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("dom.png").getPath()));
		byte[] daleDifferentAngle = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("dale2.png").getPath()));
		byte[] blankImage = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("testImage.png").getPath()));
		byte[] dale = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("dale.png").getPath()));
		byte[] jack = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("jack.png").getPath()));
		byte[] george = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("george.png").getPath()));
		byte[] zach = FileUtils.readFileToByteArray(new File(getClass().getClassLoader().getResource("zach.png").getPath()));

		doorbell1 = new Doorbell("QS-1", "TestDoorbell");
		doorbell2 = new Doorbell("QS-2", "TestDoorbell");
		doorbell3 = new Doorbell("QS-3", "TestDoorbell");
		doorbell4 = new Doorbell("QS-4", "TestDoorbell");

		doorbellTable.addNewDoorbell(doorbell1);
		doorbellTable.addNewDoorbell(doorbell2);
		doorbellTable.addNewDoorbell(doorbell3);
		doorbellTable.addNewDoorbell(doorbell4);

		dataDom = new Data(doorbell2.getId(), dom, "Dom");
		dataDale = new Data(doorbell3.getId(), dale, "Dale");
		dataJack = new Data(doorbell3.getId(), jack, "Jack");
		dataGeorge = new Data(doorbell3.getId(), george, "George");
		dataZach = new Data(doorbell3.getId(), zach, "Zach");
		dataBlank = new Data(doorbell3.getId(), blankImage, "Blank Image");
		dataDaleDifferentAngle = new Data(doorbell4.getId(), daleDifferentAngle, "Dale");

		dataTable.addRecord(dataDom);
		dataTable.addRecord(dataDale);
		dataTable.addRecord(dataJack);
		dataTable.addRecord(dataGeorge);
		dataTable.addRecord(dataZach);
		dataTable.addRecord(dataDaleDifferentAngle);
	}

	@AfterEach
	void tearDown() {
		doorbellTable.deleteDoorbell(doorbell1);
		doorbellTable.deleteDoorbell(doorbell2);
		doorbellTable.deleteDoorbell(doorbell3);
		doorbellTable.deleteDoorbell(doorbell4);
	}

	@Test
	void testCompareDuplicateFaces() {
		Data data = null;
		String found = faceSimilarity.compareFaces(dataDom.getImage(), doorbell2.getId());
		if (found != null) {
			data = dataTable.getRecord(Integer.parseInt(found));
		}
		assert data != null;
		assertEquals(dataDom.getPersonName(), data.getPersonName());
	}

	@Test
	void testCompareToEmptyDoorbellFaces() {
		String found = faceSimilarity.compareFaces(dataDom.getImage(), doorbell1.getId());
		assertEquals(null, found);
	}

	@Test
	void testCompareToUnknownFace() {
		String found = faceSimilarity.compareFaces(dataDom.getImage(), doorbell3.getId());
		assertEquals(null, found);
	}

	@Test
	void testCompareToSamePerson() {
		Data data = null;
		String found = faceSimilarity.compareFaces(dataDaleDifferentAngle.getImage(), doorbell3.getId());
		if (found != null) {
			data = dataTable.getRecord(Integer.parseInt(found));
		}
		assert data != null;
		assertEquals(dataDaleDifferentAngle.getPersonName(), data.getPersonName());
	}

	@Test
	void testCompareToBlank() {
		String found = faceSimilarity.compareFaces(dataBlank.getImage(), doorbell3.getId());
		assertEquals(null, found);
	}
}