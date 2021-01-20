package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PollingTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param deviceID - id of the doorbell to be added
	 * @param message - message to send to the doorbell
	 * @return - if poll added to table
	 */
	public boolean createPoll(String deviceID, String message) {
		try {
			String query = "INSERT INTO polling (Device_id, Message)"
					+ " VALUES (?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceID);
			statement.setString(2, message);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e){
			return false;
		}
	}
	
	/**
	 * @param id - id of the doorbell to retrieve the messages
	 * @return all messages for device
	 */
	public ArrayList<String> getPolls(String id) {
		ArrayList<String> messages = new ArrayList<>();
		try {
			String query = "SELECT Message FROM polling WHERE Device_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
				messages.add(resultSet.getString("Message"));
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}

	/**
	 * @param deviceID - id of the doorbell to delete
	 * @return if doorbell deleted
	 */
	public boolean deletePolls(String deviceID) {
		try {
			String query = "DELETE FROM polling WHERE Device_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceID);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
