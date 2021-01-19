package database;

import org.json.JSONArray;
import org.json.JSONObject;

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

	/**
	 * @return JSON array with the count of images and ids of the doorbells
	 */
	public JSONArray getDoorbellPieData() {
		JSONArray jsonArray = new JSONArray();
		try {
			String query = "SELECT Device_id, COUNT(*) FROM data Group By Device_id";
			statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String id = resultSet.getString("Device_id");
				int count = resultSet.getInt(2);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("count", count);
				jsonArray.put(jsonObject);
			}
			statement.close();
		} catch (Exception e) {
				e.printStackTrace();
		}
		return jsonArray;
	}

	public JSONArray getDoorbells(String username) {
		JSONArray jsonArray = new JSONArray();
		try {
			String query = "SELECT d2.Pi_id, d.DoorbellName FROM doorbell d, doorbelluser d2 WHERE d2.Pi_id = d.Pi_id" +
					" AND d2.Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String id = resultSet.getString("Pi_id");
				String name = resultSet.getString("DoorbellName");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("name", name);
				jsonArray.put(jsonObject);
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public boolean setDoorbell(String username, String deviceID) {
		try {
			String query = "INSERT INTO doorbelluser (Pi_id, Username)"
					+ " VALUES (?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, deviceID);
			statement.setString(2, username);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e){
			return false;
		}
	}

	public boolean doorbellExists(String doorbellID) {
		boolean exists = false;
		try {
			String query = "SELECT Pi_id FROM doorbell WHERE Pi_id = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, doorbellID);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				exists = true;
			statement.close();
		} catch (SQLException e){
			e.printStackTrace();
		};
		return exists;
	}

	public boolean addNewDoorbell(Doorbell doorbell) {
		return addNewDoorbell(doorbell.getId(), doorbell.getName());
	}

	public boolean addNewDoorbell(String doorbellID) {
		return addNewDoorbell(doorbellID, "Name not set");
	}

	public boolean addNewDoorbell(String doorbellID, String name) {
		try {
			String query = "INSERT INTO doorbell (Pi_id, DoorbellName)"
					+ " VALUES (?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, doorbellID);
			statement.setString(2, name);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e){
			return false;
		}
	}

	public boolean deleteDoorbell(Doorbell doorbell) {
		return deleteDoorbell(doorbell.getId());
	}

	public boolean deleteUserDoorbells(String username) {
		try {
			String query = "DELETE FROM doorbelluser WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isUserAssignedDoorbell(String username, String doorbellID) {
		try {
			String query = "SELECT * FROM doorbelluser WHERE Pi_id = ? AND Username = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, doorbellID);
			statement.setString(2, username);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
				return true;
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
