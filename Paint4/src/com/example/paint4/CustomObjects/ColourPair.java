package com.example.paint4.CustomObjects;

public class ColourPair {
	
	private int mMainColour;
	private int mSelectedColour;
	
	public ColourPair (int mainColour, int selectedColour)
	{
		mMainColour = mainColour;
		mSelectedColour = selectedColour;
	}
	
	public int getMainColour ()
	{
		return mMainColour;
	}
	
	public int getSelectedColour ()
	{
		return mSelectedColour;
	}
}
