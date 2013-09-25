package com.example.paint4.Fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.paint4.MainActivity;
import com.example.paint4.R;
import com.example.paint4.CustomObjects.ColourListener;
import com.example.paint4.CustomObjects.ColourPair;
import com.example.paint4.Dialog.ColourPicker;
import com.example.paint4.Views.ScratchPadView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends Fragment implements ColourListener {

	private View view;
	
	private ImageView colourBox;
	private static ColourPair mSelectedColour;
	
	private SeekBar brushSizeSeek;
	private SeekBar opacitySeek;
	
	private TextView brushSize;
	private TextView opacity;
	
	private static int mBrushSize;
	
	private static int mOpacity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout scratchLayout = new LinearLayout(getActivity());
		scratchLayout.setGravity(Gravity.CENTER);
		ScratchPadView scratchView = new ScratchPadView(getActivity());
		scratchLayout.addView(scratchView);

		view = inflater.inflate(R.layout.settings_view, layout, true); //set parent view later
		
		layout.addView(scratchLayout);
		
		mSelectedColour = MainActivity.getCurrentColourPair();
		
		return layout;
	}

	@Override
	public void onStart ()
	{
		super.onStart();
		updateControls();
	}	
	
	// Updates the settings so they reflect those in the MainActivity	
	public void updateControls()
	{
		colourBox = (ImageView) view.findViewById(R.id.colourBox);
		colourBox.setBackgroundColor(mSelectedColour.getSelectedColour());
		colourBox.setOnTouchListener(new ColourBoxTouchHandler());

		brushSizeSeek = (SeekBar) view.findViewById(R.id.brushSizeSeek);
		brushSize = (TextView) view.findViewById(R.id.brushSize);

		mBrushSize = MainActivity.getBrushSize();
		brushSizeSeek.setProgress(mBrushSize - 5);
		brushSize.setText(mBrushSize+ "px");
		
		brushSizeSeek.setOnSeekBarChangeListener(new SeekBarListener(SeekType.Size));

		opacitySeek = (SeekBar) view.findViewById(R.id.OpacitySeek);
		opacity = (TextView) view.findViewById(R.id.Opacity);
		
		mOpacity = MainActivity.getOpacity();
		opacitySeek.setProgress(mOpacity);
		opacity.setText(mBrushSize + "");
		
		opacitySeek.setOnSeekBarChangeListener(new SeekBarListener(SeekType.Opacity));
	}

	public static void onBackToCanvas (boolean save)
	{
		if (save && mSelectedColour != null)
		{
			MainActivity.setCurrentColourPair(mSelectedColour);
			MainActivity.setBrushSize(mBrushSize);
			MainActivity.setOpacity(mOpacity);
		}
			
	}

	public void onChangeColour () {
		ColourPicker myPicker = new ColourPicker(this.getActivity(), mSelectedColour, this);
		myPicker.show();
	}
	
	@Override
	public void onHiddenChanged (boolean hidden)
	{
		if (!hidden)
		{
			updateControls();
		}
	}

	@Override
	public void onSelected(int mainColour, int selectedColour) {

		mSelectedColour = new ColourPair(mainColour, selectedColour);
		colourBox.setBackgroundColor(selectedColour);
		view.invalidate();
	}
	
	public static int getColour()
	{
		return mSelectedColour.getSelectedColour();
	}
	
	public static int getBrushSize()
	{
		return mBrushSize;
	}
	
	public static int getOpacity()
	{
		return mOpacity;
	}

	private class ColourBoxTouchHandler implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			onChangeColour();
			return false;
		}

	}

	public void DialogOkBtnClick (View v) {

	}

	public void DialogCancelBtnClick (View v) {

	}

	private class SeekBarListener implements OnSeekBarChangeListener
	{
		SeekType seekbarType;
		private SeekBarListener (SeekType type)
		{
			seekbarType = type;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekbar, int progress, boolean userInitiated) {
			
			if(seekbarType == SeekType.Size)
			{
				mBrushSize = progress + 5;
				brushSize.setText((progress + 5) + "px");
			}
			else if (seekbarType == SeekType.Opacity)
			{
				mOpacity = progress;
				opacity.setText(progress + "");
			}
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {

		}
	
	}
		
	private enum SeekType
	{
		Size,
		Opacity;
	}



}
