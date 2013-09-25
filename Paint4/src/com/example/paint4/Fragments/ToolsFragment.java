package com.example.paint4.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paint4.R;
import com.example.paint4.Views.ToolsView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ToolsFragment extends Fragment 
{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tools_view, container, false); //set parent view later

	}
	
	public enum Location
	{
		TOP,
		BOTTOM;
	}
}