package database;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class DataTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param data - add data object to the database
	 * @return if data inserted into the record
	 */
	public boolean addRecord(Data data) {
		try {
			String query = "INSERT INTO data (Device_id, Image, Person, Created_at)"
					+ " VALUES (?, ?, ?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, data.getDeviceID());
			statement.setBlob(2, data.getImage());
			statement.setString(3, data.getPersonName());
			statement.setString(4, data.getCreatedAt());
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
			String query = "SELECT Id, Device_id, Image, Person, Created_at FROM data WHERE Id=?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				data = new Data(
						resultSet.getInt("Id"),
						resultSet.getString("Device_id"),
						resultSet.getBlob("Image"),
						resultSet.getString("Person"),
						resultSet.getString("Created_at")
				);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
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

	/**
	 * @param deviceId - the id of the device which contains all saved images
	 * @return list of all images with as data objects
	 */
	public ArrayList<Data> getAllImages(String deviceId) {
		ArrayList<Data> allImages = new ArrayList<>();
		try {
			String query = "SELECT Id, Device_id, Image, Person, Created_at FROM data WHERE Device_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Data data = new Data(
						resultSet.getInt("Id"),
						resultSet.getString("Device_id"),
						resultSet.getBlob("Image"),
						resultSet.getString("Person"),
						resultSet.getString("Created_at")
				);
				allImages.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return allImages;
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
	 * @param deviceID - deviceID of the doorbell
	 * @return HashMap of the image as the key and time as the value
	 */
	public Data getRecentImage(String deviceID) {
		Data recentImage = null;
		try {
			String query = "SELECT data.*, Max(Created_at) FROM data WHERE Device_id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				recentImage = new Data(
						resultSet.getInt("id"),
						resultSet.getString("Device_id"),
						resultSet.getBlob("Image"),
						resultSet.getString("Person"),
						resultSet.getString("Max(Created_at)")
				);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recentImage;
	}

	public static void main(String[] args) {
		DataTable dataTable = new DataTable();
		dataTable.connect();
		System.out.println(dataTable.getRecentImage("00000001"));
		dataTable.disconnect();
	}
}