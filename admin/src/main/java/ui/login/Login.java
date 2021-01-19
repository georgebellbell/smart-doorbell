package ui.login;

import connection.Client;
import org.json.JSONObject;
import ui.admin.AdminMenu;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Login extends JFrame {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton cancelButton;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel errorLabel;
	private JPanel panel;

	private Client connection;

	public Login() {
		// Connect to server
		try {
			connection = new Client();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Failed to connect to the server.\nError: " + e.getMessage() +
							"\nMake sure host address is correct and server is running.",
					"Connection", JOptionPane.ERROR_MESSAGE);
			dispose();
			return;
		}

		// Login frame
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(450,220);
		setVisible(true);
		setErrorMessage("");

		loginButton.addActionListener(actionEvent -> {
			String username = usernameField.getText();
			String password = String.valueOf(passwordField.getPassword());

			// Check login details are valid
			String error = validateLoginDetails(username, password);
			if (error != null) {
				errorLabel.setText(error);
				return;
			}

			// Attempt to login to the server
			Thread thread = new Thread(() -> loginToServer(username, password));
			thread.start();
		});

		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					loginButton.doClick();
				}
			}
		});

		cancelButton.addActionListener(actionEvent -> dispose());

	}


	/**
	 * Checks if login details are valid
	 * @param username inputted by user
	 * @param password inputted by user
	 * @return error message
	 */
	private String validateLoginDetails(String username, String password) {
		String error = null;
		if (username.length() == 0 || password.length() == 0) {
			error =  "Username and password cannot be blank";
		}
		return error;
	}

	/**
	 * Starts connection to server and sends login details
	 * @param username inputted by user
	 * @param password inputted by user
	 */
	private void loginToServer(String username, String password) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "login");
		request.put("username", username);
		request.put("password", password);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			dispose();
			new AdminMenu(connection);
		} else {
			setErrorMessage(response.getString("message"));
		}

	}

	private void setErrorMessage(String error) {
		errorLabel.setText(error);
	}

	public static void main(String[] args) {
		new Login();
	}
}
