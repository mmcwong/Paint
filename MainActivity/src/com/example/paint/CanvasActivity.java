package com.example.paint;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CanvasActivity extends Activity {


	public static BitMapView bitMapView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {//called on create 
        super.onCreate(savedInstanceState);
        bitMapView = new BitMapView (this);
        setContentView(bitMapView);	
    }
	
    public void onPause()
    {
    	super.onPause();
    	MainActivity.image = bitMapView.mBitmap;//save the image from the view into the main activity
    	MainActivity.list = bitMapView.undoList;//save the undo list into the mainactivity
    	MainActivity.indexOfLast = bitMapView.indexOfLast;//saves the index 
    }
    
    public void onResume()
    {
    	super.onResume();//required 
    	  if(MainActivity.image==null)
    	  {
    	        bitMapView = new BitMapView (this);
    	        setContentView(bitMapView);
    	  }
    	  else//if there is already a picture
    	  {
    	      	bitMapView.mBitmap=MainActivity.image;//put back the image on screen
    	      	bitMapView.undoList=MainActivity.list;//put the list of undo
    	      	bitMapView.indexOfLast=MainActivity.indexOfLast;//put the index back
    	       	bitMapView.invalidate();
    	  }
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {//when a menu item is selected
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_undo://if the undo is selected
                undo();
                return true;
                
            case R.id.menu_redo:
            	redo();
            	return true;
       }
        return true;
    }
    
    public void undo ()//undo function
    {
    	System.out.println("size"+bitMapView.undoList.size());
    	System.out.println("index "+bitMapView.indexOfLast);
    	System.out.println(bitMapView.indexOfLast>0);
    	
    	if (bitMapView.indexOfLast>0)
    	{
    		System.out.println("LIGHTS");
        	bitMapView.setBitMap(bitMapView.undoList.get(bitMapView.indexOfLast-1));
        	System.out.println(bitMapView.indexOfLast>0);
        	bitMapView.end=false;//index no longer at the end
        	bitMapView.indexOfLast -= 1;

    	}
    	System.out.println("After");
    	System.out.println("size"+bitMapView.undoList.size());
    	System.out.println("index "+bitMapView.indexOfLast);
    	System.out.println(bitMapView.indexOfLast>0);

    }
    
    public void redo()//redo function
    {

    	if (bitMapView.indexOfLast<(bitMapView.undoList.size()-1))
    	{
    		bitMapView.setBitMap(bitMapView.undoList.get(bitMapView.indexOfLast+1));
    		if(bitMapView.indexOfLast==(bitMapView.undoList.size()-2))//come back to the end of the list
    		{
    			bitMapView.end=true;
    			
    		}
    		else
    		{
    			bitMapView.end=false;
    		}
        	bitMapView.indexOfLast += 1;
    	}
    	
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_canvas, menu);
        return true;
    }
    

    
}


	



