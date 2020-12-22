package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
	EditText etUsername, pwdPassword, etEmailAddress;
	TextView tvInformation, tvSignUp;
	Button btnSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		assign();
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputUsername = etUsername.getText().toString();
				String inputEmail = etEmailAddress.getText().toString();
				String inputPassword = pwdPassword.getText().toString();

				signUp(inputUsername, inputEmail, inputPassword);
			}
		});
	}

	/**
	 * Notifies user of successful signup attempt and starts main activity
	 */
	void signUpSuccess() {
		runOnUiThread(new Runnable(){
			public void run() {
				Toast.makeText(getApplicationContext(), "Registration Successful",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Notifies user of failed signup attempt
	 */
	void signUpFail() {
		runOnUiThread(new Runnable(){
			public void run() {
				Toast.makeText(getApplicationContext(), "Username already exists, please try again",
						Toast.LENGTH_SHORT).show();
				tvInformation.setText("Registration Unsuccessful");
			}
		});
	}

	/**
	 * Takes user's information from register and passes it to the server to be checked
	 * @param inputUsername - username inputted by the user
	 * @param inputEmail - email inputted by the user
	 * @param inputPassword - password inputted by the user
	 */
	void signUp(String inputUsername, String inputEmail, String inputPassword){
		// Client to handle sign up response from server
		Client client = new Client() {
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
		btnSignUp = findViewById(R.id.btnSignUp);
	}
}