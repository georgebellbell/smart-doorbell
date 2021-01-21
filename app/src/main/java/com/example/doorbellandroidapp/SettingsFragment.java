package com.example.doorbellandroidapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SettingsFragment extends Fragment{

	EditText etDoorbellConnect, etDoorbellConnectName, pwdChangePassword, etChangeEmail;
	Button btnDoorbellConnect, btnChangePassword, btnChangeEmail, btnRemoveDoorbell, btnDeleteAccount;
	ImageView ivInfo, ivAddDoorbellInfo;
	Spinner spinnerID;

	private ArrayList<String> doorbells = new ArrayList<>();
	private ArrayList<String> doorbellIDs = new ArrayList<>();

	Activity mActivity;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		assign(view);
		mActivity = getActivity();
		getIDs();



		ivInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popups.showInformation(getContext(),"settings");
			}
		});

		ivAddDoorbellInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popups.showInformation(getContext(),"doorbell");
			}
		});

		btnDoorbellConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

					String doorbellID = etDoorbellConnect.getText().toString();
					String doorbellName = etDoorbellConnectName.getText().toString();
					if (!doorbellID.isEmpty()||!doorbellName.isEmpty())
						addDoorbell(doorbellID, doorbellName);
					else
						Toast.makeText(mActivity, "No ID or name was given", Toast.LENGTH_SHORT).show();
			}
		});

		btnChangeEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newEmail = etChangeEmail.getText().toString();
				if(validateEmail(newEmail))
					changeEmail(newEmail);
				else
					Toast.makeText(getContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
			}
		});

		btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String newPassword = pwdChangePassword.getText().toString();
				if(validatePassword(newPassword))
					changePassword(newPassword);
				else
					Toast.makeText(getContext(), "Invalid Password, Passwords must be at least 9 characters in length and contain capital letters, lower case letters & numbers", Toast.LENGTH_SHORT).show();
			}
		});



		btnRemoveDoorbell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (doorbellIDs.size()>0) {
					String currentDoorbellID = doorbellIDs.get(spinnerID.getSelectedItemPosition());
					String currentDoorbellName = doorbells.get(spinnerID.getSelectedItemPosition());
					Popups.removeDoorbellConfirmation(currentDoorbellID, currentDoorbellName, mActivity);
				}
				else{
					Toast.makeText(mActivity, "No doorbells to remove!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Popups.deleteConfirmation(getActivity());
			}
		});


		return view;

	}

	public void addDoorbell(String doorbellID, String doorbellName){
		// Client to handle sign up response from server
		Client client = new Client(getActivity()) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						Toast.makeText(getContext(), "Doorbell Added", Toast.LENGTH_SHORT).show();
						etDoorbellConnect.setText("");
						etDoorbellConnectName.setText("");
						getIDs();
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
			request.put("doorbellname",doorbellName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}
	public void getIDs(){
		Client client = new Client(mActivity) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						JSONArray jsonArray = response.getJSONArray("doorbells");
						for (int i = 0; i < jsonArray.length() ; i++) {
							doorbells.add(jsonArray.getJSONObject(i).getString("name"));
							doorbellIDs.add(jsonArray.getJSONObject(i).getString("id"));
						}
						populateSpinner();
						break;
					case "fail":
						Toast.makeText(mActivity, "No doorbells assigned yet!", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","getdoorbells");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Set request and start connection
		client.setRequest(request);
		client.start();

	}



	/**
	 * Adds the retrieved IDs to a dropdown menu the user can navigate between
	 */
	public void populateSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,doorbells);
		spinnerID.setAdapter(adapter);
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
						Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
						Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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



	public void assign(View view){
		etDoorbellConnect = view.findViewById(R.id.etDoorbellConnect);
		etDoorbellConnectName = view.findViewById(R.id.etDoorbellConnectName);
		etChangeEmail = view.findViewById(R.id.etChangeEmail);
		pwdChangePassword = view.findViewById(R.id.pwdChangePassword);
		btnDoorbellConnect = view.findViewById(R.id.btnDoorbellConnect);
		btnChangePassword = view.findViewById(R.id.btnChangePassword);
		btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
		btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
		btnRemoveDoorbell = view.findViewById(R.id.btnRemoveDoorbell);
		ivAddDoorbellInfo = view.findViewById(R.id.ivAddDoorbellInfo);
		ivInfo = view.findViewById(R.id.ivInfo);
		spinnerID = view.findViewById(R.id.spinnerID);

	}

	/**
	 * Validates the change password input
	 * @param inputPassword - password inputted by the user
	 * @return - returns true if all validation checks have succeeded otherwise, returns false
	 */
	boolean validatePassword(String inputPassword) {
		int passLength = inputPassword.length();

		Pattern lowerCaseLetters = Pattern.compile("[a-z]");
		Pattern upperCaseLetters = Pattern.compile("[A-Z]");
		Pattern numbers = Pattern.compile("[^0-9]");

		Matcher lowerCasePasswordMatcher = lowerCaseLetters.matcher(inputPassword);
		Matcher upperCasePasswordMatcher = upperCaseLetters.matcher(inputPassword);
		Matcher numbersPasswordMatcher = numbers.matcher(inputPassword);

		boolean lowerCheck = lowerCasePasswordMatcher.find();
		boolean upperCheck = upperCasePasswordMatcher.find();
		boolean numberCheck = numbersPasswordMatcher.find();

		if (passLength > 8 && numberCheck && upperCheck && lowerCheck){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Validates the change email input
	 * @param inputEmail - email inputted by the user
	 * @return - returns true if all validation checks have succeeded otherwise, returns false
	 */
	boolean validateEmail(String inputEmail) {

		if (!Pattern.matches("/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/",inputEmail)){
			return false;
		} else {
			return true;
		}
	}


}