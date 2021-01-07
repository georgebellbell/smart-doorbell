package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class LoginActivity extends AppCompatActivity {
	EditText etUsername, pwdPassword;
	TextView tvInformation, tvGoToSignUp;
	Button btnLogin;
	Integer attempts = 5;
	Boolean isValid;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		preferences.edit().clear().apply();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//method call to assign variables to components
		assign();
		tvInformation.setText("No of attempts remaining: " + attempts);
		//functionality added to log in button which wil check inputs
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get user input
				String inputUsername = etUsername.getText().toString();
				String inputPassword = pwdPassword.getText().toString();

				// Send to server for checking
				authenticate(inputUsername, inputPassword);
			}
		});

		tvGoToSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Create a new account",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);

			}
		});
	}

	/**
	 * Notifies user of successful login attempt and starts main activity
	 */
	void loginSuccess() {
		runOnUiThread(new Runnable(){
			public void run() {
				preferences.edit().putString("twoFactorUser",etUsername.getText().toString()).apply();
				Toast.makeText(getApplicationContext(), "Login Successful",
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(LoginActivity.this, TwoFactorAuthActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Notifies user of failed login attempt and removes an attempt
	 */
	void loginFail() {
		runOnUiThread(new Runnable(){
			public void run() {
				Toast.makeText(getApplicationContext(), "Incorrect Username or Password",
						Toast.LENGTH_SHORT).show();
				removeAttempt();
				tvInformation.setText("No of attempts remaining: " + attempts);
			}
		});
	}

	/**
	 * Removes a login attempt and disables button if attempts is now zero
	 */
	void removeAttempt(){
		attempts = attempts -1;
		if (attempts == 0) {
			btnLogin.setEnabled(false);
		}
	}

	/**
	 * Takes users information and passes it to server to be checked
	 * @param username - username inputted by user
	 * @param password - password inputted by user
	 */
	void authenticate(String username, String password){
		// Client to handle login response from server
		Client client = new Client() {
			@Override
			public void handleResponse(JSONObject response) throws JSONException {
				switch (response.getString("response")) {
					case "success":
						loginSuccess();
						break;
					case "fail":
						loginFail();
						break;
				}
			}
		};

		// JSON Request object
		JSONObject request = new JSONObject();
		try {
			request.put("request","login");
			request.put("username", username);
			request.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set request and start connection
		client.setRequest(request);
		client.start();
	}

	/**
	 * class variables are assigned components in the android app
	 */
	void assign(){
		etUsername = findViewById(R.id.etUsername);
		pwdPassword = findViewById(R.id.pwdPassword);
		tvInformation = findViewById(R.id.tvInformation);
		tvGoToSignUp = findViewById(R.id.tvGoToSignUp);
		btnLogin = findViewById(R.id.btnLogin);
	}
}