package com.leven.app.shop.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.leven.app.shop.entity.BillRecord;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.preference.PreferenceManager;
import android.util.Log;

public class BillRecordDBHelper extends AbstractDBHelper {
	private static final String[] COLUMN_ARRAY = {"_id","_customer_id","_customer_name","_state","_goods_num","_goods_id","_goods_name","_goods_unit","_goods_price","_total","_remarks","_date","_clear_date"};

	public BillRecordDBHelper(Context context, CursorFactory factory) {
		super(context, factory);
	}

	/**
	 * 新增账单信息至数据库
	 * @param billRecord
	 */
	public void insertBillRecord(BillRecord billRecord){
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();
		billRecord.setState(Constant.BILLRECORD_STATE_DEBT);
		
		//调用insert方法，将数据插入数据库 
		db.insert("_shop_bill_record", null, getValuesFromData(billRecord));
		//关闭数据库  
		db.close();
		Log.i(TAG, "insert new billRecord------------->");
	}

	/**
	 * 通过id好获取指定账单信息
	 * @param id
	 * @return
	 */
	public BillRecord getBillRecordById(int id){
		BillRecord billRecord = new BillRecord();
		billRecord.setId(id);
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();  
		Cursor cursor = db.query("_shop_bill_record", COLUMN_ARRAY, "_id=?", new String[]{String.valueOf(id)}, null, null, null);  
		if(cursor.moveToNext()){
			billRecord = getDataFromCursor(cursor);
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find billRecord------------->");
		return billRecord;
	}
	/**
	 * 修改账单信息至数据库
	 * @param billRecord
	 */
	public void updateBillRecord(BillRecord billRecord){
		SQLiteDatabase db = this.getWritableDatabase();
		//where 子句 "?"是占位符号，对应后面的"1",  
		String whereClause="_id=?";  
		String [] whereArgs = {String.valueOf(billRecord.getId())};  
		//参数1 是要更新的表名  
		//参数2 是一个ContentValeus对象  
		//参数3 是where子句  
		db.update("_shop_bill_record", getValuesFromData(billRecord), whereClause, whereArgs);
		db.close();
		Log.i(TAG, "update billRecord------------->");
	}
	/**
	 * 变更账单状态
	 * @param id
	 * @param newState
	 */
	public void changeBillState(int id, int newState){
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause="_id=?";
		String [] whereArgs = {String.valueOf(id)};
		ContentValues cv = new ContentValues();
		cv.put("_state", newState);
		if(newState == Constant.BILLRECORD_STATE_CLEAR){
			cv.put("_clear_date", new Date().getTime());
		}
		db.update("_shop_bill_record", cv, whereClause, whereArgs);
		db.close();
		Log.i(TAG, "update billRecord state------------->");
	}
	
	/**
	 * 分页列出指定客户账单信息
	 * @param context
	 * @param customerId 账单编号
	 * @param pageNo 分页号
	 * @return
	 */
	public List<BillRecord> findBillRecordByCustForPage(Context context,int customerId, int pageNo){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<BillRecord> billRecordList = new ArrayList<BillRecord>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query("_shop_bill_record", COLUMN_ARRAY, "_customer_id=?", new String[]{String.valueOf(customerId)}
			, null, null, "_date desc", ((pageNo-1)*pageSize) + "," + pageSize);
		while(cursor.moveToNext()){
			billRecordList.add(getDataFromCursor(cursor));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find billRecord page------------->");
		return billRecordList;
	}
	
	public List<BillRecord> findBillRecordByPage(Context context, int pageNo){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<BillRecord> billRecordList = new ArrayList<BillRecord>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query("_shop_bill_record", COLUMN_ARRAY, null, null, null, null, "_date desc", ((pageNo-1)*pageSize) + "," + pageSize);
		while(cursor.moveToNext()){
			billRecordList.add(getDataFromCursor(cursor));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find billRecord page------------->");
		return billRecordList;
	}
	/**
	 * 搜索分页查询
	 * @param context
	 * @param name 账单名
	 * @param pageNo
	 * @return
	 */
	public List<BillRecord> findBillRecordByPage(Context context, String name, int pageNo){
		if(name==null || "".equals(name)){
			return findBillRecordByPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<BillRecord> billRecordList = new ArrayList<BillRecord>();
		SQLiteDatabase db = this.getWritableDatabase();
		String sql= "select * from _shop_bill_record where _customer_name like '%" + name + "%' order by _date desc Limit "+((pageNo-1)*pageSize)+ " , " + pageSize;
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			billRecordList.add(getDataFromCursor(cursor));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find billRecord page------------->");
		return billRecordList;
	}

	/**
	 * 返回是否最后一页
	 * @param context
	 * @param pageNo
	 * @return
	 */
	public boolean isLastPage(Context context,String name, int pageNo){
		if(name==null || "".equals(name)){
			return isLastPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		SQLiteDatabase db = this.getWritableDatabase();
		int goodsCount = 0;
		Cursor countCursor = db.rawQuery("select count(*) from "+ getTableName() + " where _customer_name like '%" + name + "%'", null);
		countCursor.moveToNext();
		goodsCount = countCursor.getInt(0);
		db.close();
		int remainder = goodsCount % pageSize;
		return remainder==0?(pageNo == (goodsCount / pageSize)):(pageNo == (goodsCount / pageSize + 1));
	}
	/**
	 * 从数据对象中抽取数据，放置到ContentValues（键值对形式）中返回
	 * @param object
	 * @return
	 */
	private ContentValues getValuesFromData(BillRecord billRecord){
		ContentValues cv = new ContentValues();
		//往ContentValues对象存放数据，键-值对模式 
		cv.put("_id", billRecord.getId());
		cv.put("_customer_id", billRecord.getCustomerId());
		cv.put("_customer_name", billRecord.getCustomerName());
		cv.put("_goods_num", billRecord.getGoodsNum());
		cv.put("_state", billRecord.getState());
		cv.put("_goods_id", billRecord.getGoodsId());
		cv.put("_goods_name", billRecord.getGoodsName());
		cv.put("_goods_unit", billRecord.getGoodsUnit());
		cv.put("_goods_price", billRecord.getGoodsPrice());
		cv.put("_total", billRecord.getTotal());
		cv.put("_remarks", billRecord.getRemarks());
		cv.put("_date", billRecord.getDate());
		cv.put("_clear_date", billRecord.getClearDate());
		return cv;
	}
	/**
	 * 从数据库游标中依次获取数据，拼装成数据对象返回
	 * @param cursor
	 * @return
	 */
	private BillRecord getDataFromCursor(Cursor cursor){
		BillRecord billRecord = new BillRecord();
		billRecord.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		billRecord.setCustomerId(cursor.getInt(cursor.getColumnIndex("_customer_id")));
		billRecord.setCustomerName(cursor.getString(cursor.getColumnIndex("_customer_name")));
		billRecord.setGoodsNum(cursor.getInt(cursor.getColumnIndex("_goods_num")));
		billRecord.setState(cursor.getInt(cursor.getColumnIndex("_state")));
		billRecord.setGoodsId(cursor.getInt(cursor.getColumnIndex("_goods_id")));
		billRecord.setGoodsName(cursor.getString(cursor.getColumnIndex("_goods_name")));
		billRecord.setGoodsUnit(cursor.getString(cursor.getColumnIndex("_goods_unit")));
		billRecord.setGoodsPrice(cursor.getDouble(cursor.getColumnIndex("_goods_price")));
		billRecord.setTotal(cursor.getDouble(cursor.getColumnIndex("_total")));
		billRecord.setRemarks(cursor.getString(cursor.getColumnIndex("_remarks")));
		billRecord.setDate(cursor.getLong(cursor.getColumnIndex("_date")));
		billRecord.setClearDate(cursor.getLong(cursor.getColumnIndex("_clear_date")));
		return billRecord;
	}
	
	public String getTableName(){
		return "_shop_bill_record";
	}
}
