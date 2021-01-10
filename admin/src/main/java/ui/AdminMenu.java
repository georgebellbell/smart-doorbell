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

	private Client connection;

	public AdminMenu(Client connection) {
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(500, 300);
		setVisible(true);
		sidePanel.setSize(new Dimension(200, 0));

		// Connection
		this.connection = connection;

		Thread t = new Thread(() -> getUserInformation("admin"));
		t.start();
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
		} else {
			System.out.println(response.getString("message"));
		}
	}

}
