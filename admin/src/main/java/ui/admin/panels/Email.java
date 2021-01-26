/**
 * @author Dominykas Makarovas, Jack Reed
 * @version 1.0
 * @since 25/01/2021
 */

package ui.admin.panels;

import org.json.JSONObject;
import javax.swing.*;

public class Email extends AdminPanel {
	private JPanel emailPanel;
	private JPanel sendEmailPanel;
	private JComboBox emailRecipientTypeComboBox;
	private JLabel emailUsernameLabel;
	private JTextField emailUsernameField;
	private JLabel emailDoorbellLabel;
	private JTextField emailDoorbellField;
	private JTextArea emailContentsTextArea;
	private JLabel emailContentsLabel;
	private JButton emailSendButton;
	private JLabel emailSubjectLabel;
	private JTextField emailSubjectField;
	private JPanel root;

	public Email() {
		displayEmailRecipientOption(0);

		emailRecipientTypeComboBox.addItemListener(itemEvent -> {
			int selectedItemKey = emailRecipientTypeComboBox.getSelectedIndex();
			displayEmailRecipientOption(selectedItemKey);
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

	/**
	 * Sets visible the email recipient option for type of email
	 * @param type - Type of email
	 */
	private void displayEmailRecipientOption(int type) {
		switch (type) {
			case 0:
				emailUsernameLabel.setVisible(true);
				emailUsernameField.setVisible(true);
				emailDoorbellLabel.setVisible(false);
				emailDoorbellField.setVisible(false);
				break;
			case 1:
				emailUsernameLabel.setVisible(false);
				emailUsernameField.setVisible(false);
				emailDoorbellLabel.setVisible(true);
				emailDoorbellField.setVisible(true);
				break;
			case 2:
				emailUsernameLabel.setVisible(false);
				emailUsernameField.setVisible(false);
				emailDoorbellLabel.setVisible(false);
				emailDoorbellField.setVisible(false);
				break;
		}
	}

	/**
	 * Clears the contents of the email form
	 */
	private void clearEmailForm() {
		emailSubjectField.setText("");
		emailDoorbellField.setText("");
		emailUsernameField.setText("");
		emailContentsTextArea.setText("");
	}

	/**
	 * Send request to server to send email
	 * @param type - Type of email
	 * @param recipient - Recipient of email
	 * @param subject - Subject of email
	 * @param contents - HTML contents of email
	 */
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
