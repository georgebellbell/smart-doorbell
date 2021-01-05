package ui;

import connection.Client;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton cancelButton;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel errorLabel;
	private JPanel panel;

	public Login() {
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
			loginToServer(username, password);

		});
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
		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "login");
		request.put("username", username);
		request.put("password", password);

		// Create connection and run request
		Client connection = new Client();
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			dispose();
		} else {
			setErrorMessage("Incorrect username/password");
			connection.close();
		}

	}

	private void setErrorMessage(String error) {
		errorLabel.setText(error);
	}

	public static void main(String[] args) {
		new Login();
	}
}
