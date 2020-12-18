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
	//variables are declared
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
		tvInformation.setText("No of attempts remaining: "+attempts);
		//functionality added to log in button which wil check inputs
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//converts user inputs into string variables
				String inputUsername = etUsername.getText().toString();
				String inputPassword = etPassword.getText().toString();

				if(inputValidation(inputUsername,inputPassword)){
					authenticate(inputUsername,inputPassword);

					/*
					if (isValid){
						//if user inputs are valid and username and password match then give brief notification and send user to home page
						Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
					}
					else{
						//otherwise if details don't match, notify user of this and update number of attempts remaining
						Toast.makeText(LoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
						tvInformation.setText("No of attempts remaining: "+attempts);
						//if number of attempts left is zero then log in button is disabled to prevent brute force attacks
						btnLogin.setEnabled(checkAttempts(attempts));
					}*/
				}
				else{
					//if input isn't valid, tell user this
					Toast.makeText(LoginActivity.this, "Please provide valid inputs", Toast.LENGTH_SHORT).show();
				}

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

		/*
		if (username.equals("user") && password.equals("password")){
			isValid = true;
		}
		else{
			isValid = false;
			attempts = attempts-1;
		}*/
	}

	/**
	 * checks input to see if it is valid before checking against database
	 * @param username - username inputted by user
	 * @param password - password inputted by user
	 * @return true if there is an input, false if there is not
	 */
	boolean inputValidation(String username, String password){
		if(username.equals("")||password.equals("")){
			return false;
		}
		return true;
	}

	/**
	 * Removes a login attempt and disables button if attempts is now zero
	 */
	void removeAttempt(){
		attempts = attempts -1;
		if(attempts == 0){
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