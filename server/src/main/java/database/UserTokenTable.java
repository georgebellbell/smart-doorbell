package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserTokenTable extends DatabaseConnection {
	PreparedStatement statement;

	/**
	 * @param token - token to add to the database
	 * @param username - username of the associated user to add the token to
	 * @return
	 */
	public boolean addToken(String token, String username) {
		try {
			String query = "INSERT INTO usertoken (Token, Username)"
					+ " VALUES (?, ?)";
			statement = conn.prepareStatement(query);
			statement.setString(1, token);
			statement.setString(2, username);
			statement.execute();
			statement.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param username - username to delete the token by
	 * @return if token deleted
	 */
	public boolean deleteToken(String username) {
		try {
			String query = "DELETE FROM usertoken WHERE Username=?";
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

	/**
	 * @param username - username to retrieve all the tokens by from the database
	 * @return list of all tokens assigned to the username
	 */
	public ArrayList<String> getTokens(String username) {
		ArrayList<String> allTokens = new ArrayList<>();
		try {
			String query = "SELECT Token FROM usertoken WHERE Username=?";
			statement = conn.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String token = resultSet.getString("Token");
				allTokens.add(token);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allTokens;
	}
}
