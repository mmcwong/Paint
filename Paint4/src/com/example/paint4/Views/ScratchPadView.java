package com.example.paint4.Views;

import com.example.paint4.MainActivity;
import com.example.paint4.Fragments.SettingsFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class ScratchPadView extends PictureView {

	public final int SCALE = 3;
	
	public ScratchPadView(Context context) {
		super(context);
		
		int length = Math.max(MainActivity.getWidth(), MainActivity.getHeight());
		width = length/SCALE;
		height = length/SCALE;
		
		b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		newCanvas();
		int [] pixels = new int [width*height];

		b.getPixels(pixels, 0, width, 0, 0, width, height);
		
	}
	
	@Override
	protected void onDraw (Canvas canvas)
	{
		Rect dest = new Rect(0, 0, width, height);
		canvas.drawBitmap(b, null, dest, new Paint());
	}
	
	
	@Override
	public boolean onTouchEvent (MotionEvent event){
		System.out.println("ScratchPad");
		
		boolean status = super.onTouchEvent(event);
		
		if(event.getAction() == MotionEvent.ACTION_UP)
			newCanvas();
		
		return status;
	}
	
	@Override
	protected int getColour ()
	{
		return SettingsFragment.getColour();
	}
	
	@Override
	protected int getBrushSize ()
	{
		return SettingsFragment.getBrushSize();
	}
	
	

}
