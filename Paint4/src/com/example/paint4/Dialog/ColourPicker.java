package com.example.paint4.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.paint4.MainActivity;
import com.example.paint4.R;
import com.example.paint4.CustomObjects.ColourListener;
import com.example.paint4.CustomObjects.ColourPair;

public class ColourPicker extends Dialog {

	private ColourPickerViewGroup cView;
	private ColourListener mListener;
	private Bitmap arrow, pointUp, pointDown;
		
	public ColourPicker (Context context, ColourPair colour, ColourListener listener)
	{
		this(context);
		mListener = listener;
		cView.setMainColour(colour.getMainColour());
		cView.setSelectedColour(colour.getSelectedColour());
		
		arrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
		arrow = Bitmap.createScaledBitmap(arrow, arrow.getWidth()/2, arrow.getHeight()/2, false);
		
		pointUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.pointer_up);
		pointUp = Bitmap.createScaledBitmap(pointUp, pointUp.getWidth()/5, pointUp.getHeight()/5, false);
		
		pointDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.pointer_down);
		pointDown = Bitmap.createScaledBitmap(pointDown, pointDown.getWidth()/5, pointDown.getHeight()/5, false);
	}
	
	private ColourPicker(Context context) {
		super(context);
		
		cView = new ColourPickerViewGroup(this.getContext());
		
		this.setTitle("Pick a Colour");
		this.setContentView(cView);
	}	
	
	public int getSelectedColour ()
	{
		return cView.getSelectedColour();
	}
	
	private void Ok()
	{
		mListener.onSelected(cView.getMainColour(), cView.getSelectedColour());
		this.dismiss();
	}
	
	private void Cancel()
	{
		this.dismiss();
	}
	
	private class ColourPickerViewGroup extends LinearLayout {
		
		ColourPickerView view;
		Button okBtn;
		Button cancelBtn;
		private ColourPickerViewGroup (Context context)
		{
			super(context);
			
			View buttons = LayoutInflater.from(context).inflate(R.layout.buttons, null);
			okBtn = (Button)buttons.findViewById(R.id.OkBtn);
			okBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					Ok();
					
				}
				
			});
			
			cancelBtn = (Button)buttons.findViewById(R.id.CancelBtn);
			cancelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Cancel();
				}
				
			});
			
			view = new ColourPickerView(context);
			view.setLayoutParams(new LayoutParams(612, 745));
			this.addView(view);
			this.addView(buttons);
						
			this.setGravity(Gravity.CENTER);
			this.setOrientation(VERTICAL);
		}

		private int getSelectedColour() {
			return view.getSelectedColour();
		}
		
		private int getMainColour () {
			return view.getCurrentMainColor();
		}

		private void setSelectedColour(int selectedColour) {
			view.setSelectedColour(selectedColour);
			
		}

		private void setMainColour(int mainColour) {
			view.setMainColour(mainColour);
			
		}		
	}
	
	private class ColourPickerView extends View {
		
		private Paint paint, paintCursor;
		private int[] mBarColours  = new int [258];
		private int[] mMainColors = new int[65536];

		private int mSelectedColour;
		private int mMainColour;
		
		private Point cursorHueBar;
		private Point cursorMain;
		
		//constants for displaying view
		private final int LENGTH_FACTOR = 2;
		private final int WHERE_BAR_STARTS = 50;
		private final int WHERE_BAR_ENDS = 258 * LENGTH_FACTOR + WHERE_BAR_STARTS;
		private final int WHERE_MAIN_STARTS_Y = 135;
		private final int WHERE_MAIN_ENDS_Y = WHERE_MAIN_STARTS_Y + (256 * LENGTH_FACTOR);
		private final int CURSOR_RADIUS = 10;
		
		private ColourPickerView (Context context) {
			super(context);
			paint = new Paint();
			cursorHueBar = new Point (0, 0);
			cursorMain = new Point (0, 0);
			
			setPaints();
			updateMainColours();			
			createHueBar();
		
		}
		
		private void createHueBar() {

			int index = 0;
			for (float i = 0; i < 256; i += 256/42) // Red (#f00) to pink (#f0f)
			{
				mBarColours[index] = Color.rgb(255, 0, (int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Pink (#f0f) to blue (#00f)
			{
				mBarColours[index] = Color.rgb(255-(int) i, 0, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Blue (#00f) to light blue (#0ff)
			{
				mBarColours[index] = Color.rgb(0, (int) i, 255);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Light blue (#0ff) to green (#0f0)
			{
				mBarColours[index] = Color.rgb(0, 255, 255-(int) i);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Green (#0f0) to yellow (#ff0)
			{
				mBarColours[index] = Color.rgb((int) i, 255, 0);
				index++;
			}
			for (float i=0; i<256; i += 256/42) // Yellow (#ff0) to red (#f00)
			{
				mBarColours[index] = Color.rgb(255, 255-(int) i, 0);
				index++;
			}

		}
		
		private void setPaints ()
		{
			paintCursor = new Paint ();
			paintCursor.setColor(Color.BLACK);
			paintCursor.setStyle(Style.STROKE);
			paintCursor.setStrokeWidth(2);
		}

		//based on what the user selects from the bar, you change the main colours
		private void updateMainColours () {
			
			int mainColor = getCurrentMainColor();
			int index = 0;
			int[] topColors = new int[256];
			
			for (int y = 0; y < 256; y++)
			{
				for (int x = 0; x < 256; x++)
				{
					int red, green, blue;
					
					if (y == 0)
					{
						red = 255 - (255 - Color.red(mainColor))*x/255;
						green = 255 - (255 - Color.green(mainColor))*x/255;
						blue = 255 - (255 - Color.blue(mainColor))*x/255;
						
						mMainColors[index] = Color.rgb(red, green, blue);
						topColors[x] = mMainColors[index];
					}
					else
					{
						red = (255 - y) * Color.red(topColors[x])/255;
						blue = (255 - y) * Color.green(topColors[x])/255;
						green = (255 - y) * Color.blue(topColors[x])/255;
						
						mMainColors[index] = Color.rgb(red, blue, green);

					}
					index++;
				}
			}
		}
		
		private int getCurrentMainColor() {			
			return mMainColour;			
		}
		
		private int getSelectedColour () {
			return mSelectedColour;
		}
		
		private void setMainColour(int colour) {
			mMainColour = colour;
			setBarCursorPosition();
			updateMainColours();
		}
		
		private void setSelectedColour (int colour) {
			mSelectedColour = colour;
			setMainCursorPosition();
			updateMainColours();
		}
		
		private void setBarCursorPosition () {
			for (int i = 0; i < mBarColours.length; i++)
			{	
				int colour = mBarColours[i];
				if (colour == mMainColour)
				{
					cursorHueBar.set(WHERE_BAR_STARTS + (LENGTH_FACTOR * i), 58);
				}
			}
		}
		
		private void setMainCursorPosition () {
			for (int i = 0; i < mMainColors.length; i++)
			{
				int colour = mMainColors[i];
				if (mSelectedColour == colour)
				{
					cursorMain.set(WHERE_BAR_STARTS + (LENGTH_FACTOR)*(i % 256), WHERE_MAIN_STARTS_Y + (LENGTH_FACTOR)*(i / 256));
					break;
				}
			}
		}
		
		//Returns the main colour given a point on the screen of the dialog
		private int getMainColourGivenPoint (int x, int y) {
			int index = (x - WHERE_BAR_STARTS)/2 + (256*((y - WHERE_MAIN_STARTS_Y)/2)); 
			return mMainColors[index];
		}

		@Override
		protected void onDraw(Canvas canvas){			
			
			//draw the colour bar at the top
			for (int i = 0; i < 256; i++) {
				paint.setColor(mBarColours[i]);
				int x = i*LENGTH_FACTOR;
				canvas.drawRect(x + WHERE_BAR_STARTS, 40, x + WHERE_BAR_STARTS + LENGTH_FACTOR, 115, paint);
			}
			
			//drawCursor
			//canvas.drawCircle(cursorHueBar.x, cursorHueBar.y, CURSOR_RADIUS, paintCursor);
			
			// draw the new cursor
			canvas.drawBitmap(pointDown, cursorHueBar.x - pointDown.getWidth()/2, 40 - pointDown.getHeight(), paint); 
			canvas.drawBitmap(pointUp, cursorHueBar.x - pointUp.getWidth()/2, 115, paint); 

			//draw the main colour box 
			for (int i = 0; i < 256; i++)
			{
				int[] colors = new int[2];
				colors[0] = mMainColors[i];
				colors[1] = Color.BLACK;
				
				Shader shader = new LinearGradient(0, WHERE_MAIN_STARTS_Y, 0, WHERE_MAIN_ENDS_Y, colors, null, Shader.TileMode.REPEAT);
				paint.setShader(shader);
				
				int x = i*LENGTH_FACTOR;
				canvas.drawRect(x + WHERE_BAR_STARTS, WHERE_MAIN_STARTS_Y, x + WHERE_BAR_STARTS + LENGTH_FACTOR, WHERE_MAIN_ENDS_Y, paint);
			}
			
			canvas.drawCircle(cursorMain.x, cursorMain.y, CURSOR_RADIUS, paintCursor);
			
			//draw the sample box
			paint.setShader(null);
			paint.setColor(MainActivity.getCurrentColour());
			canvas.drawRect(WHERE_BAR_STARTS + 75, 667, WHERE_BAR_STARTS + 175, 727, paint);
			
			canvas.drawBitmap(arrow, WHERE_BAR_STARTS + 205 + (80 - arrow.getWidth()), 697 - (arrow.getHeight()/2), paint);
			//paint.setColor(Color.BLACK);
			//canvas.drawLine(WHERE_BAR_STARTS + 205, 677, WHERE_BAR_STARTS + 290, 677, paint);
			
			paint.setColor(mSelectedColour);
			canvas.drawRect(WHERE_BAR_STARTS + 320, 667, WHERE_BAR_STARTS + 420, 727, paint);
			
		}
		
		@Override
		public void onMeasure (int width, int height)
		{
			super.onMeasure(width, height);
			this.setMeasuredDimension(width, height);
		}
		
		@Override
		public	boolean onTouchEvent (MotionEvent event)
		{	
			int X = (int) event.getX();
			int Y = (int) event.getY();
			int index = (X - WHERE_BAR_STARTS)/2;

			//user clicked on the bar
			if (X >= WHERE_BAR_STARTS && X <= WHERE_BAR_ENDS && Y >= 20 && Y <= 95)
			{
				assert (index >= 0 && index < 258);
				mMainColour = mBarColours[index];
				cursorHueBar.x = X;
				cursorHueBar.y = Y;
				
				updateMainColours();
				mSelectedColour = getMainColourGivenPoint (cursorMain.x, cursorMain.y);
				invalidate();
			}
			//user clicked on the main square
			else if (X >= WHERE_BAR_STARTS && X <= WHERE_BAR_ENDS && Y >= 115 && Y <= 627)
			{
				mSelectedColour = getMainColourGivenPoint(X, Y);
			
				cursorMain.x = X;
				cursorMain.y = Y;
				invalidate();
			}
			
			return false;
		}
		
	}

	
}
