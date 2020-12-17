package server;

import database.AccountTable;
import database.DatabaseConnection;
import database.User;

import java.net.*;
import java.io.*;

public class Protocol {

	public String processInput(String theInput){
		String theOutput = null;
		if (theInput == null) {
			theOutput = "Waiting for response";
		} else if (theInput.equals("Hello")) {
			theOutput = "Good Bye!";
		} else {
			theOutput = getDatabaseRecords().toString();
		}
		return theOutput;
	}

	public User getDatabaseRecords() {
		User user = null;
		AccountTable accountTable = new AccountTable();
		accountTable.connect();
		user = accountTable.getRecord("Dom");
		accountTable.disconnect();
		return user;
	}
}
