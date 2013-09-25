package com.example.paint4;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.paint4.CustomObjects.AlbumStorageDirFactory;
import com.example.paint4.CustomObjects.ColourPair;
import com.example.paint4.CustomObjects.FroyoAlbumDirFactory;
import com.example.paint4.Fragments.CanvasFragment;
import com.example.paint4.Fragments.DropperFragment;
import com.example.paint4.Fragments.SettingsFragment;
import com.example.paint4.Fragments.ToolsFragment;
import com.example.paint4.Views.PictureView;


public class MainActivity extends FragmentActivity 
{
	private static int height, width;
	private static int currentBrushSize;
	private static int dropperColour;
	private static int currentOpacity;
	
	private static FragmentTransaction fragmentTransaction;
	private static FragmentManager fragmentManager;
	
	private static DropperFragment dropperFrag;
	private static CanvasFragment canvasFrag;
	private static ToolsFragment toolsFrag;
	private static SettingsFragment settingsFrag;
	
	private static String currentTool, currentPhotoPath;
	private static ColourPair selectedColour;
	
	private ImageButton draggableBtn;
	private static ToolsFragment.Location currentToolsLocation;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	public static final int DROPPER_DIALOG_TIME = 2000;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)	
	@Override
	public void onCreate(Bundle b) 
	{
		super.onCreate(b);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		currentTool = "brush";
		currentBrushSize = 20;
		currentOpacity = 255;
		selectedColour = new ColourPair(Color.RED, Color.BLACK);
		currentToolsLocation = (ToolsFragment.Location.BOTTOM);

		canvasFrag = new CanvasFragment();
		settingsFrag = new SettingsFragment();
				
		toolsFrag = new ToolsFragment();
		dropperFrag = new DropperFragment();		
		
		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		
		fragmentTransaction.add(R.id.frame, settingsFrag);
		fragmentTransaction.add(R.id.frame, canvasFrag);
	
		fragmentTransaction.hide(settingsFrag);
		
		fragmentTransaction.commit();
		setContentView(R.layout.activity_main);	
	}
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void goToSettings () {
		Log.i("Main_paint3", "go to settings");
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
		fragmentTransaction.hide(canvasFrag);
		fragmentTransaction.hide(toolsFrag);
		fragmentTransaction.show(settingsFrag);
		fragmentTransaction.addToBackStack("tag");
		fragmentTransaction.commit();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void backToCanvas() {
		Log.i("Main_paint3", "back to canvas");
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);	
		fragmentTransaction.hide(settingsFrag);
		fragmentTransaction.show(toolsFrag);
		fragmentTransaction.show(canvasFrag);
		fragmentTransaction.addToBackStack("tag");
		fragmentTransaction.commit();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showTools() {
		fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(R.anim.slide_down_bottom, R.anim.slide_up_bottom);
		fragmentTransaction.add(R.id.frame2, toolsFrag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		
		setLocationOfTools(currentToolsLocation);
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void hideTools() {
		Log.i("Main_paint3", "hide tools");
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.slide_down_bottom, R.anim.slide_up_bottom);
		fragmentTransaction.remove(toolsFrag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void showDropper(int colour) {
		
		if (dropperFrag.isAdded())
			return;
		
		dropperColour = colour;
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.frame, dropperFrag);
		fragmentTransaction.commit();

		Timer t = new Timer ();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				removeDropperFrag();
			}
			
		}, DROPPER_DIALOG_TIME);//remove the fragment after 1 second
		
	}
	
	private void setLocationOfTools (ToolsFragment.Location location)
	{
		FrameLayout toolsLayout = (FrameLayout)findViewById(R.id.frame2);
		
		if (location == ToolsFragment.Location.BOTTOM)
			toolsLayout.setPadding(toolsLayout.getPaddingLeft(), (int)(height*0.9), toolsLayout.getPaddingRight(), toolsLayout.getPaddingBottom());
		else 
			toolsLayout.setPadding(0, 0, 0, 0);
	}
	
	public void switchBtnClick (View v) 
	{		
		if (currentToolsLocation == ToolsFragment.Location.BOTTOM)
			currentToolsLocation = ToolsFragment.Location.TOP;
		else
			currentToolsLocation = ToolsFragment.Location.BOTTOM;
		
		setLocationOfTools(currentToolsLocation);
	}
	
	public void settingsBtnClick (View v) {
		goToSettings();
	}
	
	public void useDropperBtnClick (View v) {
		selectedColour = new ColourPair(selectedColour.getMainColour(), dropperColour);
		removeDropperFrag();
		currentTool = "brush";
	}
	
	public void draggableBtnClick (View view) 
	{
		showTools();
		
		draggableBtn = (ImageButton)view; 
		draggableBtn.setVisibility(View.INVISIBLE);
	}
	
	public void hideBtnClick (View v)
	{
		System.out.println("hidetoolszz");
		draggableBtn.setVisibility(View.VISIBLE);
		hideTools();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void removeDropperFrag ()
	{
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.remove(dropperFrag);
		fragmentTransaction.commit();
	}
	
	public void backToCanvasSave (View v) 
	{
		SettingsFragment.onBackToCanvas(true);
		backToCanvas();
	}
	
	public void backToCanvasCancel (View v) 
	{
		SettingsFragment.onBackToCanvas(false);
		backToCanvas();
	}
	
//	public void userDropper (View v) {
//		int selected = Color.rgb(Color.red(dropperColour), Color.green(dropperColour), Color.blue(dropperColour));
//		int main = 
//		settings.setColourPair(new ColourPair(main, selected));
//	}
	
	public void brushBtnClick (View v) {
		currentTool = "brush";
	}
	
	public void fillBtnClick(View v) {
		currentTool = "fill";
	}
	
	public void saveBtnClick (View v) {		
		useAlert("Save", "Save");		
	}
	
	public void loadBtnClick (View v) {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult (intent, 2);
	}
	
	public void newBtnClick (View v) {
		useAlert2("Create New Image", "Do you want to save your current image before starting a new one?","New");
	}
	
	public void eraserBtnClick (View v) {
		currentTool = "eraser";
	}
	
	public void pencilBtnClick (View v) {
		currentTool = "pencil";
	}
	
	public void sprayBtnClick (View v) {
		currentTool = "spray";
	}
	
	public void dropperBtnClick (View v) {
		currentTool = "dropper";
	}
	
	public void cameraBtnClick (View v) {
		useAlert2("Capture New Image", "Do you want to save your current image before capturing a new one?", "Camera");
	}
	
	public void undoBtnClick (View v) {
		canvasFrag.undo();
	}
	
	public void redoBtnClick (View v) {
		canvasFrag.redo();
	}
 	
	private void capturePicture () {
		dispatchTakePictureIntent(1);//take picture
	}
	
	 private void dispatchTakePictureIntent(int actionCode) {

			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			switch(actionCode) {
			case 1:
				File f = null;
				
				try {
					f = setUpPhotoFile();
					currentPhotoPath = f.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				} catch (IOException e) {
					e.printStackTrace();
					f = null;
					currentPhotoPath = null;
				}
				break;

			default:
				break;			
			} // switch

			startActivityForResult(takePictureIntent, actionCode);
	 }
	 
	 private File setUpPhotoFile() throws IOException {
			
			File f = createImageFile();
			currentPhotoPath = f.getAbsolutePath();
			
			return f;
		}
	 
	    @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {	    	
	    	  
	        if (requestCode == 2 && null != data) {
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    
	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();	    
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String picturePath = cursor.getString(columnIndex);
	            cursor.close();
	            Bitmap workingBitmap = BitmapFactory.decodeFile(picturePath);
	            
	            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
	            
	       	 	double widthScale = mutableBitmap.getWidth()/(width * 1.0);
	       	 	double heightScale = mutableBitmap.getHeight()/(height * 1.0);
    		 	double scale = Math.min(widthScale, heightScale);

    		 	mutableBitmap = Bitmap.createBitmap(mutableBitmap, 0, 0, (int)(scale*width), (int)(scale*height));
	            mutableBitmap = Bitmap.createScaledBitmap(mutableBitmap, width, height, false);
	            canvasFrag.setImage(mutableBitmap);
	            // String picturePath contains the path of selected Image
	        }
	        
			if (currentPhotoPath != null) {
				setPic();
//				galleryAddPic();
				currentPhotoPath = null;
			}
		}
	    
	    private void setPic() {
//	    	Bundle extras = intent.getExtras();
//			Bitmap temp = (Bitmap) extras.get("data");//get the bitmap image
//			
//			int [] pixels = new int[temp.getHeight()*temp.getWidth()];
//			System.out.println(temp.getHeight()+","+temp.getWidth());
//			temp.getPixels(pixels,0,temp.getWidth(),0,0,temp.getWidth(),temp.getHeight());
//			Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
//					profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
//			image.setPixels(pixels,0,temp.getWidth(),0,0,width,height);
	     	
	    	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    	    bmOptions.inJustDecodeBounds = true;
	    	    System.out.println("CURRENTPHOTOPATH" + currentPhotoPath);
	    	    BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
	    	    int photoW = bmOptions.outWidth;
	    	    int photoH = bmOptions.outHeight;
	    	    
	    	    //scales the image from the camera to the display area.  (screen resolution)
	    	    int scaleFactor = 1;
	    	    scaleFactor = Math.min(photoW/width, photoH/height);
	    		    		
	    		 bmOptions.inJustDecodeBounds = false;
	    		 bmOptions.inSampleSize = scaleFactor+1;
	    		 bmOptions.inPurgeable = true;
	    		  
	    		 Bitmap image = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
	    		 
	    		 if (image == null)
	    			 return;
	    		 
	    		double widthScale = image.getWidth()/(width * 1.0);
		       	double heightScale = image.getHeight()/(height * 1.0);
	    		double scale = Math.min(widthScale, heightScale);

	    		 image = Bitmap.createBitmap(image, 0, 0, (int)(scale*width), (int)(scale*height));
	    		 image = Bitmap.createScaledBitmap(image, width, height, false);  
	    		 canvasFrag.setImage(image);
	    }
	 
	    private File createImageFile() throws IOException{
	        // Create an image file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String imageFileName = "IMG_" + timeStamp + "_";
	        File albumF = getAlbumDir();
			File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
	        return imageF;
	    }
	    
	    private String getAlbumName() {
	    	return getString(R.string.album_name);
	    }
	    
	    private File getAlbumDir() {
	    	File storageDir = null;

	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
			} 
	    	else {
				System.out.println("BASE");
			}
	    	
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				
				storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

				if (storageDir != null) {
					if (! storageDir.mkdirs()) {
						if (! storageDir.exists()){
							Log.d("CameraSample", "failed to create directory");
							return null;
						}
					}
				}
				
			} else {
				Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
			}
			
			return storageDir;
		}
	    
	
	private void useAlert (String title, final String action) {
		
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage("Do you want to save your image?");
		
		confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(action.equals("Save"))
	                canvasFrag.save();	           
	           }
	    });
		confirm.setNegativeButton("Cancel", null);
		AlertDialog confirmDialog = confirm.create();
		confirmDialog.show();
	}
	
	private void useAlert2 (String title, String msg, final String action) {
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(msg);
		confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
        		   canvasFrag.save();

	        	   if(action.equals("New")) {
	        		   canvasFrag.newCanvas();
	        	   }
	        	   else if (action.equals("Camera")) {
	        			capturePicture();
	        	   }
	          }
	    });
		
		confirm.setNegativeButton("No",  new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if(action.equals("New")) {
	                canvasFrag.newCanvas();
	        	   }
	        	   else if (action.equals("Camera")) {
	        		   capturePicture();
	        	   }
	               		}
	    });
		confirm.setNeutralButton("Cancel", null);
		AlertDialog confirmDialog = confirm.create();
		confirmDialog.show();
	}
	
	public static void setCurrenttool (String tool) {
		currentTool = tool;
	}
	
	public static void setOpacity(int opacity) {
		currentOpacity = opacity;
	}

	public static int getHeight() {
		return height;
	}

	public static int getWidth () {
		return width;
	}
	
	public static String getCurrentTool() {
		return currentTool;
	}

	public static int getCurrentColour() {
		return selectedColour.getSelectedColour();
	}

	public static void setCurrentColourPair(ColourPair colour) {
		selectedColour = colour;
	}

	public static ColourPair getCurrentColourPair() {
		return selectedColour;
	}

	public static int getBrushSize() {
		return currentBrushSize;
	}
	
	public static void setBrushSize (int size) {
		currentBrushSize = size;
	}

	public static int getDropperColour() {
		return dropperColour;
	}

	public static int getOpacity() {
		return currentOpacity;
	}

}
