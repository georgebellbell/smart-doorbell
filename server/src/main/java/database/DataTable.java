package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataTable {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	PreparedStatement statement;
	/**
	 * Add record to the data table with necessary data
	 */
	public boolean addRecord(Data data) {
		try {
			String query = "INSERT INTO data (Id, Username, Image, Person, Created_at)"
					+ " VALUES (?, ?, ?, ?, ?)";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setInt(1, data.getId());
			statement.setString(2,data.getUsername());
			statement.setString(3, data.getImage());
			statement.setString(4, data.getPerson_name());
			statement.setString(5, data.getCreated_at());
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Get record by id, returns data object
	 */
	public Data getRecord(int id){
		Data data = null;
		try {
			String query = "SELECT Id, Username, Image, Person, Created_at FROM data WHERE Id=?";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				data = new Data(
						resultSet.getInt("Id"),
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
	 * Delete a record from the data table if exists
	 */
	public boolean deleteRecord(int id) {
		try {
			String query = "DELETE FROM data WHERE Id=?";
			statement = databaseConnection.conn.prepareStatement(query);
			statement.setInt(1, id);
			statement.execute();
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
