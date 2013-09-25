package com.example.paint4.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public abstract class Tool extends View{
	
	protected Tool (Context context){
		super(context);
	}
		
	public abstract void draw (Bitmap B);

}
