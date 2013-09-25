package com.example.paint4.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.paint4.MainActivity;
import com.example.paint4.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DropperFragment extends Fragment {
	private View view;
	private ImageView dropperColour;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.dropper_view, container, false); //set parent view later
		return view;

	}
	@Override
	public void onStart () {
		super.onStart();
		dropperColour = (ImageView) view.findViewById(R.id.dropperColour);
		dropperColour.setBackgroundColor(MainActivity.getDropperColour());
	}

}
