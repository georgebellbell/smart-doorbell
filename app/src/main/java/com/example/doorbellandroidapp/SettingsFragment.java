package com.example.doorbellandroidapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingsFragment extends Fragment {

	EditText etDoorbellConnect, pwdChangePassword, etChangeEmail;
	Button btnDoorbellConnect, btnChangePassword, btnChangeEmail, btnDeleteAccount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		assign(view);
		btnDoorbellConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String doorbellID = etDoorbellConnect.getText().toString();
				addDoorbell(doorbellID);
			}
		});

		btnChangeEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newEmail = etChangeEmail.getText().toString();
				changeEmail(newEmail);
			}
		});

		btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newPassword = pwdChangePassword.getText().toString();
				changePassword(newPassword);
			}
		});

		btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteAccount();
			}
		});
		return view;

	}

	public void addDoorbell(String doorbellID){
		// Client to handle sign up response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Doorbell Added", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(getContext(), "Doorbell Not Added", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","connectdoorbell");
			request.put("doorbellID", doorbellID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	public void changeEmail(String newEmail){
		// Client to handle sign up response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Email Changed", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(getContext(), "Email Not Changed", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","changeemail");
			request.put("email", newEmail);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	public void changePassword(String newPassword){
		// Client to handle sign up response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(getContext(), "Password Not Changed", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","changepassword");
			request.put("password", newPassword);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	public void deleteAccount(){
		// Client to handle sign up response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
						break;
					case "fail":
						Toast.makeText(getContext(), "Account Not Deleted", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","deleteaccount");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	public void assign(View view){
		etDoorbellConnect = view.findViewById(R.id.etDoorbellConnect);
		etChangeEmail = view.findViewById(R.id.etChangeEmail);
		pwdChangePassword = view.findViewById(R.id.pwdChangePassword);
		btnDoorbellConnect = view.findViewById(R.id.btnDoorbellConnect);
		btnChangePassword = view.findViewById(R.id.btnChangePassword);
		btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
		btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
	}

	/**
	 * Validates all of the users inputs
	 * @param inputEmail - email inputted by the user
	 * @param inputPassword - password inputted by the user
	 * @return - returns true if all validation checks have succeeded otherwise, returns false
	 */
	boolean validate(String inputEmail, String inputPassword) {
		int passLength = inputPassword.length();

		Pattern lowerCaseLetters = Pattern.compile("[a-z]");
		Pattern upperCaseLetters = Pattern.compile("[A-Z]");
		Pattern numbers = Pattern.compile("[^0-9]");

		Matcher lowerCasePasswordMatcher = lowerCaseLetters.matcher(inputPassword);
		Matcher upperCasePasswordMatcher = upperCaseLetters.matcher(inputPassword);
		Matcher numbersPasswordMatcher = numbers.matcher(inputPassword);

		boolean emailCheck = inputEmail.contains("@");
		boolean lowerCheck = lowerCasePasswordMatcher.find();
		boolean upperCheck = upperCasePasswordMatcher.find();
		boolean numberCheck = numbersPasswordMatcher.find();

		if (emailCheck && passLength > 8 && numberCheck && upperCheck && lowerCheck){
			return true;
		} else {
			return false;
		}
	}


}