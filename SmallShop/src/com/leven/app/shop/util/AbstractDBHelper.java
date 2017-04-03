package com.leven.app.shop.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class AbstractDBHelper extends SQLiteOpenHelper {
	protected static final String TAG = "SmallShopDB";
	public static final String DB_NAME = "small_shop_db";
	/**
	 * 数据库版本
	 */
	public static final int DB_VERSION = 1;

	public AbstractDBHelper(Context context, CursorFactory factory) {
		super(context, DB_NAME, factory, DB_VERSION);
	}

	/**
	 * 当第一次创建数据库的时候，调用该方法
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String goodsCreateSql = "create table _shop_goods(_id INTEGER PRIMARY KEY AUTOINCREMENT,_name varchar(100),_sell_price double,_cost_price double,_stock int,_unit varchar(10),_image BLOB,_bar_code varchar(40),_purchase_date INTEGER,_overdue_date INTEGER)";
		String customerCreateSql = "create table _shop_customer(_id INTEGER PRIMARY KEY AUTOINCREMENT,_name varchar(100),_address varchar(100),_sex varchar(2),_remarks varchar(100))";
		String billRecordSql = "create table _shop_bill_record(_id INTEGER PRIMARY KEY AUTOINCREMENT,_customer_id INTEGER,_customer_name varchar(100),_state INTEGER,_goods_num INTEGER,_goods_id INTEGER,_goods_name varchar(100),_goods_unit varchar(10),_goods_price double,_total double,_remarks varchar(100),_date INTEGER,_clear_date INTEGER)";
		//输出创建数据库的日志信息  
		Log.i(TAG, "create Database------------->");
		Log.i(TAG, goodsCreateSql);
		Log.i(TAG, customerCreateSql);
		Log.i(TAG, billRecordSql);
		db.beginTransaction();
		db.execSQL(goodsCreateSql);
		db.execSQL(customerCreateSql);
		db.execSQL(billRecordSql);
        db.setTransactionSuccessful();  
        db.endTransaction();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "update Database------------->");
	}
	
	/**
	 * 返回是否最后一页
	 * @param context
	 * @param pageNo
	 * @return
	 */
	public boolean isLastPage(Context context, int pageNo){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		SQLiteDatabase db = this.getWritableDatabase();
		int goodsCount = 0;
		Cursor countCursor = db.rawQuery("select count(*) from "+ getTableName(), null);
		countCursor.moveToNext();
		goodsCount = countCursor.getInt(0);
		db.close();
		int remainder = goodsCount % pageSize;
		return remainder==0?(pageNo == (goodsCount / pageSize)):(pageNo == (goodsCount / pageSize + 1));
	}
	

	/**
	 * 批量删除商品数据记录
	 * @param selectedIdList 所需删除的商品ID集合
	 */
	public void deleteBatch(List<Integer> selectedIdList) {
		if(selectedIdList!=null && !selectedIdList.isEmpty()){
			SQLiteDatabase db = this.getWritableDatabase();
			db.beginTransaction();
        	String whereClauses = "_id=?";
    		String[] whereArgs;
	        for (int i = 0; i < selectedIdList.size(); i++) {
	        	whereArgs = new String[]{String.valueOf(selectedIdList.get(i))};
	    		db.delete(getTableName(), whereClauses, whereArgs);
	        }
	        db.setTransactionSuccessful();  
	        db.endTransaction();
	        db.close();
		}
	}

	/**
	 * 从数据库中删除指定商品信息
	 * @param id
	 */
	public void delete(int id){
		String whereClauses = "_id=?";  
		String [] whereArgs = {String.valueOf(id)};  
		SQLiteDatabase db = this.getWritableDatabase();  
		db.delete(getTableName(), whereClauses, whereArgs);
		db.close();
		Log.i(TAG, "delete customer------------->");
	}
	/**
	 * 返回表名
	 * @return
	 */
	abstract String getTableName();
}
