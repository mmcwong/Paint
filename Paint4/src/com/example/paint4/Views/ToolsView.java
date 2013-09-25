package com.example.paint4.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.paint4.MainActivity;

public class ToolsView extends View {

	private boolean inDoubleUp; 
	private static final double PERCENTAGE_OF_SCREEN_FOR_SWIPE = 15, TOOL_SIZE = 0.15, SWIPE_DISTANCE = 10;
	private Bitmap b;
	private Canvas c;
	private int width, height, originY;

	public ToolsView(Context context) {
		super(context);

		width = MainActivity.getWidth();
		height = MainActivity.getHeight();
		//TODO: experiement
		height = 200;
		b = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);//each pixel stored on 4 bytes
		c = new Canvas(b);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				b.setPixel(i, j, Color.BLUE);
			}
		}
		c.drawBitmap(b, 0, 0, null);

		setPadding(0, MainActivity.getHeight() - 200, 0, 0);		
	}

	@Override
	public void onDraw(Canvas c){
		Paint paint = new Paint ();
		paint.setFilterBitmap(true);

		// The image will be scaled so it will fill the width, and the
		// height will preserve the image’s aspect ration	
		Rect dest = new Rect(0, 0, width, height);
		c.drawBitmap(b, null, dest, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int bottomPartOfScreen = (int)  ((1.0 - (PERCENTAGE_OF_SCREEN_FOR_SWIPE)/100) * MainActivity.getHeight());
		if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
			if (event.getPointerCount() == 2 && event.getY() >= bottomPartOfScreen) {
				handleSwipeUp(event);
			}
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_UP && event.getPointerCount() == 1 && inDoubleUp) {
			handleUp(event);
		}
		return true;
	}
	
	private void handleUp(MotionEvent event) {	
		if (inDoubleUp && event.getY() - originY >= SWIPE_DISTANCE) {
			originY = 0;
			MainActivity.hideTools();
		}
		inDoubleUp = false;
	}
	
	private void handleSwipeUp(MotionEvent event) {
		inDoubleUp = true;
		originY = (int) event.getY(1);
	}
}
