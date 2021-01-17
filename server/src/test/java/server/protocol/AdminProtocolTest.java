package server.protocol;

import database.User;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminProtocolTest {
	private AdminProtocol protocol;
	private static AdminProtocol loginProtocol;

	@BeforeAll
	static void init() {
		loginProtocol = new AdminProtocol();
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "admin");
		request.put("password", "password");
		loginProtocol.setRequest(request.toString());
		loginProtocol.processInput();
	}

	@BeforeEach
	void setup() {
		protocol = new AdminProtocol();
	}

	@Test
	void testValidRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "admin");
		request.put("password", "password");

		assertTrue(protocol.isRequestValid(request.toString()));
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
		String request = "login,Admin,Password";
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
		request.put("username", "admin");
		assertFalse(protocol.isRequestValid(request.toString()));
	}

	@Test
	void testLoginCorrectDetails() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "admin");
		request.put("password", "password");
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testLoginIncorrectDetails() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "admin");
		request.put("password", "incorrect");
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testLoginNonAdminAccount() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "jack");
		request.put("password", "password");
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testRequestWithoutLogin() {
		JSONObject request = new JSONObject();
		request.put("request","analysis");
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("invalid", response.getString("response"));
	}
}