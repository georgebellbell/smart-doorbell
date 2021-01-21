package server.protocol;

import database.AccountTable;
import database.User;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProtocolTest {
	private Protocol protocol;
	private User testUser = new User("testuser",
			"quicksolutions.doorbell@gmail.com", "password", "user");
	private User testAdmin = new User("testadmin",
			"quicksolutions.doorbell@gmail.com", "password", "admin");
	private String testToken = "testToken23443534534556546";
	private AccountTable accountTable = new AccountTable();

	@BeforeEach
	void setup() {
		protocol = new UserProtocol();
		accountTable.addRecord(testUser);
		accountTable.deleteRecord(testAdmin);
	}

	@AfterEach
	void tearDown() {
		accountTable.deleteRecord(testUser);
		accountTable.deleteRecord(testAdmin);
	}

	@Test
	void testValidRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "Dom");
		request.put("password", "Password");
		request.put("token", "20389402938498324");
		assertTrue(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testRequestWithoutToken() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "Dom");
		request.put("password", "Password");
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testInvalidJSONRequest() {
		JSONObject request = new JSONObject();
		request.put("request","invalidrequest");
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testEmptyJSONRequest() {
		JSONObject request = new JSONObject();
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testEmptyStringRequest() {
		String request = "";
		assertFalse(protocol.isRequestValid(request));
	}

	@Test
	void testInvalidStringRequest() {
		String request = "login,Dom,Password,20389402938498324";
		assertFalse(protocol.isRequestValid(request));
	}

	@Test
	void testMissingAllRequiredJSONKeysRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testMissingPartialRequiredJSONKeysRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "Dom");
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testLoginCorrectDetails() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testUser.getUsername());
		request.put("password", testUser.getPassword());
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testLoginIncorrectDetails() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testUser.getUsername());
		request.put("password", "incorrect");
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testLoginNonUserAccount() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testAdmin.getUsername());
		request.put("password", testAdmin.getPassword());
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("fail", response.getString("response"));
	}

}