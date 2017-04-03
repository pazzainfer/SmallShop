package com.leven.app.shop.util;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;

public class DrawableUtil {
	public static Uri getUriFromAssert(Context context, String imageName){
		return Uri.parse("file:///android_asset/images/" + imageName);
	}
	/**
	 * 获取APP图片资源的Uri对象
	 * @param context
	 * @param drawableID
	 * @return
	 */
	public static Uri getUri(Context context, int drawableID){
		Resources r = context.getResources();
		Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
			    + r.getResourcePackageName(drawableID) + "/"
			    + r.getResourceTypeName(drawableID) + "/"
			    + r.getResourceEntryName(drawableID));
		return uri;
//		return Uri.parse("android.resource://" + context.getPackageName()+"/"+ drawableID);
	}
	
	public static Bitmap getBitmapFromDrawable(Context context, int resourceID){
		Resources r = context.getResources();
		InputStream is = r.openRawResource(resourceID);  
		return BitmapFactory.decodeStream(is);
	}

	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
}
