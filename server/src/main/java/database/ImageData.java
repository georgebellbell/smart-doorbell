/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package database;

import java.util.Objects;

public class ImageData {
	// Object properties
	private Integer imageID;
	private byte[] image;
	private String deviceID;
	private String personName;
	private String lastUsed;

	// Double constructor for when retrieving data from database and when adding to the database
	public ImageData(String deviceID, byte[] image, String personName) {
		this(-1, deviceID, image, personName, null);
	}

	public ImageData(Integer imageID, String deviceID, byte[] image, String personName, String lastUsed) {
		this.imageID = imageID;
		this.deviceID = deviceID;
		this.image = image;
		this.personName = personName;
		this.lastUsed = lastUsed;
	}

	public ImageData(String deviceID, byte[] image, String personName, String used) {
		this(-1, deviceID, image, personName, used);
	}

	public Integer getImageID() { return imageID; }
	public void setImageID(int imageID) { this.imageID = imageID; }
	public byte[] getImage() { return image; }
	public void setImage(byte[] image) { this.image = image; }
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}

	@Override
	public String toString() {
		return "Data{" +
				"imageID=" + imageID +
				", image=" + image +
				", deviceID='" + deviceID + '\'' +
				", personName='" + personName + '\'' +
				", createdAt='" + lastUsed + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImageData imageData = (ImageData) o;
		return imageID == imageData.imageID &&
				Objects.equals(image, imageData.image) &&
				Objects.equals(deviceID, imageData.deviceID) &&
				Objects.equals(personName, imageData.personName) &&
				Objects.equals(lastUsed, imageData.lastUsed);
	}

	@Override
	public int hashCode() {
		return Objects.hash(imageID, image, deviceID, personName, lastUsed);
	}
}
