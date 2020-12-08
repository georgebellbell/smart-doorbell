package email;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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

	private void setupProperties() {
		properties = new Properties();
		properties.put("mail.smtp.host", SERVER);
		properties.put("mail.smtp.port", PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");
	}

	private void createSession() {
		session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(EMAIL, PASSWORD);
					}
				});
	}

	public void addRecipient(String email) {
		recipients.add(email);
	}

	public HashSet<String> getRecipients() {
		return recipients;
	}

	private InternetAddress[] getInternetAddressRecipients() throws AddressException {
		InternetAddress[] addresses = new InternetAddress[recipients.size()];
		int i = 0;
		for (String address: recipients) {
			addresses[i] = new InternetAddress(address);
		}
		return addresses;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

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
