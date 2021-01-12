package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DoorbellTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param id - id of the doorbell to retrieve the name of
	 * @return the user friendly name of the doorbell
	 */
	public String getDoorbellName(String id) {
		String doorbellName = null;
		try {
			String query = "SELECT Pi_id, DoorbellName FROM doorbell WHERE Pi_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				doorbellName = resultSet.getString("DoorbellName");
			statement.close();
		} catch (SQLException e) {
			System.out.println("Doorbell ID doesn't exist");
		}
		return doorbellName;
	}

	/**
	 * @param id - id of the doorbell
	 * @return all assigned users to the doorbell
	 */
	public ArrayList<String> getUsers(String id) {
		ArrayList<String> users = new ArrayList<>();
		try {
			String query = "SELECT Username FROM doorbelluser WHERE Pi_id = ? ";
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
				users.add(resultSet.getString("Username"));
			statement.close();
		} catch (SQLException e) {
			System.out.println("Doorbell ID doesn't exist");
		}
		return users;
	}

	/**
	 * @param id - id of the doorbell to delete
	 * @return if doorbell deleted
	 */
	public boolean deleteDoorbell(String id) {
		try {
			String query = "DELETE FROM doorbell WHERE Pi_id=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, id);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param id - id of the doorbell to update the name of
	 * @param name - name to change the doorbell to
	 * @return if sucessfully updated the name
	 */
	public boolean updateDoorbell(String id, String name) {
		try {
			String query = "UPDATE doorbell Set DoorbellName = ? WHERE Pi_id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, name);
			statement.setString(2, id);
			statement.execute();
			statement.close();
			return true;
		}
		catch (SQLException e) {
		System.out.println("Duplicate username");
		return false;
		}
	}

	/**
	 * @return total doorbells stored in the database
	 */
	public int getTotalDoorbells() {
		int total = 0;
		try {
			String query = "SELECT COUNT(*) FROM doorbell";
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
}
