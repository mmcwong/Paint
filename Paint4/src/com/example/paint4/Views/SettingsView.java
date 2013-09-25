package com.example.paint4.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.example.paint4.MainActivity;


public class SettingsView extends View {
	private boolean inDoubleRight; 
	private int originX;
	private static final double PERCENTAGE_OF_SCREEN_FOR_SWIPE = 10, SWIPE_DISTANCE_UP = 50; 

	public SettingsView(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	   public boolean onTouchEvent(MotionEvent event) {
		 	int rightPartOfScreen = (int) ((1.0 - (PERCENTAGE_OF_SCREEN_FOR_SWIPE)/100) * MainActivity.getWidth());
		 	
			if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
				
				if(event.getPointerCount() == 2  && event.getX() >= rightPartOfScreen){
					handleSwipeLeft(event);				
				}
				
			}				
			else if (event.getActionMasked() == MotionEvent.ACTION_UP && event.getPointerCount() == 1 && inDoubleRight){
				handleUp(event);
			}
		 return true;
	    }

	private void handleUp(MotionEvent event) {
		
		if (inDoubleRight && originX - event.getX() >= SWIPE_DISTANCE_UP){
			originX = 0;
			MainActivity.backToCanvas();
		}

		inDoubleRight = false;
		
	}

	private void handleSwipeLeft(MotionEvent event) {
		inDoubleRight = true;
		originX = (int) event.getY(1);
		
	}
	
}
