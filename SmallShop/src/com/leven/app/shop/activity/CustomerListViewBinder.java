package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.view.View;
import android.widget.ImageView;

public class CustomerListViewBinder implements com.leven.app.shop.activity.CheckListAdapter.ViewBinder {  
	    @Override  
	    public boolean setViewValue(View view, Object data,  
	            String textRepresentation) {
	    	String sexWoman = view.getResources().getString(R.string.sex_woman);
	        if(view instanceof ImageView) {
	        	ImageView imageView = (ImageView) view;
	        	if(data!=null && sexWoman.equals(data)){
	        		imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.woman));
	        	} else {
	        		imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.man));
	        	}
	        	return true;
	        }  
	        return false;
	    }  
	      
	}