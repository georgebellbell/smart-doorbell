package com.example.doorbellandroidapp;

import android.media.Image;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment {
	private ImageView ivLastFace;
	private Button btnOpenDoor, btnLeaveClosed;
	private TextView tvDoorInformation;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		assign(view);

		// TODO Get most recent picture from raspberry pi and set it to ivLastFace
		btnOpenDoor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDoorInformation.setText("You opened the door!");
				contactDoor(true);
			}
		});
		btnLeaveClosed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDoorInformation.setText("You chose not to open the door");
				contactDoor(false);
			}
		});
		return view;
	}

	public void assign(View view){
		ivLastFace = view.findViewById(R.id.ivLastFace);
		btnOpenDoor = view.findViewById(R.id.btnOpenDoor);
		btnLeaveClosed = view.findViewById(R.id.btnLeaveClosed);
		tvDoorInformation = view.findViewById(R.id.tvDoorInformation);
	}

	private void contactDoor(boolean response){
		Toast.makeText(getContext(), "Woah, your doorbell message in on route", Toast.LENGTH_SHORT).show();
		// TODO Send response to doorbell
	}
}