package com.example.paint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class BitMapView extends View{

	public Bitmap mBitmap = null;
	public ArrayList <Snap> undoList= new ArrayList <Snap>(); 
	public int width,height;
	public Canvas c = null;
	public int [] colors;
	public int indexOfLast = 0;
	public int brushSize = MainActivity.brush_size,redValue = MainActivity.red_value,greenValue=MainActivity.green_value,blueValue= MainActivity.blue_value;
	public String brushStyle=MainActivity.brush_style;
	public boolean end = true;
	public Context canvasAct;



	@TargetApi(13)
	public BitMapView (Context context)
	{
		super(context);
		
		canvasAct = context;
		width = MainActivity.getWidth();
		height = MainActivity.getHeight();
		mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);//each pixel stored on 4 bytes
		c = new Canvas(mBitmap);
		colors = new int [width*height];
		for (int i = 0;i<(width*height);i++)
			colors[i] = Color.WHITE;
		c.drawBitmap(colors, 0, width, 0, 0, width, height, false, new Paint());    
		undoList.add(convertToSnap(mBitmap));
	}

	public Snap convertToSnap (Bitmap b)
	{
		int [] pixels = new int [height*width];
		b.getPixels(pixels,0,width,0,0,width,height);
		Snap s = new Snap (pixels);
		return s;
	}

	@Override
	protected void onDraw(Canvas canvas){

		Paint paint = new Paint ();
		paint.setFilterBitmap(true);

		// The image will be scaled so it will fill the width, and the
		// height will preserve the image’s aspect ration	
		Rect dest = new Rect(0, 0, width,height);
		canvas.drawBitmap(mBitmap, null, dest, paint);

	}

	public  void setBitMap (Snap s)
	{
		int [] list = s.getColours();
		mBitmap.setPixels(list,0,width,0,0,width,height); 
		invalidate();

	}

	public boolean onTouchEvent (MotionEvent e)
	{
		int X = (int) e.getX();
		int Y = (int) e.getY();

		if (end==false)//if the undoList is not at the end, but the user touches the picture again    		
		{
			while (indexOfLast<(undoList.size()-1))
			{
				System.out.println("here"+(undoList.size()-1));
				System.out.println("here"+indexOfLast);

				undoList.remove(undoList.size()-1);

			}
		}

		//      	System.out.println(X+","+Y);
		//      	System.out.println("BRUSHSIZE"+brushSize);
		//      	System.out.println("R"+redValue);
		//      	System.out.println("G"+greenValue);
		//      	System.out.println("B"+blueValue);

		if(brushStyle.equals("Square"))
		{
			int r = brushSize/2;
			//Square brush
			for (int i = X-r;i<X+r;i++){
				for (int j = Y-r;j<Y+r;j++)
				{
					try
					{
						mBitmap.setPixel(i, j, Color.rgb(redValue,greenValue,blueValue));
					}
					catch (IllegalArgumentException e1)//pixel is out of range
					{

					}
				}
			}

		}

		else if (brushStyle.equals("Spray"))
		{
			//Spray Brush

			int r = brushSize/2;
			int f = 1 - r;
			int ddF_x = 1;
			int ddF_y = -2 * r;
			int x = 0;
			int y = r;


			for (int i = Y-r;i<=Y+r;i++)
				try{
					int num = (int)(Math.random()*10)+1;
					if (num%3==0)mBitmap.setPixel(X, i,  Color.rgb(redValue,greenValue,blueValue));}
			catch (IllegalArgumentException e1)
			{

			}
			for (int i = X-r;i<=X+r;i++)
				try{
					int num = (int)(Math.random()*10)+1;
					if(num%3==0)mBitmap.setPixel(i, Y,  Color.rgb(redValue,greenValue,blueValue));}
			catch (IllegalArgumentException e1)
			{

			}

			while(x<y){

				// ddF_x == 2 * x + 1;
				// ddF_y == -2 * y;
				// f == x*x + y*y - radius*radius + 2*x - y + 1;
				if(f >= 0) 
				{
					y--;
					ddF_y += 2;
					f += ddF_y;
				}
				x++;
				ddF_x += 2;
				f += ddF_x;   

				for (int i = X-x;i<=X+x;i++)
					try {
						int num = (int)(Math.random()*10)+1;
						if(num%3==0)
							mBitmap.setPixel(i, Y+y,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-x;i<=X+x;i++)
					try {
						int num = (int)(Math.random()*10)+1;
						if(num%3==0)
							mBitmap.setPixel(i, Y-y, Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-y;i<=X+y;i++)
					try {
						int num = (int)(Math.random()*10)+1;
						if(num%3==0)mBitmap.setPixel(i, Y+x,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-y;i<=X+y;i++)
					try{
						int num = (int)(Math.random()*10)+1;
						if(num%3==0)
							mBitmap.setPixel(i, Y-x,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
			}
		}      	
		else if (brushStyle.equals("Fill")) {
			//System.out.println("lets fill (:");
			
			//for with asynctask
			//new FillTask(new Point(X, Y), mBitmap.getPixel(X, Y), Color.rgb(redValue, greenValue, blueValue)).execute(); //test threshold with 10
			
			//without asynctask
			ProgressBar pBar = new ProgressBar(canvasAct);
			pBar.setIndeterminate(true);
			pBar.setVisibility(View.VISIBLE);
			floodFill(new Point(X, Y), mBitmap.getPixel(X, Y), Color.rgb(redValue, greenValue, blueValue));
			pBar.setVisibility(View.INVISIBLE);
		}
		else
		{
			//Circle Brush

			int r = brushSize/2;
			int f = 1 - r;
			int ddF_x = 1;
			int ddF_y = -2 * r;
			int x = 0;
			int y = r;


			for (int i = Y-r;i<=Y+r;i++)
				try{mBitmap.setPixel(X, i,  Color.rgb(redValue,greenValue,blueValue));}
			catch (IllegalArgumentException e1)
			{

			}
			for (int i = X-r;i<=X+r;i++)
				try{mBitmap.setPixel(i, Y,  Color.rgb(redValue,greenValue,blueValue));}
			catch (IllegalArgumentException e1)
			{

			}

			while(x < y){

				// ddF_x == 2 * x + 1;
				// ddF_y == -2 * y;
				// f == x*x + y*y - radius*radius + 2*x - y + 1;
				if(f >= 0) 
				{
					y--;
					ddF_y += 2;
					f += ddF_y;
				}
				x++;
				ddF_x += 2;
				f += ddF_x;   

				for (int i = X-x;i<=X+x;i++)
					try{
						mBitmap.setPixel(i, Y+y,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-x;i<=X+x;i++)
					try {
						mBitmap.setPixel(i, Y-y, Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-y;i<=X+y;i++)
					try{ mBitmap.setPixel(i, Y+x,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}
				for (int i = X-y;i<=X+y;i++)
					try{ mBitmap.setPixel(i, Y-x,  Color.rgb(redValue,greenValue,blueValue));}
				catch (IllegalArgumentException e1)
				{

				}


			}

		}


		//redraw
		invalidate();


		//    	if ((e.getDownTime()/(count*1000))>1)
		//    	{
		//    		undoList.add(mBitmap);
		//    		count++;
		//    	}
		//    	

		if (e.getAction()==MotionEvent.ACTION_UP)//if the user picks up their finger ->save the image for redo/undo
		{    		
			undoList.add(convertToSnap(mBitmap));//adds the colours to the history array
			indexOfLast+=1;//increase the size of the history
			System.out.println("SAVED into Array");
			System.out.println("Size of History"+undoList.size());

			if (indexOfLast==9)//ten images in history 0...9
			{
				System.out.println("outofMemory");
				undoList.remove(0);
				indexOfLast--;
				System.out.println("Size of History"+undoList.size());

			}
		}

		return true;

	}

	public void floodFill(Point node, int targetColor, int replacementColor) {
		int target = targetColor;
		int replacement = replacementColor;
		if (target != replacement) {
			Queue <Point> queue = new LinkedList<Point>();
			do {

				int x = node.x;
				int y = node.y;
				while (x > 0 && mBitmap.getPixel(x - 1, y) == target) {
					x--;

				}
				boolean spanUp = false;
				boolean spanDown = false;
				while (x < width && mBitmap.getPixel(x, y) == target) {
					mBitmap.setPixel(x, y, replacement);
					if (!spanUp && y > 0
							&& mBitmap.getPixel(x, y - 1) == target) {
						queue.add(new Point(x, y - 1));
						spanUp = true;
					} else if (spanUp && y > 0
							&& mBitmap.getPixel(x, y - 1) != target) {
						spanUp = false;
					}
					if (!spanDown && y < height - 1
							&& mBitmap.getPixel(x, y + 1) == target) {
						queue.add(new Point(x, y + 1));
						spanDown = true;
					} else if (spanDown && y < height - 1
							&& mBitmap.getPixel(x, y + 1) != target) {
						spanDown = false;
					}
					x++;
				}
			} while ((node = queue.poll()) != null);
		}
	}

	class FillTask extends AsyncTask<Void, Integer, Void> {

		Point pt;
		int replacementColor, targetColor;

		public FillTask(Point p, int targetColor, int replacementColor) {
			this.pt = p;
			this.replacementColor = replacementColor;
			this.targetColor = targetColor;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected Void doInBackground(Void... params) {
			floodFill(pt, targetColor, replacementColor);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			invalidate();
		}
	}
}
