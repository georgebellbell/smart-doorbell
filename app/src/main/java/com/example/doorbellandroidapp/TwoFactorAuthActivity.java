package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class TwoFactorAuthActivity extends AppCompatActivity {
	TextView etInputDigits, tv2FAResponse, tv2FAAgain;
	Button btnSubmitDigits, btnReturn;
	private SharedPreferences preferences;
	private String currentUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(TwoFactorAuthActivity.this);
		currentUser = preferences.getString("twoFactorUser","");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_two_factor_auth);

		etInputDigits = findViewById(R.id.etInputDigits);
		tv2FAResponse = findViewById(R.id.tv2FAResponse);
		tv2FAAgain = findViewById(R.id.tv2FAAgain);
		btnSubmitDigits = findViewById(R.id.btnSubmitDigits);
		btnReturn = findViewById(R.id.btnReturn);

		btnSubmitDigits.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check if code is correct
				String digits = etInputDigits.getText().toString();
				if (isValidCode(digits)){
					checkCode(digits);
				}
			}
		});

		btnReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Back to login
				preferences.edit().clear().apply();
				Intent intent = new Intent(TwoFactorAuthActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

		tv2FAAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Resend 2FA email
				resendEmail();
			}
		});

	}

	/**
	 * Notifies user of successful login entered and starts main activity
	 */
	void twoFactorSuccess() {
		runOnUiThread(new Runnable(){
			public void run() {
				// Set current user
				preferences.edit().putString("currentUser",currentUser).apply();

				// Success notification
				Toast.makeText(getApplicationContext(), "Login Successful",
						Toast.LENGTH_SHORT).show();

				// Start main activity
				Intent intent = new Intent(TwoFactorAuthActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Notifies user of unsuccessful 2FA code entered
	 */
	void twoFactorFail(final String errorMsg) {
		runOnUiThread(new Runnable(){
			public void run() {
				Toast.makeText(getApplicationContext(), errorMsg,
						Toast.LENGTH_SHORT).show();
			}
		});
	}


	/**
	 * Sends user's inputted code to be checked on server
	 * @param code - Inputted code by user
	 */
	void checkCode(String code){
		// Client to handle response from server
		Client client = new Client() {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						twoFactorSuccess();
						break;
					case "fail":
						twoFactorFail(response.getString("message"));
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","twofactor");
			request.put("username", currentUser);
			request.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * Displays text that confirms email was resent
	 */
	void displayEmailResent() {
		runOnUiThread(new Runnable(){
			public void run() {
				tv2FAResponse.setText("New email sent!");
			}
		});
	}

	/**
	 * Sends request to server to resend email
	 */
	void resendEmail() {
		Client client = new Client() {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				displayEmailResent();
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","resendtwofactor");
			request.put("username", currentUser);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}
	/**
	 * Checks if user input is valid
	 * @param code - inputted Two Factor Authentication Code
	 * @return true or false depending if code is in correct format
	 */
	boolean isValidCode(String code){
		if (!Pattern.matches("[1-9]+",code)){
			twoFactorFail("Make sure your code only uses numbers");
			return false;
		}
		else if (code.length() != 6){
			twoFactorFail("Your code needs to be six digits long");
			return false;
		}
		else{
			return true;
		}
	}
}