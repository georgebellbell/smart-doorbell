package ui;

import connection.Client;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AdminMenu extends JFrame{
	private JButton analyticsButton;
	private JPanel panel;
	private JPanel sidePanel;
	private JButton searchAccountButton;
	private JButton logoutButton;
	private JTextField usernameField;
	private JTextField emailField;
	private JTextField devicesField;
	private JTextField createdField;
	private JTextField searchField;
	private JButton searchButton;
	private JButton deleteUserButton;
	private JPanel userInfoPanel;
	private JTextField roleField;
	private JPanel accountPanel;
	private JPanel searchPanel;
	private JButton saveChangesButton;
	private JPanel mainPanel;
	private JPanel analyticsPanel;
	private JButton doorbellButton;
	private JButton emailButton;
	private JPanel doorbellPanel;
	private JPanel emailPanel;
	private String displayedUser;

	private Client connection;

	public AdminMenu(Client connection) {
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(500, 300);
		setVisible(true);
		sidePanel.setSize(new Dimension(200, 0));

		userInfoPanel.setVisible(false);

		// Connection
		this.connection = connection;

		// Set current main panel
		setMainPanel("accounts");

		// Set actions for navigation buttons
		searchAccountButton.addActionListener(actionEvent -> setMainPanel("accounts"));
		analyticsButton.addActionListener(actionEvent -> setMainPanel("analytics"));
		doorbellButton.addActionListener(actionEvent -> setMainPanel("doorbell"));
		emailButton.addActionListener(actionEvent -> setMainPanel("email"));


		searchButton.addActionListener(actionEvent -> {
			String username = searchField.getText();
			Thread t = new Thread(() -> getUserInformation(username));
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

		saveChangesButton.addActionListener(actionEvent -> {
			String newUsername = usernameField.getText();
			String newEmail = emailField.getText();
			Thread t = new Thread(() -> updateUser(newUsername, newEmail));
			t.start();
		});

		deleteUserButton.addActionListener(actionEvent -> {
			String username = searchField.getText();
			Thread t = new Thread(() -> deleteUser(username));
			t.start();
		});

		logoutButton.addActionListener(actionEvent -> {
			connection.close();
			dispose();
		});
	}

	private void setMainPanel(String panelName) {
		CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
		cardLayout.show(mainPanel, panelName);
	}

	private void getUserInformation(String username) {
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

}
