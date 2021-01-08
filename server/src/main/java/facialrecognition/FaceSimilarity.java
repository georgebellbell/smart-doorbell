package facialrecognition;

import database.Data;
import database.DataTable;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.FacePatchFeature.Extractor;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;

import javax.imageio.ImageIO;

public class FaceSimilarity {
	DataTable dataTable = new DataTable();

	public boolean compareFaces(byte[] doorbellImage, String deviceID) throws IOException {
		//haar cascade detector used to find faces
		final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_alt2.load();

		//keypoint-enhanced detector to find facial keypoints for the face
		final FKEFaceDetector kedetector = new FKEFaceDetector(detector);

		//constructed a feature extractor to extract pixel patched around prominent facial features
		final Extractor extractor = new FacePatchFeature.Extractor();

		//features are compared using the euclidean distance between vectors
		final FaceFVComparator<FacePatchFeature, FloatFV> comparator =
				new FaceFVComparator<>(FloatFVComparison.EUCLIDEAN);

		//FaceSimilarityEngine constructed to run face detector and extract and compare faces in both images
		final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
				new FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage>(kedetector, extractor, comparator);

		//load the two images, a face from database and face from doorbell
		long startTime = System.currentTimeMillis();
		ByteArrayInputStream bais = new ByteArrayInputStream(doorbellImage);
		final FImage image1 = ImageUtilities.createFImage(ImageIO.read(bais));

		dataTable.connect();
		ArrayList<Data> allImages = dataTable.getAllImages(deviceID);
		dataTable.disconnect();
		try {
			for (Data allImage : allImages) {
				byte[] imageFromDB = allImage.getImage().getBytes(1, (int) allImage.getImage().length());
				ByteArrayInputStream bais2 = new ByteArrayInputStream(imageFromDB);
				final FImage imageToCompare = ImageUtilities.createFImage(ImageIO.read(bais2));

				engine.setQuery(image1, "doorbell");
				engine.setTest(imageToCompare, "database");
				engine.performTest();
			}
			//checks through faces in both images for best matching pair
			for (final Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
				// this computes if the images are similar enough to be deemed the same person
				double bestScore = 40;
				for (final Entry<String, Double> matches : e.getValue().entrySet()) {
					if (matches.getValue() < bestScore) {
						return true;
					}
				}
			}
		} catch (SQLException ioException) {
			ioException.printStackTrace();
		}
		return false;
	}
}
