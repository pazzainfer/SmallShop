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

public class CheckListAdapter extends SimpleAdapter {
	private HashMap<Integer, Boolean> checkMap;
	private List<? extends Map<String, ?>> mData;
	private boolean isCheckState = false;
	private int[] mImgIds;
	/**
	 * 带复选框的列表适配器
	 * @param context 上下文
	 * @param data 列表数据
	 * @param resource 列表项资源文件
	 * @param from 数据键数组
	 * @param to 数据显示资源数字
	 * @param imgIds 箭头及复选框的资源ID
	 * @param isCheckState 是否复选状态
	 */
	public CheckListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to,int[] imgIds, boolean isCheckState) {
		super(context, data, resource, from, to);
		mData = data;
		mImgIds = imgIds;
		this.isCheckState = isCheckState;
		checkMap = new HashMap<Integer, Boolean>();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		final ImageView checkView = (ImageView)view.findViewById(mImgIds[1]);
		final ImageView arrowView = (ImageView)view.findViewById(mImgIds[0]);
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
