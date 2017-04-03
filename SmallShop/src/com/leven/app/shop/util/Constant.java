package com.leven.app.shop.util;
/**
 * 常量数据定义
 * @author Felix Lee
 * @2015年12月19日 @下午8:41:22
 */
public class Constant {
	/**
	 * 默认列表显示记录数
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	/**
	 * APP属性设置Key值（列表显示记录数）
	 */
	public static final String PREF_KEY_PAGE_SIZE = "pref_key_page_size";
	/**
	 * APP属性设置Key值（到期检测数）
	 */
	public static final String PREF_KEY_OVERDUE_NUM = "pref_key_overdue_num";
	/**
	 * APP属性设置Key值（到期检测单位）
	 */
	public static final String PREF_KEY_OVERDUE_UNIT = "pref_key_overdue_unit";
	/**
	 * APP属性设置Key值（账单结清取消时间）
	 */
	public static final String PREF_KEY_CANCELBILLSTATE = "pref_key_cancelbillstate";
	/**
	 * APP属性设置Key值（数据导入）
	 */
	public static final String PREF_KEY_DATA_SYNC_IN = "pref_key_data_sync_in";
	/**
	 * APP属性设置Key值（数据导出）
	 */
	public static final String PREF_KEY_DATA_SYNC_OUT = "pref_key_data_sync_out";
	/**
	 * 账单状态---未结清
	 */
	public static final int BILLRECORD_STATE_DEBT = 0;
	/**
	 * 账单状态---已结清
	 */
	public static final int BILLRECORD_STATE_CLEAR = 1;
}
