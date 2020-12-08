package email;

import java.util.HashSet;

public class Email {

	HashSet<String> recipients;

	public Email() {
		recipients = new HashSet<String>();
	}

	public void addRecipient(String email) {
		recipients.add(email);
	}

	public HashSet<String> getRecipients() {
		return recipients;
	}

	public void setSubject(String subject) {

	}

	public String getSubject() {
		return "";
	}
}
