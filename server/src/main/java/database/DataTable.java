package database;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DataTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param data - add data object to the database
	 * @return if data inserted into the record
	 */
	public boolean addRecord(Data data) {
		try {
			String query = "INSERT INTO data (Device_id, Image, Person, Last_used)"
					+ " VALUES (?, ?, ?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, data.getDeviceID());
			Blob blobImage = conn.createBlob();
			statement.setBlob(2, blobImage);
			statement.setString(3, data.getPersonName());
			statement.setString(4, data.getLastUsed());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param id - id of data to retrieve
	 * @return data object if exists in the database
	 */
	public Data getRecord(int id) {
		Data data = null;
		try {
			String query = "SELECT Id, Device_id, Image, Person, Last_used FROM data WHERE Id=?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Blob blob = resultSet.getBlob("Image");
				byte[] image = blob.getBytes(1, (int) blob.length());
				data = new Data(
						resultSet.getInt("id"),
						resultSet.getString("Device_id"),
						image,
						resultSet.getString("Person"),
						resultSet.getString("Last_used")
				);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * @param deviceId - the id of the device which contains all saved images
	 * @return list of all images with as data objects
	 */
	public ArrayList<Data> getAllImages(String deviceId) {
		ArrayList<Data> allImages = new ArrayList<>();
		try {
			String query = "SELECT Id, Device_id, Image, Person, Last_used FROM data WHERE Device_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				try {
					Blob blob = resultSet.getBlob("Image");
					byte[] image = blob.getBytes(1, (int) blob.length());
					Data data = new Data(
							resultSet.getInt("Id"),
							resultSet.getString("Device_id"),
							image,
							resultSet.getString("Person"),
							resultSet.getString("Last_used")
					);
					allImages.add(data);
				} catch (SQLException ignored) {
					// Blob error
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return allImages;
	}

	/**
	 * @return total images stored in the database
	 */
	public int getTotalImages() {
		int total = 0;
		try {
			String query = "SELECT COUNT(*) FROM data";
			statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			total = resultSet.getInt(1);
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	/**
	 * @param username - username of the doorbell
	 * @return HashMap of the image as the key and time as the value
	 */
	public Data getRecentImage(String username) {
		Data recentImage = null;
		try {
			String query = "SELECT * FROM data WHERE Last_used = " +
					"(SELECT Max(Last_used) FROM data WHERE Device_id IN" +
					" (SELECT Pi_id FROM doorbelluser d2 WHERE Username = ?))" +
					" AND Device_id IN (SELECT Pi_id FROM doorbelluser d2 WHERE Username = ?)" +
					" LIMIT 1";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, username);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Blob blob = resultSet.getBlob("Image");
				byte[] image = blob.getBytes(1, (int) blob.length());
				recentImage = new Data(
						resultSet.getInt("id"),
						resultSet.getString("Device_id"),
						image,
						resultSet.getString("Person"),
						resultSet.getString("Last_used")
				);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recentImage;
	}

	/**
	 * @param imageID - ID of the image to update in the database
	 * @return if record updated
	 */
	public boolean updateData(Integer imageID) {
		try {
			String query = "UPDATE data Set Last_used = ? WHERE Id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			statement.setInt(2, imageID);
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param id - id of image stored in the database
	 * @param name - name to change the person to be displayed in the app
	 * @return if name has been changed
	 */
	public boolean changeName(int id, String name) {
		try {
			String query = "UPDATE data Set Person=? WHERE Id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, name);
			statement.setInt(2, id);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param id - id of the data to delete
	 * @return if record deleted
	 */
	public boolean deleteRecordById(int id) {
		try {
			String query = "DELETE FROM data WHERE Id=?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}