package email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

	private Email email;

	@BeforeEach
	void setup() {
		email = new Email();
	}

	@Test
	void testAddRecipientToEmail() {
		String recipient = "quicksolutions.doorbell@gmail.com";
		email.addRecipient(recipient);

		assertTrue(email.getRecipients().contains(recipient));
	}

	@Test
	void testAddMultipleRecipientsToEmail() {
		String recipient1 = "quicksolutions.doorbell@gmail.com";
		String recipient2 = "quicksolutions.doorbell2@gmail.com";
		email.addRecipient(recipient1);
		email.addRecipient(recipient2);

		assertTrue(email.getRecipients().contains(recipient1) &&
				email.getRecipients().contains(recipient2));
	}

	@Test
	void testAddDuplicateRecipientToEmail() {
		String recipient = "quicksolutions.doorbell@gmail.com";
		email.addRecipient(recipient);
		email.addRecipient(recipient);
		email.addRecipient(recipient);
		assertEquals(email.getRecipients().size(), 1);
	}

	@Test
	void testSetEmailSubject() {
		String subject = "Test";
		email.setSubject(subject);
		assertEquals(email.getSubject(), subject);
	}

	@Test
	void testSetEmailContents() {
		String text = "Test message";
		email.setContents(text);
		assertEquals(email.getContents(), text);
	}
}