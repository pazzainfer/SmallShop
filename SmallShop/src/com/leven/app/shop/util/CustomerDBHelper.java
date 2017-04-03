package com.leven.app.shop.util;

import java.util.ArrayList;
import java.util.List;

import com.leven.app.shop.entity.Customer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.preference.PreferenceManager;
import android.util.Log;

public class CustomerDBHelper extends AbstractDBHelper {
	private static final String[] COLUMN_ARRAY = {"_id","_name","_address","_sex","_remarks"};

	public CustomerDBHelper(Context context, CursorFactory factory) {
		super(context, factory);
	}

	/**
	 * 新增客户信息至数据库
	 * @param customer
	 */
	public void insertCustomer(Customer customer){
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();  
		//调用insert方法，将数据插入数据库  
		db.insert("_shop_customer", null, getValuesFromData(customer));  
		//关闭数据库  
		db.close();
		Log.i(TAG, "insert new customer------------->");
	}

	/**
	 * 通过id好获取指定客户信息
	 * @param id
	 * @return
	 */
	public Customer getCustomerById(int id){
		Customer customer = new Customer();
		customer.setId(id);
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();  
		Cursor cursor = db.query("_shop_customer", COLUMN_ARRAY, "_id=?", new String[]{String.valueOf(id)}, null, null, null);  
		if(cursor.moveToNext()){
			customer = getDataFromCursor(cursor);
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find customer------------->");
		return customer;
	}
	/**
	 * 修改客户信息至数据库
	 * @param customer
	 */
	public void updateCustomer(Customer customer){
		SQLiteDatabase db = this.getWritableDatabase();
		
		//where 子句 "?"是占位符号，对应后面的"1",  
		String whereClause="_id=?";  
		String [] whereArgs = {String.valueOf(customer.getId())};  
		//参数1 是要更新的表名  
		//参数2 是一个ContentValeus对象  
		//参数3 是where子句  
		db.update("_shop_customer", getValuesFromData(customer), whereClause, whereArgs);
		db.close();
		Log.i(TAG, "update customer------------->");
	}
	
	/**
	 * 分页列出客户信息
	 * @param pageNo 分页号
	 * @return
	 */
	public List<Customer> findCustomerByPage(Context context, int pageNo){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<Customer> customerList = new ArrayList<Customer>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query("_shop_customer", COLUMN_ARRAY, null, null, null, null, null, ((pageNo-1)*pageSize) + "," + pageSize);
		while(cursor.moveToNext()){
			Customer customer = new Customer();
			customerList.add(getDataFromCursor(cursor));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find customer page------------->");
		return customerList;
	}
	/**
	 * 搜索分页查询
	 * @param context
	 * @param name 客户名
	 * @param pageNo
	 * @return
	 */
	public List<Customer> findCustomerByPage(Context context, String name, int pageNo){
		if(name==null || "".equals(name)){
			return findCustomerByPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<Customer> customerList = new ArrayList<Customer>();
		SQLiteDatabase db = this.getWritableDatabase();
		String sql= "select * from _shop_customer where _name like '%" + name + "%' Limit "+((pageNo-1)*pageSize)+ " , " + pageSize;
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			customerList.add(getDataFromCursor(cursor));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find customer page------------->");
		return customerList;
	}
	
	/**
	 * 返回是否最后一页
	 * @param context
	 * @param name
	 * @param pageNo
	 * @return
	 */
	public boolean isLastPage(Context context, String name, int pageNo){
		if(name==null || "".equals(name)){
			return isLastPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		SQLiteDatabase db = this.getWritableDatabase();
		int goodsCount = 0;
		StringBuffer whereArgs = new StringBuffer();
		whereArgs.append(" where _name like '%");
		whereArgs.append(name);
		whereArgs.append("%'");
		Cursor countCursor = db.rawQuery("select count(*) from "+ getTableName() + whereArgs, null);
		countCursor.moveToNext();
		goodsCount = countCursor.getInt(0);
		db.close();
		int remainder = goodsCount % pageSize;
		return remainder==0?(pageNo == (goodsCount / pageSize)):(pageNo == (goodsCount / pageSize + 1));
	}
	
	public String getTableName(){
		return "_shop_customer";
	}
	
	private ContentValues getValuesFromData(Customer customer){
		ContentValues cv = new ContentValues();
		cv.put("_id", customer.getId()); 
		cv.put("_name", customer.getName());  
		cv.put("_address", customer.getAddress());
		cv.put("_sex", customer.getSex());
		cv.put("_remarks", customer.getRemarks());
		return cv;
	}
	
	private Customer getDataFromCursor(Cursor cursor){
		Customer customer = new Customer();
		customer.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		customer.setName(cursor.getString(cursor.getColumnIndex("_name")));
		customer.setAddress(cursor.getString(cursor.getColumnIndex("_address")));
		customer.setSex(cursor.getString(cursor.getColumnIndex("_sex")));
		customer.setRemarks(cursor.getString(cursor.getColumnIndex("_remarks")));
		return customer;
	}
}
