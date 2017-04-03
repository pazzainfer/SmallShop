package com.leven.app.shop.util;

import java.util.Date;

import android.text.format.DateFormat;

public class DateUtil {
	public static final String DATE_FORMAT = "yyyy/MM/dd";
	/**
	 * 返回当前日期时间格式
	 * @return
	 */
	public static String getCurrentDateStr(){
		return DateFormat.format(DATE_FORMAT, new Date()).toString();
	}
	/**
	 * 将日期毫秒值转换为日期格式
	 * @param l
	 * @return
	 */
	public static String transLongToDate(Long l){
		if(l==null || l==0){
			return "";
		}
		return DateFormat.format(DATE_FORMAT, new Date(l)).toString();
	}
}
