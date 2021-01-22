package server.protocol;

import authentication.TwoFactorAuthentication;
import database.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserProtocolTest {
	private Protocol protocol;
	private String loggedInToken = "123456123456";
	private User testUser = new User("testuser",
			"quicksolutions.doorbell@gmail.com", "password", "user");
	private User testAdmin = new User("testadmin",
			"quicksolutions.doorbell@gmail.com", "password", "admin");
	private String testToken = "testToken23443534534556546";
	private TwoFactorAuthentication twoFactor = new TwoFactorAuthentication(testUser);
	private Doorbell testDoorbell1 = new Doorbell("3040596943", "Test Doorbell 1");
	private Doorbell testDoorbell2 = new Doorbell("3040456945", "Test Doorbell 2");
	private AccountTable accountTable = new AccountTable();
	private DoorbellTable doorbellTable = new DoorbellTable();
	private UserTokenTable userTokenTable = new UserTokenTable();

	@BeforeEach
	void setup() {
		protocol = new UserProtocol();
		accountTable.addRecord(testUser);
		accountTable.deleteRecord(testAdmin);
		doorbellTable.addNewDoorbell(testDoorbell1);
		doorbellTable.addNewDoorbell(testDoorbell2);
		userTokenTable.addToken(loggedInToken, testUser.getUsername());

	}

	@AfterEach
	void tearDown() {
		twoFactor.deleteCode();
		accountTable.deleteRecord(testUser);
		accountTable.deleteRecord(testAdmin);
		doorbellTable.deleteDoorbell(testDoorbell1);
		doorbellTable.deleteDoorbell(testDoorbell2);
		userTokenTable.deleteByToken(loggedInToken);
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

	@Test
	void testLogin2FACodeGenerated() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testUser.getUsername());
		request.put("password", testUser.getPassword());
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		protocol.processRequest();
		assertNotEquals(null, twoFactor.getGeneratedCode());
	}

	@Test
	void testTwoFactorCorrectCode() {
		twoFactor.generateCode();
		JSONObject request = new JSONObject();
		request.put("request","twofactor");
		request.put("username", testUser.getUsername());
		request.put("code", twoFactor.getGeneratedCode());
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testTwoFactorIncorrectCode() {
		twoFactor.generateCode();
		JSONObject request = new JSONObject();
		request.put("request","twofactor");
		request.put("username", testUser.getUsername());
		request.put("code", "000000000000");
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testTwoFactorInvalidCode() {
		twoFactor.generateCode();
		JSONObject request = new JSONObject();
		request.put("request","twofactor");
		request.put("username", testUser.getUsername());
		request.put("code", "AAAAAAAAAA");
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testTwoFactorExpiredCode() {
		JSONObject request = new JSONObject();
		request.put("request","twofactor");
		request.put("username", testUser.getUsername());
		request.put("code", "123456");
		request.put("token", testToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertAll("fail",
				() -> assertEquals("fail", response.getString("response")),
				() -> assertEquals("2FA code has expired, request a new one",
						response.getString("message"))
		);
	}

	@Test
	void testAddDoorbellToUser() {
		JSONObject request = new JSONObject();
		request.put("request","connectdoorbell");
		request.put("doorbellID", testDoorbell1.getId());
		request.put("doorbellname", testDoorbell1.getName());
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testDeleteDoorbellFromUser() {
		doorbellTable.setDoorbell(testUser.getUsername(), testDoorbell1.getId());
		JSONObject request = new JSONObject();
		request.put("request","removedoorbell");
		request.put("doorbellID", testDoorbell1.getId());
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testGetOneAssignedDoorbell() {
		doorbellTable.setDoorbell(testUser.getUsername(), testDoorbell1.getId());
		JSONObject request = new JSONObject();
		request.put("request","getdoorbells");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		JSONArray doorbells = response.getJSONArray("doorbells");
		JSONObject doorbell = doorbells.getJSONObject(0);
		assertAll("success",
				() -> assertEquals("success", response.getString("response")),
				() -> assertEquals(1, doorbells.length()),
				() -> assertEquals(testDoorbell1.getId(), doorbell.getString("id"))
		);
	}

	@Test
	void testGetMultipleAssignedDoorbells() {
		doorbellTable.setDoorbell(testUser.getUsername(), testDoorbell1.getId());
		doorbellTable.setDoorbell(testUser.getUsername(), testDoorbell2.getId());
		JSONObject request = new JSONObject();
		request.put("request","getdoorbells");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		JSONArray doorbells = response.getJSONArray("doorbells");
		assertAll("success",
				() -> assertEquals("success", response.getString("response")),
				() -> assertEquals(2, doorbells.length())
		);
	}

	@Test
	void testGetNoAssignedDoorbells() {
		JSONObject request = new JSONObject();
		request.put("request","getdoorbells");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertAll("fail",
				() -> assertEquals("fail", response.getString("response")),
				() -> assertEquals("You have 0 doorbells assigned", response.getString("message"))
		);
	}

	@Test
	void testOpenDoor() {
		JSONObject request = new JSONObject();
		request.put("request","opendoor");
		request.put("message", "open");
		request.put("doorbellID", testDoorbell1.getId());
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("open", response.getString("response"));
	}

	@Test
	void testCloseDoor() {
		JSONObject request = new JSONObject();
		request.put("request","opendoor");
		request.put("message", "close");
		request.put("doorbellID", testDoorbell1.getId());
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("close", response.getString("response"));
	}

	@Test
	void testChangePassword() {
		JSONObject request = new JSONObject();
		request.put("request","changepassword");
		request.put("password", "NewPassword12345");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testChangeValidEmail() {
		JSONObject request = new JSONObject();
		request.put("request","changeemail");
		request.put("email", "quicksolutions.doorbell2@gmail.com");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testChangeInvalidEmail() {
		JSONObject request = new JSONObject();
		request.put("request","changeemail");
		request.put("email", "newEmail@@@.co.uk");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertAll("fail",
				() -> assertEquals("fail", response.getString("response")),
				() -> assertEquals("Email is not valid", response.getString("message"))
		);
	}

	@Test
	void testDeleteAccount() {
		JSONObject request = new JSONObject();
		request.put("request","deleteaccount");
		request.put("token", loggedInToken);
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processRequest());
		assertEquals("success", response.getString("response"));
	}


}