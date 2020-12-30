package com.example.doorbellandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TwoFactorAuthActivity extends AppCompatActivity {
	TextView etInputDigits, tv2FAResponse, tv2FAAgain;
	Button btnSubmitDigits, btnReturn;
	private SharedPreferences preferences;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				//check if digits are correct
				String digits = etInputDigits.getText().toString();
				if (digits.equals("111111")){
					Intent intent = new Intent(TwoFactorAuthActivity.this, MainActivity.class);
					startActivity(intent);
				}
				else{
					tv2FAResponse.setText("Incorrect");
				}
			}
		});

		btnReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//back to login
				preferences.edit().clear().apply();
				Intent intent = new Intent(TwoFactorAuthActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

		tv2FAAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//resend 2FA email
				tv2FAResponse.setText("New email sent!");
			}
		});

	}
}