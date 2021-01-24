package facialrecognition;

import database.Doorbell;
import database.DoorbellTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.codec.Base64;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FaceSimilarityTest {
	private FaceSimilarity faceSimilarity;
	private DoorbellTable doorbellTable;
	private Doorbell doorbell;
	private byte[] image;

	@BeforeEach
	void setUp() throws IOException {
		doorbellTable = new DoorbellTable();
		faceSimilarity = new FaceSimilarity();
		File fi = new File(getClass().getClassLoader().getResource("190219257.png").getPath());
		byte[] fileContent = Files.readAllBytes(fi.toPath());
		image = Base64.decode(fileContent);
		doorbell = new Doorbell("QS-12345", "TestDoorbell");

		doorbellTable.addNewDoorbell(doorbell);
	}

	@AfterEach
	void tearDown() {
		doorbellTable.deleteDoorbell(doorbell);
	}

	@Test
	void compareFaces() throws IOException {
	}
}