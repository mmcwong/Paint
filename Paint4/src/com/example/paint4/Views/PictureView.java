package com.example.paint4.Views;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import com.example.paint4.MainActivity;
import com.example.paint4.CustomObjects.MyPoint;
import com.example.paint4.CustomObjects.SnapShot;


public class PictureView extends View {

	protected Canvas c;
	protected Bitmap b;
	protected int [] colors;	
	protected int width, height;

	private int originX = 0, originY = 0, historyIndex; 
	private float previousX = -1, previousY = -1, previousX2 = -1, previousY2 = -1;

	private boolean inDoubleRight, inDoubleUp;
	private Path path = new Path();
	private List<SnapShot> history;
	private ProgressDialog fillDialog;

	private static final int SWIPE_DISTANCE_RIGHT = 100, SWIPE_DISTANCE_UP = 50; //pixels
	private static final double PERCENTAGE_OF_SCREEN_FOR_SWIPE = 20; 

	public PictureView (Context context)
	{
		super(context);
		width = MainActivity.getWidth();
		height = MainActivity.getHeight();
	    
		history = new LinkedList<SnapShot> ();
		historyIndex = -1;
		b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//each pixel stored on 4 bytes
		c = new Canvas(b);

		newCanvas();
		int [] pixels = new int [width*height];

		b.getPixels(pixels, 0, width, 0, 0, width, height);

	}

	public void addCurrentBitmapToHistory() {

		System.out.println("addtohistory");
		int [] pixels = new int [width*height];
		b.getPixels(pixels, 0, width, 0, 0, width, height);
		System.out.println(b.getPixel(0, 0));
		SnapShot saved = new SnapShot (pixels);

		if (history.size() == 8) 
		{
			history.remove(0);
			historyIndex -- ;
		}

		historyIndex++;

		history.add(historyIndex, saved);
		history = history.subList(0, historyIndex+1);
	}

	public void undo() {

		if(historyIndex > 0) {
			historyIndex--;
			SnapShot prev = history.get(historyIndex);
			b.setPixels(prev.getPixels(), 0, width, 0, 0, width, height);
			invalidate();
		}		
	}

	public void redo() {

		if(historyIndex + 1 < history.size()) {
			historyIndex++;
			SnapShot next = history.get(historyIndex);
			b.setPixels(next.getPixels(), 0, width, 0, 0, width, height);
			invalidate();
		}		
	}

	@Override
	public boolean onTouchEvent (MotionEvent event) {
	if (event.getActionMasked() == MotionEvent.ACTION_UP && event.getPointerCount() == 1 && (inDoubleRight == true || inDoubleUp == true)){
			handleUp(event);
		}

		else if (event.getPointerCount() == 1) {
			draw(event);
		}  

		if(event.getActionMasked() == MotionEvent.ACTION_UP && MainActivity.getCurrentTool() != "fill") {

			//			Paint p = getPaint(MainActivity.getCurrentColour(), MainActivity.getBrushSize());
			////			p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			////			c.drawPath(path, p);
			////			p.setColor(MainActivity.getCurrentColour());
			////			System.out.println("Alpha + "+ MainActivity.getOpacity());
			////			p.setAlpha(MainActivity.getOpacity());
			////			c.drawPath(path, p);
			//			p = getPaint(MainActivity.getCurrentColour(), MainActivity.getBrushSize());
			//			float numberOfPasses = 20;
			//			float maxWidth = MainActivity.getBrushSize();
			//			for (float i = 0; i <= numberOfPasses; i++){
			//			    int alpha = (int) (i / numberOfPasses * 255f);
			//			    float width = maxWidth * (1 - i / numberOfPasses);
			//			    
			//			    p.setAlpha(alpha);
			//			    p.setStrokeWidth(width);
			//			    c.drawPath(path, p);
			//			}

			previousX = -1;
			previousY = -1;
			previousX2 = -1;
			previousY2 = -1;
			path = new Path();
			addCurrentBitmapToHistory();
			System.out.println("ADDED");
		}

		return true;
	}

	private void draw (MotionEvent event)
	{	
		int colour = getColour();
		int brushSize = getBrushSize();

		if (MainActivity.getCurrentTool().equals("brush")) {
			//quadDraw(event, colour, brushSize);
			cubicDrawTest(event, colour, brushSize);
		}
		else if (MainActivity.getCurrentTool().equals("eraser")) {
			cubicDrawTest(event, Color.WHITE, brushSize);
		}
		else if (MainActivity.getCurrentTool().equals("pencil")) {
			cubicDrawTest(event, colour, 2);
		}
		else if (MainActivity.getCurrentTool().equals("spray")) {
			myDraw(event);
		}
		else if (MainActivity.getCurrentTool().equals("fill")){
			floodFill(new Point ((int)event.getX(), (int)event.getY()), colour);
		}
		else if (MainActivity.getCurrentTool().equals("dropper")) {
			dropperSelected(event);
		}
	}

	protected int getColour ()
	{
		return MainActivity.getCurrentColour();
	}

	protected int getBrushSize ()
	{
		return MainActivity.getBrushSize();
	}
	private void dropperSelected(MotionEvent event) {                                                                                
		int colour = b.getPixel((int)event.getX(), (int)event.getY());
		MainActivity.showDropper(colour);
	}
	//
	//	private void floodFill (MotionEvent event) {
	//		progress = new ProgressDialog (this.getContext());
	//		progress.setTitle("Filling");
	//		progress.setMessage("This may take a few minutes...");
	//		progress.show();
	//
	//		FloodFillThread floodFillThread = new FloodFillThread (event);
	//		floodFillThread.start();
	//	}

	//	class FloodFillThread extends Thread{
	//
	//		private MotionEvent mEvent;
	//
	//		public FloodFillThread (MotionEvent event) {
	//			mEvent = event;
	//		}
	//
	//		public void run () {
	//			floodFill2(new Point ((int)mEvent.getX(), (int)mEvent.getY()), Color.WHITE);
	//			handler.sendEmptyMessage(0);
	//		}
	//
	//		private Handler handler = new Handler() {
	//			public void handleMessage(Message msg) {
	//				invalidate();
	//				progress.dismiss();
	//			}
	//		};
	//	}

	//
	//	private void floodFill (Point p, int c) {
	//		Stack<Point> s = new Stack<Point> ();
	//		s.add(p);
	//
	//		while (s.size()>0) {
	//			Point popped = s.pop();
	//			int x = popped.x;
	//			int y = popped.y;
	//			if (b.getPixel(x, y) == c) {
	//				b.setPixel(x, y, Color.BLACK);
	//
	//				if(x+1 < MainActivity.getWidth())
	//					s.add(new Point (x + 1, y));
	//				if (x-1 >= 0)
	//					s.add(new Point (x - 1, y));
	//				if (y+1 < MainActivity.getHeight())
	//					s.add(new Point (x, y + 1));
	//				if(y-1 >= 0)
	//					s.add(new Point (x, y - 1));				
	//			}
	//
	//		}
	//	}

	private Paint getPaint (int colour, int width)
	{
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(colour);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAlpha(MainActivity.getOpacity());
		paint.setStrokeWidth(width);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);

		return paint;
	}

	private void cubicDraw (MotionEvent event, int colour, int width) {

		if (event.getAction() == MotionEvent.ACTION_UP)
			return;

		float x = event.getX();
		float y = event.getY();

		if (previousX == -1 && previousY == -1) { // check to see if it is the first point
			path.moveTo(x, y);
			previousX = x;
			previousY = y;
		}

		else 
		{
			ArrayList<Point> points = new ArrayList<Point>();
			points.add(new Point((int)previousX, (int)previousY));
			
			int historySize = event.getHistorySize();
			int pointerCount = event.getPointerCount();

			for (int h = 0; h < historySize; h++) 
			{
				for (int p = 0; p < pointerCount; p++)
				{				
					int historicalX = (int)event.getHistoricalX(p, h);
					int historicalY = (int)event.getHistoricalY(p, h);

					points.add(new Point(historicalX, historicalY));
				}
			}
			points.add(new Point ((int)x, (int)y));

			// at this point the 'points' arraylist contains a list of points that we want to join
			
			for (int i = 1; i < points.size() - 1; i++)
			{
				Point prev = points.get(i - 1);
				Point current = points.get(i);
				Point next = points.get(i + 1);
				
				path.cubicTo((current.x + prev.x)/2, (current.y + prev.y)/2, (current.x + next.x)/2, (current.y + next.y)/2, current.x, current.y);			
			}
			Point prev = points.get(points.size() - 2);
			Point last = points.get(points.size() - 1);
			
			path.quadTo((prev.x + last.x)/2, (prev.y + last.y)/2, last.x, last.y);
			previousX = x;
			previousY = y;	

			invalidate();
		}

		Paint paint = getPaint(colour, width);
		
		c.drawPath(path, paint);

	}
	
	private void cubicDrawTest (MotionEvent event, int colour, int width) {

			if (event.getAction() == MotionEvent.ACTION_UP)
				return;

			float x = event.getX();
			float y = event.getY();

			if (previousX == -1 && previousY == -1) { // check to see if it is the first point
				path.moveTo(x, y);
				previousX = x;
				previousY = y;
			}

			else 
			{

//				path.reset();
//				path.moveTo(previousX, previousY);
				
				ArrayList<Point> points = new ArrayList<Point>();
				points.add(new Point((int)previousX, (int)previousY));
				
				int historySize = event.getHistorySize();
				int pointerCount = event.getPointerCount();

				for (int h = 0; h < historySize; h++) 
				{
					for (int p = 0; p < pointerCount; p++)
					{				
						int historicalX = (int)event.getHistoricalX(p, h);
						int historicalY = (int)event.getHistoricalY(p, h);

						points.add(new Point(historicalX, historicalY));
					}
				}
				points.add(new Point ((int)x, (int)y));

				// at this point the 'points' arraylist contains a list of points that we want to join
				
				path = splineInterp(points, path, colour, width);
				
				Paint paint = getPaint(colour, width);
				
				c.drawPath(path, paint);				

				invalidate();
			}
			
			previousX = x;
			previousY = y;	

			System.out.println("MovedTo: "+x + ", "+y);
		}
	
	private Path splineInterp(List<Point> points, Path path, int colour, int width)
	{
		for (int i = 1; i < points.size(); i++)
		{
			Point prev = points.get(i - 1);
			Point curr = points.get(i);
			
			if(i == 1)
			{
				path.quadTo(prev.x, prev.y, curr.x, curr.y);
				System.out.println("Start at " +curr.x + ", "+curr.y);


			}
			else
			{			
				Point prev2 = points.get(i - 2);
				path.cubicTo(prev2.x, prev2.y, prev.x, prev.y, curr.x, curr.y);
				System.out.println("Start at " +curr.x + ", "+curr.y);

			}		
		}
		
		return path;
	}
		
	private void cubicDrawLineEquation (MotionEvent event, int colour, int width) {

		if (event.getAction() == MotionEvent.ACTION_UP)
			return;

		float x = event.getX();
		float y = event.getY();

		if (previousX == -1 && previousY == -1) { // check to see if it is the first point
			path.moveTo(x, y);
			previousX = x;
			previousY = y;
		}

		else 
		{
			ArrayList<Point> points = new ArrayList<Point>();
			points.add(new Point((int)previousX, (int)previousY));
			
			int historySize = event.getHistorySize();
			int pointerCount = event.getPointerCount();

			for (int h = 0; h < historySize; h++) 
			{
				for (int p = 0; p < pointerCount; p++)
				{				
					int historicalX = (int)event.getHistoricalX(p, h);
					int historicalY = (int)event.getHistoricalY(p, h);

					points.add(new Point(historicalX, historicalY));
				}
			}
			points.add(new Point ((int)x, (int)y));
			
			System.out.println("Start of Points");
			for (Point p : points)
			{
				System.out.println("Point:" + p.x + ", "+ p.y);
			}
			
			// at this point the 'points' arraylist contains a list of points that we want to join
			
			for (int i = 1; i < points.size(); i++)
			{
				Point prev = points.get(i - 1);
				Point current = points.get(i);
				
				if (current.x == prev.x)
				{
					continue;
				}
				
				double m = (current.y - prev.y)* 1.0 / (current.x - prev.x);
				double b = current.y - (m*current.x);
				
				double Xmid = (current.x + prev.x)/2.0;
				
				double X1 = (Xmid + prev.x)/2;
				double Y1 = m*X1 + b;
				double X2 = (Xmid + current.x)/2;
				double Y2 = m*X2 + b;				
				
				System.out.println("cubicTo("+X1 + ", "+Y2 + ", "+X2+", "+Y2 + ", "+current.x + ", "+current.y+");");
				path.cubicTo((int)X1, (int)Y1, (int)X2, (int)Y2, current.x, current.y);
				previousX = current.x;
				previousY = current.y;	
			}	

			invalidate();
		}

		Paint paint = getPaint(colour, width);

		c.drawPath(path, paint);
	}
	
	private void quadDraw (MotionEvent event, int colour, int width) {

		if (event.getAction() == MotionEvent.ACTION_UP)
			return;

		float x = event.getX();
		float y = event.getY();

		if (previousX == -1 && previousY == -1) { // check to see if it is the first point
			path.moveTo(x, y);
			previousX = x;
			previousY = y;
		}

		else 
		{
			int historySize = event.getHistorySize();
			int pointerCount = event.getPointerCount();

			for (int h = 0; h < historySize; h++) 
			{
				for (int p = 0; p < pointerCount; p++)
				{				
					int historicalX = (int)event.getHistoricalX(p, h);
					int historicalY = (int)event.getHistoricalY(p, h);

					path.quadTo((previousX + historicalX)/2, (previousY + historicalY)/2, historicalX, historicalY);

					previousX = historicalX;
					previousY = historicalY;
				}
			}			
			path.quadTo((previousX+x)/2, (previousY+y)/2, x, y);

			invalidate();
		}

		Paint paint = getPaint(colour, width);

		c.drawPath(path, paint);
	}
	
	
	public void floodFill(Point node, int targetColour) {

		// there is a current fill operation going on
		if (fillDialog != null &&fillDialog.isShowing())
			return;
		
		fillDialog = ProgressDialog.show(this.getContext(), "Fill", "Please wait...", true);
		FloodFillTask fillTask = new FloodFillTask (node, targetColour);
		fillTask.execute();
		
	}

	private void myDraw (MotionEvent event) {

		if (previousX == -1 || previousY == -1) {
			previousX = event.getX();
			previousY = event.getY();
			return;
		}

		final int historySize = event.getHistorySize();
		final int pointerCount = event.getPointerCount();

		for (int h = 0; h < historySize; h++) {
			for (int p = 0; p < pointerCount; p++){				
				int historicalX = (int)event.getHistoricalX(p, h);
				int historicalY = (int)event.getHistoricalY(p, h);
				interpolatePoints (historicalX, historicalY, previousX, previousY);
				invalidate();
				previousX = historicalX;
				previousY = historicalY;
			}
		}

		float x = event.getX();
		float y = event.getY();

		interpolatePoints (x, y, previousX, previousY);
		invalidate();			
		previousX = x;
		previousY = y;
	}
	private void cubicDrawExample (MotionEvent event, int colour, int width) {

		if (event.getAction() == MotionEvent.ACTION_UP)
			return;

		float x = event.getX();
		float y = event.getY();

		// check to see if it is the first point
		if (previousX == -1 && previousY == -1) 
		{ 
			previousX = x;
			previousY = y;
			path.moveTo(x, y);
			path.lineTo(x, y);
		}

		else 
		{
			List<MyPoint> points = new ArrayList<MyPoint>();
			points.add(new MyPoint(previousX, previousY));
			final int historySize = event.getHistorySize();
			final int pointerCount = event.getPointerCount();

			for (int h = 0; h < historySize; h++) {
				for (int p = 0; p < pointerCount; p++){				
					int historicalX = (int)event.getHistoricalX(p, h);
					int historicalY = (int)event.getHistoricalY(p, h);

					points.add(new MyPoint(historicalX, historicalY));				

				}
			}
			points.add(new MyPoint(x,y));
//			System.out.println("START");

//			for (MyPoint p : points)
//			{
//				System.out.println ("Point" + p.x + "," + p.y);
//			}
			
			for(int i = points.size() - 2; i < points.size(); i++){
				
				if(i >= 0) {
				MyPoint point = points.get(i);

				if(i == 0){
					MyPoint next = points.get(i + 1);
					point.dx = ((next.x - point.x) / 5);
					point.dy = ((next.y - point.y) / 5);
				}
				else if(i == points.size() - 1){
					MyPoint prev = points.get(i - 1);
					point.dx = ((point.x - prev.x) / 5);
					point.dy = ((point.y - prev.y) / 5);
				}
				else{
					MyPoint next = points.get(i + 1);
					MyPoint prev = points.get(i - 1);
					point.dx = ((next.x - prev.x) / 5);
					point.dy = ((next.y - prev.y) / 5);
				}
				}
			}

			for(int i = 1; i < points.size(); i++){
				MyPoint point = points.get(i);
				MyPoint prev = points.get(i - 1);
				System.out.println("Start");
				System.out.println("Point 1" + (prev.x + prev.dx)+","+(prev.y + prev.dy));
				System.out.println("Point 2" + (point.x - point.dx)+","+(point.y - point.dy));
				System.out.println("Point 3" + (point.x)+","+(point.y));

				path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
				
				invalidate();
			}
			
			previousX = x;
			previousY = y;

		}			

		Paint paint = getPaint(colour, width);		

		c.drawPath(path, paint);
	}

	private void interpolatePoints (double X1, double Y1, double X2, double Y2) {

		if(X2 == -1 || Y2 == -1)
			return;

		interpolate(X1, Y1, X2, Y2);
	}

	private void interpolate (double X1, double Y1, double X2, double Y2) {

		if (Math.abs(X2-X1) <= 3) {
			double y1 = Math.min(Y1, Y2);
			double y2 = Math.max(Y1, Y2);		

			for (int i = (int)y1; i <= (int)y2; i+=2) {
				drawBrush((int)X1, i);
			}
			return;
		}
		double m = (Y2 - Y1)/(X2 - X1);
		double b = Y1 - m*X1;

		if (m < 1.0 ) { // slope is less than 1, interpolate along x
			double x1 = Math.min(X1, X2);
			double x2 = Math.max(X1, X2);

			for (int i = (int)x1; i <= (int)x2; i+=5) {
				int yCalc = (int) (m*i + b);
				System.out.println("DrawBrush" + i +","+ yCalc);
				drawBrush(i, yCalc);
			}
		}
		else {// interpolate along y
			double y1 = Math.min(Y1, Y2);
			double y2 = Math.max(Y1, Y2);

			for (int i = (int)y1; i <= (int)y2; i+=10) {
				int xCalc = (int)((i - b)/m);
				drawBrush(xCalc, i);
			}
		}
	}


	private void drawBrush (int x_coord, int y_coord) {

		int colour = MainActivity.getCurrentColour();
		int X = x_coord;
		int Y = y_coord;

		int r = MainActivity.getBrushSize()/2;
		int f = 1 - r;
		int ddF_x = 1;
		int ddF_y = -2 * r;
		int x = 0;
		int y = r;

		for (int i = Y-r;i<=Y+r;i++)
			try {
				int num = (int)(Math.random()*20)+1;
				if (num%10==0)
					b.setPixel(X, i,  colour);
			}
		catch (IllegalArgumentException e1)
		{

		}
		for (int i = X-r;i<=X+r;i++)
			try{
				int num = (int)(Math.random()*20)+1;
				if(num%10==0)b.setPixel(i, Y,  colour);}
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
					int num = (int)(Math.random()*20)+1;
					if(num%10==0)
						b.setPixel(i, Y+y,  colour);}
			catch (IllegalArgumentException e1)
			{

			}
			for (int i = X-x;i<=X+x;i++)
				try {
					int num = (int)(Math.random()*20)+1;
					if(num%10==0)
						b.setPixel(i, Y-y, colour);}
			catch (IllegalArgumentException e1)
			{

			}
			for (int i = X-y;i<=X+y;i++)
				try {
					int num = (int)(Math.random()*20)+1;
					if(num%10==0)b.setPixel(i, Y+x,  colour);}
			catch (IllegalArgumentException e1)
			{

			}
			for (int i = X-y;i<=X+y;i++)
				try{
					int num = (int)(Math.random()*20)+1;
					if(num%10==0)
						b.setPixel(i, Y-x,  colour);}
			catch (IllegalArgumentException e1)
			{

			}
		}
	}      	

	private void handleSwipeUp (MotionEvent event) {
		inDoubleUp = true;
		originY = (int) event.getY(1);
	}

	private void handleUp (MotionEvent event) {
		if (inDoubleRight && event.getX() - originX >= SWIPE_DISTANCE_RIGHT) {
			originX = 0;
			MainActivity.goToSettings();
		}				
		else if (inDoubleUp && originY - event.getY() >= SWIPE_DISTANCE_UP) {
			originY = 0;
			((MainActivity)getContext()).showTools();
		}
		inDoubleRight = false;
		inDoubleUp = false;
	}

	private void handleSwipeRight (MotionEvent event){		
		inDoubleRight = true;
		originX = (int) event.getX();								
	}	

	@Override
	protected void onDraw(Canvas canvas){
		System.out.println("onDraw");
		Paint paint = new Paint ();
		paint.setFilterBitmap(true);

		// The image will be scaled so it will fill the width, and the
		// height will preserve the image’s aspect ration	
		Rect dest = new Rect(0, 0, width, height);
		canvas.drawBitmap(b, null, dest, paint);
	}	

	public void save() {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/saved_images");    
		myDir.mkdirs();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		String fname = "Image-"+ date +".jpg";

		File file = new File (myDir, fname);
		if (file.exists ()) {
			file.delete (); 
		}
		try 
		{
			FileOutputStream out = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		this.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

	}

	public void newCanvas() {
		history.clear();
		historyIndex = -1;
		colors = new int [width*height];

		for (int i = 0;i<(width*height);i++) {
			colors[i] = Color.WHITE;
		}
		b.setPixels(colors, 0, width, 0, 0, width, height);
		c.drawBitmap(colors, 0, width, 0, 0, width, height, false, new Paint());
		invalidate();
		addCurrentBitmapToHistory();

	}

	public void setImage(Bitmap image) {
		history.clear();
		historyIndex = - 1;
		b = Bitmap.createBitmap(image);
		c = new Canvas (b);
		invalidate();
		addCurrentBitmapToHistory();
	}	

	private class FloodFillTask extends AsyncTask<Void, Void, Void> {

		private Point node;
		private int numberChanged;
		private int replacement;
		private Bitmap bitmap;

		private FloodFillTask (Point point, int targetColour)
		{
			node = point;
			replacement = targetColour;			
		}

		@Override
		protected void onPreExecute ()
		{
			super.onPreExecute();

			// need to create a separate bitmap to work with in this thread that is
			// separate from the bitmap in the UI thread
			bitmap = Bitmap.createBitmap(b);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			int target = bitmap.getPixel(node.x, node.y);

			if (target != replacement) 
			{
				Queue <Point> queue = new LinkedList<Point>();
				do 
				{
					int x = node.x;
					int y = node.y;
					while (x > 0 && bitmap.getPixel(x - 1, y) == target) 
						x--;

					boolean spanUp = false;
					boolean spanDown = false;

					while (x < width && bitmap.getPixel(x, y) == target)
					{
						bitmap.setPixel(x, y, replacement);
						numberChanged++;
						
						if (!spanUp && y > 0 && bitmap.getPixel(x, y - 1) == target) 
						{
							queue.add(new Point(x, y - 1));
							spanUp = true;
						} 
						else if (spanUp && y > 0 && bitmap.getPixel(x, y - 1) != target) 
						{
							spanUp = false;
						}
						if (!spanDown && y < height - 1	&& bitmap.getPixel(x, y + 1) == target) 
						{
							queue.add(new Point(x, y + 1));
							spanDown = true;
						} 
						else if (spanDown && y < height - 1	&& bitmap.getPixel(x, y + 1) != target) 
						{
							spanDown = false;
						}
						x++;
					}					
					node = queue.poll();
				} 
				while (node != null);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			fillDialog.dismiss();
			
			// don't add to history or update canvas if no pixels were changed
			if (numberChanged == 0)
				return;
			
			b = Bitmap.createBitmap(bitmap);
			c = new Canvas(b);
			invalidate();
			addCurrentBitmapToHistory();
		}
	}


}
