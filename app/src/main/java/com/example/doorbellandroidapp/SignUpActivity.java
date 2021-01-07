package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
	EditText etUsername, pwdPassword, etEmailAddress;
	TextView tvInformation, tvSignUp, tvGoToLogin;
	Button btnSignUp;

	private SharedPreferences preferences;
	private String currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences= PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		assign();
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputUsername = etUsername.getText().toString();
				String inputEmail = etEmailAddress.getText().toString();
				String inputPassword = pwdPassword.getText().toString();
				/*Checking to see that the email contains at least an @ sign & that the password
				 is at least 9 characters long and contains a number, a lower case letter and
				 a upper case letter */
				if (validate(inputUsername, inputEmail, inputPassword)){
					signUp(inputUsername, inputEmail, inputPassword);
				}
				else {
					Toast.makeText(getApplicationContext(), "Invalid Email or Password, please try again",
							Toast.LENGTH_SHORT).show();
					tvInformation.setText("Registration Unsuccessful");

				}
			}
		});

		tvGoToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Log into existing account",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
				startActivity(intent);

			}
		});
	}

	/**
	 * Validates all of the users inputs
	 * @param inputUsername - username inputted by the user
	 * @param inputEmail - email inputted by the user
	 * @param inputPassword - password inputted by the user
	 * @return - returns true if all validation checks have succeeded otherwise, returns false
	 */
	boolean validate(String inputUsername, String inputEmail, String inputPassword) {
		int passLength = inputPassword.length();
		int userLength = inputUsername.length();

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
		boolean usernameCheck = inputUsername.contains(" ");

		if (emailCheck && passLength > 8 && numberCheck && upperCheck && lowerCheck && !usernameCheck && userLength > 1){
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Notifies user of successful signup attempt and starts main activity
	 */
	void signUpSuccess() {
		preferences.edit().putString("currentUser",currentUser).apply();
		Toast.makeText(getApplicationContext(), "Registration Successful",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * Notifies user of failed signup attempt
	 */
	void signUpFail() {
		Toast.makeText(getApplicationContext(), "Username already exists, please try again",
				Toast.LENGTH_SHORT).show();
		tvInformation.setText("Registration Unsuccessful");
	}

	/**
	 * Takes user's information from register and passes it to the server to be checked
	 * @param inputUsername - username inputted by the user
	 * @param inputEmail - email inputted by the user
	 * @param inputPassword - password inputted by the user
	 */
	void signUp(String inputUsername, String inputEmail, String inputPassword){
		currentUser = inputUsername;
		// Client to handle sign up response from server
		Client client = new Client(this) {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						signUpSuccess();
						break;
					case "fail":
						signUpFail();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","signup");
			request.put("username", inputUsername);
			request.put("email", inputEmail);
			request.put("password", inputPassword);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	void assign(){
		etUsername = findViewById(R.id.etUsername);
		etEmailAddress = findViewById(R.id.etEmailAddress);
		pwdPassword = findViewById(R.id.pwdPassword);
		tvInformation = findViewById(R.id.tvInformation);
		tvSignUp = findViewById(R.id.tvSignUp);
		tvGoToLogin = findViewById(R.id.tvGoToLogin);
		btnSignUp = findViewById(R.id.btnSignUp);
	}
}