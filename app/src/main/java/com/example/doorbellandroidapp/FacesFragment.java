package com.example.doorbellandroidapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class FacesFragment extends Fragment {
	private static final String TAG = "FacesFragment";

	//vars
	private ArrayList<String> mNames = new ArrayList<>();
	private ArrayList<String> mImageUrls = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_faces, container, false);
		Log.d(TAG, "onCreate: started");

		initImageBitmaps();
		initRecyclerView(view);
		return view;
	}

	/**
	 * Adds image URLS and image names to ArrayLists to be added to view holders
	 */
	private void initImageBitmaps(){
		Log.d(TAG, "initImageBitmaps: preparing bitmaps");

		mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
		mNames.add("Trondheim");

		mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
		mNames.add("Portugal");

		mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
		mNames.add("Rocky Mountain National Park");


		mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
		mNames.add("Mahahual");

		mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
		mNames.add("Frozen Lake");


		mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
		mNames.add("White Sands Desert");

		mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
		mNames.add("Austrailia");

		mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
		mNames.add("Washington");


	}

	/**
	 * Finds the recycler view and initialised it with RecyclerViewAdapter
	 * @param view current view of android app
	 */
	private void initRecyclerView(View view){
		Log.d(TAG, "initRecyclerView: init recyclerview.");
		RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
		RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImageUrls);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


	}
}