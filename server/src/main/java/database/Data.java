package database;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Data {
	// Object properties
	private int imageID;
	private Blob image;
	private String deviceID;
	private String personName;
	private String createdAt;

	// Double constructor for when retrieving data from database and when adding to the database
	public Data(String deviceID, Blob image, String personName) {
		this(-1, deviceID, image, personName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
	public Data(int imageID, String deviceID, Blob image, String personName, String created_at) {
		this.imageID = imageID;
		this.deviceID = deviceID;
		this.image = image;
		this.personName = personName;
		this.createdAt = created_at;
	}

	public int getImageID() { return imageID; }
	public void setImageID(int imageID) { this.imageID = imageID; }
	public Blob getImage() { return image; }
	public void setImage(Blob image) { this.image = image; }
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
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Data{" +
				"imageID=" + imageID +
				", image=" + image +
				", deviceID='" + deviceID + '\'' +
				", personName='" + personName + '\'' +
				", createdAt='" + createdAt + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Data data = (Data) o;
		return imageID == data.imageID &&
				Objects.equals(image, data.image) &&
				Objects.equals(deviceID, data.deviceID) &&
				Objects.equals(personName, data.personName) &&
				Objects.equals(createdAt, data.createdAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(imageID, image, deviceID, personName, createdAt);
	}
}
