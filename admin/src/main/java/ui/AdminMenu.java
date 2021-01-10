package ui;

import connection.Client;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class AdminMenu extends JFrame{
	private JButton analyticsButton;
	private JPanel panel;
	private JPanel sidePanel;
	private JButton searchAccountButton;
	private JButton logoutButton;
	private JComboBox roleBox;
	private JButton saveChangesButton;
	private JPanel accountPanel;
	private JTextField usernameField;
	private JTextField emailField;
	private JTextField devicesField;
	private JTextField createdField;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton searchButton;
	private JButton deleteUserButton;
	private JPanel userInfoPanel;

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

		searchButton.addActionListener(actionEvent -> {
			String username = searchField.getText();
			Thread t = new Thread(() -> getUserInformation(username));
			t.start();
		});

		deleteUserButton.addActionListener(actionEvent -> {
			String username = searchField.getText();
			Thread t = new Thread(() -> deleteUser(username));
			t.start();
		});
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
			usernameField.setText(response.getString("username"));
			emailField.setText(response.getString("email"));
			devicesField.setText(response.getString("devices"));
			createdField.setText(response.getString("time"));
			roleBox.setSelectedIndex(0);
			userInfoPanel.setVisible(true);
		} else {
			System.out.println(response.getString("message"));
		}
	}

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
			userInfoPanel.setVisible(false);
			JOptionPane.showMessageDialog(this,
					"Account successfully deleted", "Account", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					"Account not deleted", "Account", JOptionPane.ERROR_MESSAGE);
		}
	}

}
