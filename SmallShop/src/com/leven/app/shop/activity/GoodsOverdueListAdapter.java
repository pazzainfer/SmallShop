package com.leven.app.shop.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class GoodsOverdueListAdapter extends SimpleAdapter {
	private HashMap<Integer, Boolean> checkMap;
	private List<? extends Map<String, ?>> mData;
	private boolean isCheckState = false;

	public GoodsOverdueListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to, boolean isCheckState) {
		super(context, data, resource, from, to);
		mData = data;
		this.isCheckState = isCheckState;
		checkMap = new HashMap<Integer, Boolean>();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		final ImageView checkView = (ImageView)view.findViewById(R.id.overdue_list_checkbox);
		final ImageView arrowView = (ImageView)view.findViewById(R.id.overdue_list_arrow);
		if(isCheckState){
			final Drawable checkboxNO = view.getResources().getDrawable(R.drawable.checkbox_no);
			final Drawable checkboxYES = view.getResources().getDrawable(R.drawable.checkbox_yes);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isChecked(position)){
						checkMap.remove(position);
						checkView.setImageDrawable(checkboxNO);
					}else {
						checkMap.put(position, true);
						checkView.setImageDrawable(checkboxYES);
					}
				}
			});
			checkView.setVisibility(View.VISIBLE);
			arrowView.setVisibility(View.INVISIBLE);
			Boolean isCheck = checkMap.get(position);
			if(isCheck!=null && isCheck){
				checkView.setImageDrawable(checkboxYES);
			}else {
				checkView.setImageDrawable(checkboxNO);
			}
		} else {
			checkView.setVisibility(View.INVISIBLE);
			arrowView.setVisibility(View.VISIBLE);
		}
		return view;
	}
	
	public Object getItem(int position) {
		return mData.get(position);
	}
	
	public void setChecked(int position){
		Boolean isCheck = checkMap.get(position);
		if(isCheck!=null && isCheck){
			checkMap.put(position, false);
		} else {
			checkMap.put(position, true);
		}
	}
	
	public boolean isChecked(int position){
		Boolean isCheck = checkMap.get(position);
		return isCheck!=null && isCheck;
	}
	
	public HashMap<Integer, Boolean> getCheckMap() {
		return checkMap;
	}

	public void setCheckState(boolean isCheckState) {
		this.isCheckState = isCheckState;
		checkMap.clear();
	}
}
