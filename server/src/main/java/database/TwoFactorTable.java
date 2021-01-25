package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TwoFactorTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * Adds record to twofactor table in the team database
	 * @param user the use who's username will be added to the table
	 * @param code the authentication code
	 * @return true if added successfully
	 */
	public boolean addRecord(User user, String code) {
		try {
			String query = "INSERT INTO twofactor (Username, Code, Expiration_time)"
					+ " VALUES (?, ?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2, code);
			statement.setString(3, LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ofPattern("yyyMdd HH:mm:ss")));
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get code of user if code has not expired
	 * @param user the user who's record will be retrieved from the database
	 * @return code if it's not expired
	 */
	public String getCode(User user) {
		try {
			String query = "SELECT Code  FROM twofactor WHERE Username=? AND Expiration_time>?";
			statement = conn.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyMdd HH:mm:ss")));
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString("Code");
			}
			statement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Deletes the record of the user
	 * @param user takes the user who used the code
	 * @return if record is deleted
	 */
	public boolean deleteRecord(User user) {
		try {
			String query = "DELETE FROM twofactor WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, user.getUsername());
			statement.execute();
			statement.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
