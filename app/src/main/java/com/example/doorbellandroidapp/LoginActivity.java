package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
	EditText etUsername, etPassword;
	TextView tvInformation, tvSignUp;
	Button btnLogin;
	Integer attempts = 5;
	Boolean isValid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				String inputPassword = etPassword.getText().toString();

				// Send to server for checking
				authenticate(inputUsername, inputPassword);
			}
		});
	}

	/**
	 * Takes users information and passes it to server to be checked
	 * @param username - username inputted by user
	 * @param password - password inputted by user
	 */
	void authenticate(String username, String password){
		Client client = new Client();
		client.execute(username, password);
	}

	/**
	 * Notifies user of successful login attempt and starts main activity
	 */
	void loginSuccess() {
		Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
	}

	/**
	 * Notifies user of failed login attempt and removes an attempt
	 */
	void loginFail() {
		Toast.makeText(LoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
		removeAttempt();
		tvInformation.setText("No of attempts remaining: " + attempts);
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
	 * class variables are assigned components in the android app
	 */
	void assign(){
		etUsername = findViewById(R.id.etUsername);
		etPassword = findViewById(R.id.pwdPassword);
		tvInformation = findViewById(R.id.tvInformation);
		tvSignUp = findViewById(R.id.tvSignUp);
		btnLogin = findViewById(R.id.btnLogin);
	}
}