/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package ui.admin.panels;

import org.json.JSONArray;
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
	private JButton accountSaveChangesButton;
	private JTextField createdField;
	private JButton accountDeleteButton;
	private JTextField roleField;
	private JButton accountResetPasswordButton;
	private JPanel root;
	private JList<String> deviceList;
	private JButton removeDoorbellButton;
	private JTextField newDeviceField;
	private JButton addDoorbellButton;
	private JPanel devicesPanel;
	private JLabel devicesLabel;
	private DefaultListModel<String> listModel;
	private JSONArray currentDevices;

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
			Thread t = new Thread(() -> updateUser(newUsername, newEmail, currentDevices));
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

		addDoorbellButton.addActionListener(actionEvent -> {
			String newDevice = newDeviceField.getText();
			if (newDevice.equals("")) {
				return;
			}
			listModel.addElement(newDevice);
			currentDevices.put(newDevice);
			newDeviceField.setText("");
		});

		newDeviceField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addDoorbellButton.doClick();
				}
			}
		});

		removeDoorbellButton.addActionListener(actionEvent -> {
			int index = deviceList.getSelectedIndex();
			removeDoorbellButton.setVisible(false);
			if (index == -1) {
				return;
			}
			currentDevices.remove(index);
			listModel.removeElementAt(index);
		});

		deviceList.addListSelectionListener(listenerEvent -> {
			String selected = deviceList.getSelectedValue();
			if (selected == null) {
				return;
			}
			removeDoorbellButton.setVisible(true);
			removeDoorbellButton.setText(String.format("Remove '%s'", selected));
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
					response.getJSONArray("devices"));
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
	private void populateUserInformation(String username, String email, String role, String time, JSONArray devices) {
		displayedUser = username;
		currentDevices = devices;
		usernameField.setText(username);
		emailField.setText(email);
		roleField.setText(role);
		createdField.setText(time);
		boolean displayDevices;
		if (role.equals("admin")) {
			displayDevices = false;
		} else {
			displayDevices = true;
			listModel.clear();
			for (int i = 0; i < devices.length(); i++) {
				listModel.addElement(devices.getString(i));
			}
		}

		devicesPanel.setVisible(displayDevices);
		devicesLabel.setVisible(displayDevices);
		removeDoorbellButton.setVisible(false);
		userInfoPanel.setVisible(true);
	}

	/**
	 * Clears fields in account panel
	 */
	private void clearUserInformation() {
		populateUserInformation("", "", "", "", new JSONArray());
		userInfoPanel.setVisible(false);
	}

	private void updateUser(String newUsername, String newEmail, JSONArray newDevices) {
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
		request.put("devices", newDevices);

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

	private void createUIComponents() {
		listModel = new DefaultListModel<>();
		deviceList = new JList<>(listModel);
	}
}
