package com.example.paint4.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;

import com.example.paint4.MainActivity;
import com.example.paint4.R;
import com.example.paint4.Views.PictureView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CanvasFragment extends Fragment {
	
	private FrameLayout layout;
	private PictureView view;
	private ImageButton draggableBtn;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Context context = this.getActivity();
		
		layout = new FrameLayout(context);
		
		view = new PictureView(context);
		
		draggableBtn = (ImageButton) inflater.inflate(R.layout.draggable_button, container, false);
		draggableBtn.setOnTouchListener(new DraggableButtonListener ());
		
		layout.addView(view);
		layout.addView(draggableBtn);
		
		return layout;
	}

	public void save() {
		view.save();
		
	}

	public void newCanvas() {
		view.newCanvas();
		
	}

	public void undo() {
		view.undo();
		
	}
	
	public void redo() {
		view.redo();
	}

	public void setImage(Bitmap image) {
		view.setImage(image);
		
	}
	
	private class DraggableButtonOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View arg0) {
			MainActivity activity = (MainActivity)getActivity();
			activity.draggableBtnClick(draggableBtn);
		}
		
	}
	
	private class DraggableButtonListener implements OnTouchListener
	{
		private long firstDown;
		private boolean start;
		private final long HOLD_LENGTH = 750;

		@Override
		 public boolean onTouch(View v, MotionEvent me) 
		{
			if (me.getAction() == MotionEvent.ACTION_DOWN)
			{
				start = true;
				firstDown = System.currentTimeMillis();
			}
			else if (me.getAction() == MotionEvent.ACTION_MOVE && System.currentTimeMillis() - firstDown > HOLD_LENGTH)
			{
				if (start)
				{
					Vibrator vib = (Vibrator)(getActivity().getSystemService(Context.VIBRATOR_SERVICE));
					vib.vibrate(100);
					start = false;
				}
				
                LayoutParams params = new LayoutParams(v.getWidth(),  v.getHeight());
                //set the margins. Not sure why but multiplying the height by 1.5 seems to keep my finger centered on the button while it's moving
                params.setMargins((int)me.getRawX() - v.getWidth()/2, (int)(me.getRawY() - v.getHeight()), (int)me.getRawX() - v.getWidth()/2, (int)(me.getRawY() - v.getHeight()*1.5));
                v.setLayoutParams(params);
             }
			else if (me.getAction() == MotionEvent.ACTION_UP)
			{
				start = false;
				
				// consume the event if the user had been dragging the button around
				// otherwise let the method return false so that the onClick is detected
				if (System.currentTimeMillis() - firstDown > HOLD_LENGTH)
					return true;
			}
             return false;
        }
		
		
	}

}
