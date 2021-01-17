package com.example.doorbellandroidapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;


public class SettingsFragment extends Fragment {

	private Button btnSettings;
	private Switch swMode;

	private ImageView ivInfo;

	private Fragment fragment;
	private FragmentManager fragmentManager;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		ivInfo = view.findViewById(R.id.ivInfo);

		ivInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InformationPopups.showInformation(getContext(),"settings");
			}
		});



		return view;
	}
}