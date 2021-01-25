package facialrecognition;

import database.ImageData;
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
	private ImageData imageDataDom;
	private ImageData imageDataDale;
	private ImageData imageDataJack;
	private ImageData imageDataGeorge;
	private ImageData imageDataZach;
	private ImageData imageDataDaleDifferentAngle;
	private ImageData imageDataBlank;

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

		imageDataDom = new ImageData(doorbell2.getId(), dom, "Dom");
		imageDataDale = new ImageData(doorbell3.getId(), dale, "Dale");
		imageDataJack = new ImageData(doorbell3.getId(), jack, "Jack");
		imageDataGeorge = new ImageData(doorbell3.getId(), george, "George");
		imageDataZach = new ImageData(doorbell3.getId(), zach, "Zach");
		imageDataBlank = new ImageData(doorbell3.getId(), blankImage, "Blank Image");
		imageDataDaleDifferentAngle = new ImageData(doorbell4.getId(), daleDifferentAngle, "Dale");

		dataTable.addRecord(imageDataDom);
		dataTable.addRecord(imageDataDale);
		dataTable.addRecord(imageDataJack);
		dataTable.addRecord(imageDataGeorge);
		dataTable.addRecord(imageDataZach);
		dataTable.addRecord(imageDataDaleDifferentAngle);
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
		ImageData imageData = null;
		String found = faceSimilarity.compareFaces(imageDataDom.getImage(), doorbell2.getId());
		if (found != null) {
			imageData = dataTable.getRecord(Integer.parseInt(found));
		}
		assert imageData != null;
		assertEquals(imageDataDom.getPersonName(), imageData.getPersonName());
	}

	@Test
	void testCompareToEmptyDoorbellFaces() {
		String found = faceSimilarity.compareFaces(imageDataDom.getImage(), doorbell1.getId());
		assertEquals(null, found);
	}

	@Test
	void testCompareToUnknownFace() {
		String found = faceSimilarity.compareFaces(imageDataDom.getImage(), doorbell3.getId());
		assertEquals(null, found);
	}

	@Test
	void testCompareToSamePerson() {
		ImageData imageData = null;
		String found = faceSimilarity.compareFaces(imageDataDaleDifferentAngle.getImage(), doorbell3.getId());
		if (found != null) {
			imageData = dataTable.getRecord(Integer.parseInt(found));
		}
		assert imageData != null;
		assertEquals(imageDataDaleDifferentAngle.getPersonName(), imageData.getPersonName());
	}

	@Test
	void testCompareToBlank() {
		String found = faceSimilarity.compareFaces(imageDataBlank.getImage(), doorbell3.getId());
		assertEquals(null, found);
	}
}