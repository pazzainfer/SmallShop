package com.leven.app.shop.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.AsyncTask;
import android.view.inputmethod.InputMethodManager;
/**
 * Android UI工具类
 * @author Felix Lee
 * @2015年12月10日 @下午12:58:01
 */
public class UIUtil {
	/**
	 * 强制隐藏输入法
	 * @param context
	 */
	public static void hideInputImm(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	public static void doInBackground(final Method method, final Object receiver, final Object... args){
		new AsyncTask<Void, Void, String[]>(){
			@Override
			protected String[] doInBackground(Void... params) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				return null;
			}
			@Override
			protected void onPostExecute(String[] result) {
				try {
					method.invoke(receiver, args);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
}
