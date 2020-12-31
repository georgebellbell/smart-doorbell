package server;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {

	private Protocol protocol;

	@BeforeEach
	void setup() {
		protocol = new Protocol();
	}

	@Test
	void testValidRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", "Dom");
		request.put("password", "Password");

		assertTrue(protocol.isRequestValid(request.toString()));
	}
}