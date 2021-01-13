package ui.admin.panels;

import connection.Client;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Doorbell extends AdminPanel {
	private JPanel doorbellPanel;
	private JPanel doorbellSearchPanel;
	private JTextField searchDoorbellField;
	private JButton searchDoorbellButton;
	private JPanel doorbellInfoPanel;
	private JTextField doorbellIdField;
	private JTextField doorbellFacesField;
	private JButton viewFacesButton;
	private JButton saveDoorbellChangesButton;
	private JButton deleteDoorbellButton;
	private JTextField doorbellNameField;
	private JPanel root;


	private String displayedDoorbell;
	private JSONArray currentDoorbellFaces;
	private JSONArray currentDoorbellUsers;

	public Doorbell() {
		clearDoorbellInformation();

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
}
