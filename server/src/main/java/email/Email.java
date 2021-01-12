package email;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

public class Email {

	// Email and password information
	private final String EMAIL = "quicksolutions.doorbell@gmail.com";
	private final String PASSWORD = "ReallyGoodPassword1234";

	// SMTP Settings
	private final String SERVER = "smtp.gmail.com";
	private final String PORT = "465";

	private HashSet<String> recipients;
	private String subject;
	private String contents;

	private Properties properties;
	private Session session;

	public Email() {
		// Message information
		recipients = new HashSet<>();
		subject = "";
		contents = "";

		// Set properties for sending email and get session
		setupProperties();
		createSession();
	}

	/**
	 * Set properties for connection settings
	 */
	private void setupProperties() {
		properties = new Properties();
		properties.put("mail.smtp.host", SERVER);
		properties.put("mail.smtp.port", PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");
	}

	/**
	 * Creates session for email to be sent
	 */
	private void createSession() {
		session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(EMAIL, PASSWORD);
					}
				});
	}

	/**
	 * Adds a recipient for email to be sent to
	 * @param email - Email address of recipient
	 */
	public void addRecipient(String email) {
		recipients.add(email);
	}

	/**
	 * Adds multiple recipients for email to be sent to
	 * @param emails - List of emails of recipients
	 */
	public void addRecipients(ArrayList<String> emails) {
		recipients.addAll(emails);
	}

	public HashSet<String> getRecipients() {
		return recipients;
	}

	private InternetAddress[] getInternetAddressRecipients() throws AddressException {
		InternetAddress[] addresses = new InternetAddress[recipients.size()];
		int i = 0;
		for (String address: recipients) {
			addresses[i] = new InternetAddress(address);
			i++;
		}
		return addresses;
	}

	/**
	 * Set the subject header of the email
	 * @param subject - Subject of email
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the HTML contents of the email
	 * @param contents - HTML contents
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

	/**
	 * Sends the email to assigned recipients with subject and contents
	 * @return whether the email was sent successfully
	 */
	public boolean send() {
		try {
			// Create message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(EMAIL));
			message.setRecipients(
					Message.RecipientType.TO,
					getInternetAddressRecipients()
			);
			message.setSubject(subject);
			message.setContent(contents, "text/html");

			Transport.send(message);

			// Email successfully sent
			return true;

		} catch (MessagingException e) {
			// Email not sent
			e.printStackTrace();
			return false;
		}

	}
}
