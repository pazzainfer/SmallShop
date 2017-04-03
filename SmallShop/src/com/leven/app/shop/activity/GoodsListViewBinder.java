package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
/**
 * 商品列表数据绑定器，用于将Bitmap数据转化为图片
 * @author Felix Lee
 * @2015年12月8日 @下午8:43:57
 */
public class GoodsListViewBinder implements com.leven.app.shop.activity.CheckListAdapter.ViewBinder {  
    @Override  
    public boolean setViewValue(View view, Object data,  
            String textRepresentation) {  
        if(view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
        	if(data instanceof Bitmap){
	            Bitmap bmp = (Bitmap) data;  
	            imageView.setImageBitmap(bmp);  
	            return true;  
        	} else if(data == null){
        		imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.picture_add));
        		return true;  
        	}
        }  
        return false;
    }  
      
}