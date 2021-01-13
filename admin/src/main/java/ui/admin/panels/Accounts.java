package ui.admin.panels;

import connection.Client;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Accounts extends AdminPanel {
	private JPanel accountPanel;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton searchButton;
	private JPanel userInfoPanel;
	private JTextField usernameField;
	private JTextField emailField;
	private JTextField devicesField;
	private JButton accountSaveChangesButton;
	private JTextField createdField;
	private JButton accountDeleteButton;
	private JTextField roleField;
	private JButton accountResetPasswordButton;
	private JPanel root;

	private String displayedUser;

	public Accounts() {
		clearUserInformation();

		searchButton.addActionListener(actionEvent -> {
			String username = searchField.getText();
			Thread t = new Thread(() -> searchUser(username));
			t.start();
		});

		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					searchButton.doClick();
				}
			}
		});

		accountSaveChangesButton.addActionListener(actionEvent -> {
			String newUsername = usernameField.getText();
			String newEmail = emailField.getText();
			Thread t = new Thread(() -> updateUser(newUsername, newEmail));
			t.start();
		});

		accountDeleteButton.addActionListener(actionEvent -> {
			Thread t = new Thread(() -> deleteUser(displayedUser));
			t.start();
		});

		accountResetPasswordButton.addActionListener(actionEvent -> {
			Thread t = new Thread(() -> resetUserPassword(displayedUser));
			t.start();
		});
	}

	/**
	 * Sends request to server to search for user and populates user panel if successful
	 * @param username - Username of user being searched for
	 */
	private void searchUser(String username) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "user");
		request.put("username", username);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			populateUserInformation(
					response.getString("username"),
					response.getString("email"),
					response.getString("role"),
					response.getString("time"),
					response.getString("devices"));
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account not found", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Populates account panel with user information
	 * @param username - Username of user
	 * @param email - Email of user
	 * @param role - Role of user
	 * @param time - Creation time of user
	 * @param devices - Doorbell devices of user
	 */
	private void populateUserInformation(String username, String email, String role, String time, String devices) {
		displayedUser = username;
		usernameField.setText(username);
		emailField.setText(email);
		roleField.setText(role);
		createdField.setText(time);
		devicesField.setText(devices);
		userInfoPanel.setVisible(true);
	}

	/**
	 * Clears fields in account panel
	 */
	private void clearUserInformation() {
		populateUserInformation("", "", "", "", "");
		userInfoPanel.setVisible(false);
	}

	private void updateUser(String newUsername, String newEmail) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "update");
		request.put("username", displayedUser);
		request.put("newusername", newUsername);
		request.put("newemail", newEmail);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			displayedUser = newUsername;
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Sends a request to delete user to server and creates a popup message if it was successful
	 * @param username - Username of user being deleted
	 */
	private void deleteUser(String username) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "deleteuser");
		request.put("username", username);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			clearUserInformation();
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void resetUserPassword(String username) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "newpassword");
		request.put("username", username);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account", JOptionPane.ERROR_MESSAGE);
		}
	}
}
