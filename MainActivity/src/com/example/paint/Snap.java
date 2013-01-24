package com.example.paint;

import java.util.ArrayList;

public class Snap extends ArrayList{
	
	private int [] colours;
	
	public Snap (int [] pixels)
	{
		colours = pixels;
	}
	
	public int [] getColours ()
	{
		return colours;
	}
	
	

}
