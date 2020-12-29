package database;

import com.mysql.cj.jdbc.Blob;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			statement.setString(1, data.getUsername());
			statement.setString(2, data.getImage());
			statement.setString(3, data.getPerson_name());
			statement.setString(4, data.getCreated_at());
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
	public Data getRecord(String username) {
		Data data = null;
		try {
			String query = "SELECT Username, Image, Person, Created_at FROM data WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				data = new Data(
						resultSet.getString("Username"),
						resultSet.getString("Image"),
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
	 * @param username - username of the user
	 * @param person   - person to delete the image of from the database
	 * @return if record deleted
	 */
	public boolean deleteRecord(String username, String person) {
		try {
			String query = "DELETE FROM data WHERE Username=? AND Person=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, person);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param username - what will
	 * @return
	 */
	public JSONArray getAllImages(String username) {
		JSONArray allImages = new JSONArray();
		try {
			String query = "SELECT ID, Image, Person, Created_at FROM data WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", resultSet.getString("Id"));
				jsonObject.put("image", resultSet.getBlob("Image"));
				jsonObject.put("person", resultSet.getString("Person"));
				jsonObject.put("created_at", resultSet.getString("Created_at"));
				allImages.put(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return allImages;
	}
}