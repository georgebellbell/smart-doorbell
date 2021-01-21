package server.protocol;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserProtocolTest {
	private Protocol protocol;

	@BeforeEach
	void setup() {
		protocol = new UserProtocol();
	}

	@AfterEach
	void tearDown() {
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

}