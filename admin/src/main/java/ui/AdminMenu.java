package ui;

import connection.Client;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

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
	private JButton accountDeleteButton;
	private JPanel userInfoPanel;
	private JTextField roleField;
	private JPanel accountPanel;
	private JPanel searchPanel;
	private JButton accountSaveChangesButton;
	private JPanel mainPanel;
	private JPanel analyticsPanel;
	private JButton doorbellButton;
	private JButton emailButton;
	private JPanel doorbellPanel;
	private JPanel emailPanel;
	private JPanel doorbellSearchPanel;
	private JTextField searchDoorbellField;
	private JButton searchDoorbellButton;
	private JPanel doorbellInfoPanel;
	private JTextField doorbellIdField;
	private JTextField doorbellNameField;
	private JTextField doorbellFacesField;
	private JButton viewFacesButton;
	private JButton saveDoorbellChangesButton;
	private JButton deleteDoorbellButton;
	private JPanel sendEmailPanel;
	private JComboBox emailRecipientTypeComboBox;
	private JLabel emailUsernameLabel;
	private JTextField emailUsernameField;
	private JTextField emailDoorbellField;
	private JLabel emailDoorbellLabel;
	private JTextArea emailContentsTextArea;
	private JButton emailSendButton;
	private JLabel emailContentsLabel;
	private JTextField emailSubjectField;
	private JLabel emailSubjectLabel;
	private JButton accountResetPasswordButton;
	private JPanel analyticsBriefPanel;
	private JLabel analyticsUsers;
	private JLabel analyticsAdmins;
	private JLabel analyticsImages;
	private JLabel analyticsDoorbells;
	private JPanel analyticsImagePanel;
	private String displayedUser;
	private String displayedDoorbell;
	private JSONArray currentDoorbellFaces;
	private JSONArray currentDoorbellUsers;

	private Client connection;

	public AdminMenu(Client connection) {
		add(panel);
		setTitle("Quick Solutions: Smart Doorbell Admin Tool");
		setSize(550, 350);
		setVisible(true);
		sidePanel.setSize(new Dimension(200, 0));

		userInfoPanel.setVisible(false);
		doorbellInfoPanel.setVisible(false);

		// Connection
		this.connection = connection;

		// Set current main panel
		setMainPanel("accounts");

		// Set actions for navigation buttons
		searchAccountButton.addActionListener(actionEvent -> setMainPanel("accounts"));
		analyticsButton.addActionListener(actionEvent -> {
			setMainPanel("analytics");
			Thread t = new Thread(this::getAnalytics);
			t.start();
		});
		doorbellButton.addActionListener(actionEvent -> setMainPanel("doorbell"));
		emailButton.addActionListener(actionEvent -> setMainPanel("email"));
		logoutButton.addActionListener(actionEvent -> dispose());

		// Account panel
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

		// Doorbell panel
		searchDoorbellButton.addActionListener(actionEvent -> {
			String id = searchDoorbellField.getText();
			Thread t = new Thread(() -> searchDoorbell(id));
			t.start();
		});

		searchDoorbellField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					searchDoorbellButton.doClick();
				}
			}
		});

		viewFacesButton.addActionListener(actionEvent -> showDoorbellFaces());

		saveDoorbellChangesButton.addActionListener(actionEvent -> {
			String id = displayedDoorbell;
			String name = doorbellNameField.getText();
			Thread t = new Thread(() -> updateDoorbell(id, name));
			t.start();
		});

		deleteDoorbellButton.addActionListener(actionEvent -> {
			String id = displayedDoorbell;
			Thread t = new Thread(() -> deleteDoorbell(id));
			t.start();
		});


		displayEmailRecipientOptions(true, false);

		emailRecipientTypeComboBox.addItemListener(itemEvent -> {
			int selectedItemKey = emailRecipientTypeComboBox.getSelectedIndex();
			switch (selectedItemKey) {
				case 0:
					displayEmailRecipientOptions(true, false);
					break;
				case 1:
					displayEmailRecipientOptions(false, true);
					break;
				case 2:
					displayEmailRecipientOptions(false, false);
					break;
			}
		});

		emailSendButton.addActionListener(actionEvent -> {
			int type = emailRecipientTypeComboBox.getSelectedIndex();
			String recipient = null;
			switch (type) {
				case 0:
					recipient = emailUsernameField.getText();
					break;
				case 1:
					recipient = emailDoorbellField.getText();
					break;
				case 2:
					recipient = "all";
					break;
			}
			String subject = emailSubjectField.getText();
			String contents = emailContentsTextArea.getText();
			String finalContents = contents.replace("\n", "<br>");
			String finalRecipient = recipient;
			Thread t = new Thread(() -> sendEmail(type, finalRecipient, subject, finalContents));
			t.start();
		});
	}

	private void setMainPanel(String panelName) {
		CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
		cardLayout.show(mainPanel, panelName);
	}

	@Override
	public void dispose() {
		connection.close();
		super.dispose();
	}

	/**
	 * Retrieves analytics data from server
	 */
	private void getAnalytics() {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "analysis");

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			populateAnalytics(
					response.getInt("users"),
					response.getInt("admins"),
					response.getInt("images"),
					response.getInt("doorbells"),
					response.getJSONArray("imagegraph"));
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Account not found", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Creates pie chart for image distribution in analytics
	 * @param graphData - Data retrieved by the server
	 * @return pie chart
	 */
	private JFreeChart createAnalyticsPieChart(JSONArray graphData) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>( );
		for (int i=0; i < graphData.length(); i++) {
			JSONObject doorbellInfo = graphData.getJSONObject(i);
			dataset.setValue(doorbellInfo.getString("id"), doorbellInfo.getInt("count"));
		}

		return ChartFactory.createPieChart(
				"Image Distribution by Doorbell",
				dataset,
				true,
				true,
				false);
	}

	/**
	 * Populates the analytics panel
	 * @param users - Total number of users
	 * @param admins - Total number of admins
	 * @param images - Total number of images
	 * @param doorbells - Total number of doorbells
	 * @param imageGraphData - Image distribution graph data
	 */
	private void populateAnalytics(int users, int admins, int images, int doorbells, JSONArray imageGraphData) {
		// Set overview panel
		analyticsUsers.setText("Total Users: " + users);
		analyticsAdmins.setText("Total Admins: " + admins);
		analyticsImages.setText("Total Images: " + images);
		analyticsDoorbells.setText("Total Doorbells: " + doorbells);

		// Image distribution pie chart
		JFreeChart chart = createAnalyticsPieChart(imageGraphData);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setVisible(true);
		analyticsImagePanel.removeAll();
		analyticsImagePanel.add(chartPanel);
		analyticsImagePanel.updateUI();
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

	/**
	 * Populates doorbell information panel with given information
	 * @param id - Id of doorbell
	 * @param name - Name of doorbell
	 * @param faces - Recognised faces from doorbell
	 */
	private void populateDoorbellInformation(String id, String name, JSONArray faces,
											 JSONArray users) {
		// Store current information
		displayedDoorbell = id;
		currentDoorbellFaces = faces;
		currentDoorbellUsers = users;

		// Set fields
		doorbellIdField.setText(id);
		doorbellNameField.setText(name);
		doorbellFacesField.setText(String.format("(%s faces)", faces.length()));

		// Display panel
		doorbellInfoPanel.setVisible(true);
	}

	/**
	 * Clear fields in doorbell information
	 */
	private void clearDoorbellInformation() {
		populateDoorbellInformation("", "", new JSONArray(), new JSONArray());
		doorbellInfoPanel.setVisible(false);
	}

	/**
	 * Sends a request to server to search for a doorbell and populates doorbell info panel if successful
	 * @param id - Doorbell ID of doorbell being searched for
	 */
	private void searchDoorbell(String id) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "searchdoorbell");
		request.put("id", id);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			populateDoorbellInformation(
					response.getString("id"),
					response.getString("name"),
					response.getJSONArray("images"),
					response.getJSONArray("users"));
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell not found", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Shows the faces of the doorbell being shown
	 */
	private void showDoorbellFaces() {
		// Check if there are faces to show
		if (currentDoorbellFaces.length() == 0) {
			JOptionPane.showMessageDialog(this,
					"No faces to show", "Doorbell", JOptionPane.ERROR_MESSAGE);
			return;
		}

		ArrayList<FImage> images = new ArrayList<>();
		for (int i=0; i < currentDoorbellFaces.length(); i++) {
			JSONObject imageObject = currentDoorbellFaces.getJSONObject(i);
			String image = imageObject.getString("image");
			byte[] imageBytes =  java.util.Base64.getDecoder().decode(image.getBytes());
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
			try {
				final FImage imageToDisplay = ImageUtilities.createFImage(ImageIO.read(byteArrayInputStream));
				images.add(imageToDisplay);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		DisplayUtilities.display("Faces from Doorbell " + displayedDoorbell, images);
	}

	/**
	 * Send request to server to update doorbell's name
	 * @param id - Doorbell ID of doorbell being changed
	 * @param name - New name of doorbell
	 */
	private void updateDoorbell(String id, String name) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "updatedoorbell");
		request.put("id", id);
		request.put("name", name);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Sends request to server to delete doorbell and displays popup message on response
	 * @param id - Doorbell ID of doorbell being deleted
	 */
	private void deleteDoorbell(String id) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "deletedoorbell");
		request.put("id", id);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			clearDoorbellInformation();
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void displayEmailRecipientOptions(boolean showUsername, boolean showDoorbell) {
		emailUsernameLabel.setVisible(showUsername);
		emailUsernameField.setVisible(showUsername);
		emailDoorbellLabel.setVisible(showDoorbell);
		emailDoorbellField.setVisible(showDoorbell);
	}

	private void clearEmailForm() {
		emailSubjectField.setText("");
		emailDoorbellField.setText("");
		emailUsernameField.setText("");
		emailContentsTextArea.setText("");
	}

	private void sendEmail(int type, String recipient, String subject, String contents) {
		// Make sure request is not already in progress
		if (connection.isRequestInProgress()) {
			return;
		}

		// Create request
		JSONObject request = new JSONObject();
		request.put("request", "email");
		request.put("type", type);
		request.put("subject", subject);
		request.put("contents", contents);
		request.put("recipient", recipient);

		// Run request
		JSONObject response = connection.run(request);
		if (response.getString("response").equals("success")) {
			clearEmailForm();
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
					response.getString("message"), "Doorbell", JOptionPane.ERROR_MESSAGE);
		}
	}
}
