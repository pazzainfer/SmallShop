package com.leven.app.shop.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class GoodsSearchWidgetProvider extends AppWidgetProvider {
	public static final String ACTION_NAME = "com.leven.app.shop.action.search";
	
	@Override //更新部件时调用，在第1次添加部件时也会调用  
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  
            int[] appWidgetIds) {  
        super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override //  
    public void onReceive(Context context, Intent intent) {  
        super.onReceive(context, intent);  
    }
}
