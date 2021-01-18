package server.protocol;

import database.AccountTable;
import database.Doorbell;
import database.DoorbellTable;
import database.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminProtocolTest {
	private AdminProtocol protocol;
	private static AdminProtocol loginProtocol;
	private static User testUser;
	private static User testAdmin;
	private static Doorbell testDoorbell;
	private static Doorbell deleteDoorbell;
	private static Doorbell updateDoorbell;

	@BeforeAll
	static void init() {
		// Create test accounts
		testUser = new User("testuser", "quicksolutions.doorbell@gmail.com", "password", "user");
		testAdmin = new User("testadmin", "quicksolutions.doorbell@gmail.com", "password", "admin");
		saveUser(testUser);
		saveUser(testAdmin);

		// Create test doorbells
		testDoorbell = new Doorbell("QS-0000-0000", "Test Doorbell");
		deleteDoorbell = new Doorbell("QS-0000-0001", "Delete Doorbell");
		updateDoorbell = new Doorbell("QS-0000-0002", "Update Doorbell");
		saveDoorbell(testDoorbell);
		saveDoorbell(deleteDoorbell);
		saveDoorbell(updateDoorbell);

		// Create protocol that is logged in as admin
		loginProtocol = new AdminProtocol();
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testAdmin.getUsername());
		request.put("password", testAdmin.getPassword());
		loginProtocol.setRequest(request.toString());
		loginProtocol.processInput();
	}

	@AfterAll
	static void cleanUp() {
		removeUser(testUser);
		removeUser(testAdmin);
		removeDoorbell(testDoorbell);
		removeDoorbell(deleteDoorbell);
		removeDoorbell(updateDoorbell);
	}

	@BeforeEach
	void setup() {
		protocol = new AdminProtocol();
	}

	static void saveUser(User user) {
		AccountTable accountTable = new AccountTable();
		accountTable.connect();
		accountTable.addRecord(user);
		accountTable.disconnect();
	}

	static void removeUser(User user) {
		AccountTable accountTable = new AccountTable();
		accountTable.connect();
		accountTable.deleteRecord(user.getUsername());
		accountTable.disconnect();
	}

	static void saveDoorbell(Doorbell doorbell) {
		DoorbellTable doorbellTable = new DoorbellTable();
		doorbellTable.connect();
		doorbellTable.addNewDoorbell(doorbell);
		doorbellTable.disconnect();
	}

	static void removeDoorbell(Doorbell doorbell) {
		DoorbellTable doorbellTable = new DoorbellTable();
		doorbellTable.connect();
		doorbellTable.deleteDoorbell(doorbell);
		doorbellTable.disconnect();
	}

	@Test
	void testValidRequest() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testAdmin.getUsername());
		request.put("password", testAdmin.getPassword());

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
		request.put("username", testAdmin.getUsername());
		request.put("password", testAdmin.getPassword());
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testLoginIncorrectDetails() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testAdmin.getUsername());
		request.put("password", "incorrect");
		protocol.setRequest(request.toString());
		JSONObject response = new JSONObject(protocol.processInput());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testLoginNonAdminAccount() {
		JSONObject request = new JSONObject();
		request.put("request","login");
		request.put("username", testUser.getUsername());
		request.put("password", testUser.getPassword());
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

	@Test
	void testSearchForExistingUser() {
		JSONObject request = new JSONObject();
		request.put("request","user");
		request.put("username", testUser.getUsername());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertAll("success",
				() -> assertEquals("success", response.getString("response")),
				() -> assertEquals(testUser.getUsername(), response.getString("username")),
				() -> assertEquals(testUser.getEmail(), response.getString("email")),
				() -> assertEquals(testUser.getCreated_at(), response.getString("time"))
		);
	}

	@Test
	void testSearchForNonExistentUser() {
		JSONObject request = new JSONObject();
		request.put("request","user");
		request.put("username","non-existent-user");
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testUpdateUser() {
		JSONObject request = new JSONObject();
		request.put("request","update");
		request.put("username", testUser.getUsername());
		request.put("newusername", testUser.getUsername());
		request.put("newemail", testUser.getEmail());
		request.put("devices", new JSONArray());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testUpdateExistingUserToAnotherExistingUsername() {
		JSONObject request = new JSONObject();
		// Attempt to set testUser's username to testAdmin's username
		request.put("request","update");
		request.put("username", testUser.getUsername());
		request.put("newusername", testAdmin.getUsername());
		request.put("newemail", testUser.getEmail());
		request.put("devices", new JSONArray());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertAll("fail",
				() -> assertEquals("fail", response.getString("response")),
				() -> assertEquals("Account username is already taken", response.getString("message"))
		);
	}

	@Test
	void testDeleteUser() {
		User userToDelete = new User("usertodelete", "delete@delete.com",
				"password", "user");
		saveUser(userToDelete);
		JSONObject request = new JSONObject();
		request.put("request","deleteuser");
		request.put("username", userToDelete.getUsername());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testDeleteCurrentAdminAccount() {
		JSONObject request = new JSONObject();
		request.put("request","deleteuser");
		request.put("username", testAdmin.getUsername());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertAll("fail",
				() -> assertEquals("fail", response.getString("response")),
				() -> assertEquals("You cannot delete your own account", response.getString("message"))
		);
	}

	@Test
	void testResetUserPassword() {
		JSONObject request = new JSONObject();
		request.put("request","newpassword");
		request.put("username", testUser.getUsername());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("success", response.getString("response"));
	}

	@Test
	void testSearchForExistingDoorbell() {
		JSONObject request = new JSONObject();
		request.put("request","searchdoorbell");
		request.put("id", testDoorbell.getId());
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertAll("success",
				() -> assertEquals("success", response.getString("response")),
				() -> assertEquals(testDoorbell.getId(), response.getString("id")),
				() -> assertEquals(testDoorbell.getName(), response.getString("name"))
		);
	}

	@Test
	void testSearchForNonExistentDoorbell() {
		JSONObject request = new JSONObject();
		request.put("request","searchdoorbell");
		request.put("id","not-existing-doorbell");
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("fail", response.getString("response"));
	}

	@Test
	void testUpdateDoorbell() {
		JSONObject request = new JSONObject();
		request.put("request","updatedoorbell");
		request.put("id", updateDoorbell.getId());
		request.put("name", "New Doorbell Name");
		loginProtocol.setRequest(request.toString());
		JSONObject response = new JSONObject(loginProtocol.processInput());
		assertEquals("success", response.getString("response"));
	}

}