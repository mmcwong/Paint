package com.example.paint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.EditText;

public class OpenGL extends Activity {
	
    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);    
        }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_open_gl, menu);
        return true;
    }
    
    
    class MyGLSurfaceView extends GLSurfaceView {

    	private final MyRenderer mRenderer;
        public MyGLSurfaceView(Context context){
            super(context);
            
            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);
            // Set the Renderer for drawing on the GLSurfaceView
            mRenderer = new MyRenderer();
            setRenderer(mRenderer);
         // Render the view only when there is a change in the drawing data
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        }
        
        @Override 
        //touch events on the Open Gl screen
        public boolean onTouchEvent (MotionEvent e)
        {
        	 // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                	
                	System.out.println("HERE");
                	//mRenderer.color [1] += 0.005f;
                	mRenderer.color [2] += 0.005f;
//                	mRenderer.color [3] += 0.5f;
                	
                	requestRender();
                   
            }

            return true;
        }
    }
}
