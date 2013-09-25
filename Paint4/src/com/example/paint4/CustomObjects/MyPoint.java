package com.example.paint4.CustomObjects;

import android.graphics.Point;

public class MyPoint extends Point{

	public int dx, dy;
	
	public MyPoint (int x, int y)
	{
		super (x, y);

	}
	
	public MyPoint (float x, float y)
	{
		super((int)x, (int)y);

	}
}
