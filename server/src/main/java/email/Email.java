package email;

import java.util.HashSet;

public class Email {

	private HashSet<String> recipients;
	private String subject;

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
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setContents(String contents) {

	}

	public String getContents() {
		return "";
	}
}
