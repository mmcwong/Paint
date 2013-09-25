package com.example.paint4.CustomObjects;

public class SnapShot {
	
	private int [] mPixels;
	
	public SnapShot (int [] pixels) {
		mPixels = pixels;
	}
	
	public int [] getPixels () {
		return mPixels;
	}
	
}
