package database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DataTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param data - add data object to the database
	 * @return if data inserted into the record
	 */
	public boolean addRecord(Data data) {
		try {
			String query = "INSERT INTO data (Username, Image, Person, Created_at)"
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
	 * @param username - username of data to retrieve
	 * @return data object if exists in the database
	 */
	public Data getRecord(int id) {
		Data data = null;
		try {
			String query = "SELECT Username, Image, Person, Created_at FROM data WHERE Id=?";
			statement = conn.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				data = new Data(
						resultSet.getString("Username"),
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
	 * @param deviceId - what will
	 * @return
	 */
	public ArrayList<Data> getAllImages(String deviceId) {
		ArrayList<Data> allImages = new ArrayList<>();
		try {
			String query = "SELECT ID, Image, Person, Created_at FROM data WHERE Device_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Data data = new Data(
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
}