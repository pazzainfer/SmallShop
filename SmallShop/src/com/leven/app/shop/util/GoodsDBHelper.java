package com.leven.app.shop.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.Goods;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap.CompressFormat;
import android.preference.PreferenceManager;
import android.graphics.BitmapFactory;
import android.util.Log;

public class GoodsDBHelper extends AbstractDBHelper {
	private static final String TAG = "SmallShopDB";
	public static final String DB_NAME = "small_shop_db";
	private static final String[] COLUMN_ARRAY = {"_id","_name","_sell_price","_cost_price","_stock","_unit","_image","_bar_code","_purchase_date","_overdue_date"};

	public GoodsDBHelper(Context context, CursorFactory factory) {
		super(context, factory);
	}

	/**
	 * 新增商品信息至数据库
	 * @param goods
	 */
	public void insertGoods(Goods goods){
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();  
		  
		//生成ContentValues对象 //key:列名，value:想插入的值   
		ContentValues cv = new ContentValues();
		//往ContentValues对象存放数据，键-值对模式  
		cv.put("_name", goods.getName());  
		cv.put("_sell_price", goods.getSellPrice());
		cv.put("_cost_price", goods.getCostPrice());
		cv.put("_stock", goods.getStock());
		cv.put("_unit", goods.getUnit());
		if(goods.getImage()!=null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			goods.getImage().compress(CompressFormat.PNG, 100, baos);
			cv.put("_image", baos.toByteArray());
		}
		cv.put("_bar_code", goods.getBarCode());
		cv.put("_purchase_date", goods.getPurchaseDate());
		cv.put("_overdue_date", goods.getOverdueDate());
		//调用insert方法，将数据插入数据库  
		db.insert("_shop_goods", null, cv);  
		//关闭数据库  
		db.close();
		Log.i(TAG, "insert new goods------------->");
	}

	/**
	 * 通过id好获取指定商品信息
	 * @param id
	 * @return
	 */
	public Goods getGoodsById(int id){
		Goods goods = new Goods();
		goods.setId(id);
		//得到一个可写的数据库  
		SQLiteDatabase db = this.getWritableDatabase();  
		Cursor cursor = db.query("_shop_goods", COLUMN_ARRAY, "_id=?", new String[]{String.valueOf(id)}, null, null, null);  
		if(cursor.moveToNext()){
			goods.setName(cursor.getString(cursor.getColumnIndex("_name")));
			goods.setSellPrice(cursor.getDouble(cursor.getColumnIndex("_sell_price")));
			goods.setCostPrice(cursor.getDouble(cursor.getColumnIndex("_cost_price")));
			goods.setStock(cursor.getInt(cursor.getColumnIndex("_stock")));
			goods.setUnit(cursor.getString(cursor.getColumnIndex("_unit")));
			byte[] imgByteArray = cursor.getBlob(cursor.getColumnIndex("_image"));
			if(imgByteArray!=null && imgByteArray.length > 0){
				goods.setImage(BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length));
			}
			goods.setBarCode(cursor.getString(cursor.getColumnIndex("_bar_code")));
			goods.setPurchaseDate(cursor.getLong(cursor.getColumnIndex("_purchase_date")));
			goods.setOverdueDate(cursor.getLong(cursor.getColumnIndex("_overdue_date")));
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find goods------------->");
		return goods;
	}
	/**
	 * 修改商品信息至数据库
	 * @param goods
	 */
	public void updateGoods(Goods goods){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();  
		cv.put("_name", goods.getName());  
		cv.put("_sell_price", goods.getSellPrice());
		cv.put("_cost_price", goods.getCostPrice());
		cv.put("_stock", goods.getStock());
		cv.put("_unit", goods.getUnit());
		if(goods.getImage()!=null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			goods.getImage().compress(CompressFormat.PNG, 100, baos);
			cv.put("_image", baos.toByteArray());
		}
		cv.put("_bar_code", goods.getBarCode());
		cv.put("_purchase_date", goods.getPurchaseDate());
		cv.put("_overdue_date", goods.getOverdueDate());
		//where 子句 "?"是占位符号，对应后面的"1",  
		String whereClause="_id=?";  
		String [] whereArgs = {String.valueOf(goods.getId())};  
		//参数1 是要更新的表名  
		//参数2 是一个ContentValeus对象  
		//参数3 是where子句  
		db.update("_shop_goods", cv, whereClause, whereArgs);
		db.close();
		Log.i(TAG, "update goods------------->");
	}
	
	/**
	 * 分页列出商品信息
	 * @param pageNo 分页号
	 * @return
	 */
	public List<Goods> findGoodsByPage(Context context, int pageNo){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<Goods> goodsList = new ArrayList<Goods>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query("_shop_goods", COLUMN_ARRAY, null, null, null, null, null, ((pageNo-1)*pageSize) + "," + pageSize);
		while(cursor.moveToNext()){
			Goods goods = new Goods();
			goods.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			goods.setName(cursor.getString(cursor.getColumnIndex("_name")));
			goods.setSellPrice(cursor.getDouble(cursor.getColumnIndex("_sell_price")));
			goods.setCostPrice(cursor.getDouble(cursor.getColumnIndex("_cost_price")));
			goods.setStock(cursor.getInt(cursor.getColumnIndex("_stock")));
			goods.setUnit(cursor.getString(cursor.getColumnIndex("_unit")));
			byte[] imgByteArray = cursor.getBlob(cursor.getColumnIndex("_image"));
			if(imgByteArray!=null && imgByteArray.length > 0){
				goods.setImage(BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length));
			}
			goods.setBarCode(cursor.getString(cursor.getColumnIndex("_bar_code")));
			goods.setPurchaseDate(cursor.getLong(cursor.getColumnIndex("_purchase_date")));
			goods.setOverdueDate(cursor.getLong(cursor.getColumnIndex("_overdue_date")));
			goodsList.add(goods);
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find goods page------------->");
		return goodsList;
	}
	/**
	 * 搜索分页查询
	 * @param name 商品名称
	 * @param selectArg (barCode 条形码)或者(pageNo 分页号)的查询参数
	 * @return
	 */
	public List<Goods> findGoodsByPage(Context context, String selectArg, int pageNo){
		if(selectArg==null || "".equals(selectArg)){
			return findGoodsByPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		List<Goods> goodsList = new ArrayList<Goods>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		String sql= "select * from _shop_goods where _bar_code='" + selectArg + "' or _name like '%" + selectArg + "%' Limit "+((pageNo-1)*pageSize)+ " , " + pageSize;
		cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			Goods goods = new Goods();
			goods.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			goods.setName(cursor.getString(cursor.getColumnIndex("_name")));
			goods.setSellPrice(cursor.getDouble(cursor.getColumnIndex("_sell_price")));
			goods.setCostPrice(cursor.getDouble(cursor.getColumnIndex("_cost_price")));
			goods.setStock(cursor.getInt(cursor.getColumnIndex("_stock")));
			goods.setUnit(cursor.getString(cursor.getColumnIndex("_unit")));
			byte[] imgByteArray = cursor.getBlob(cursor.getColumnIndex("_image"));
			if(imgByteArray!=null && imgByteArray.length > 0){
				goods.setImage(BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length));
			}
			goods.setBarCode(cursor.getString(cursor.getColumnIndex("_bar_code")));
			goods.setPurchaseDate(cursor.getLong(cursor.getColumnIndex("_purchase_date")));
			goods.setOverdueDate(cursor.getLong(cursor.getColumnIndex("_overdue_date")));
			goodsList.add(goods);
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find goods page------------->");
		return goodsList;
	}

	public List<Goods> findOverdueByPage(Context context, int pageNo) {
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		int overdueNum = mPerferences.getInt(Constant.PREF_KEY_OVERDUE_NUM, 1);
		String overdueUnit = mPerferences.getString(Constant.PREF_KEY_OVERDUE_UNIT, 
				context.getResources().getString(R.string.date_unit_month));
		//到期检测时间（毫秒）
		long overdueMiles;
		if(context.getResources().getString(R.string.date_unit_week).equals(overdueUnit)){
			overdueMiles = overdueNum * 1000l * 60l * 60l * 24l * 7l;
		}else if(context.getResources().getString(R.string.date_unit_month).equals(overdueUnit)){
			overdueMiles = overdueNum * 1000l * 60l * 60l * 24l * 30l;
		}else if(context.getResources().getString(R.string.date_unit_year).equals(overdueUnit)){
			overdueMiles = overdueNum * 1000l * 60l * 60l * 24l * 365l;
		}else{
			overdueMiles = overdueNum * 1000l * 60l * 60l * 24l;
		}
		
		long selectArg = new Date().getTime() + overdueMiles;
		List<Goods> goodsList = new ArrayList<Goods>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		String sql= "select * from _shop_goods where _overdue_date is not null and _overdue_date<>0 and _overdue_date <=" + selectArg + " Limit "+((pageNo-1)*pageSize)+ " , " + pageSize;
		cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			Goods goods = new Goods();
			goods.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			goods.setName(cursor.getString(cursor.getColumnIndex("_name")));
			goods.setSellPrice(cursor.getDouble(cursor.getColumnIndex("_sell_price")));
			goods.setCostPrice(cursor.getDouble(cursor.getColumnIndex("_cost_price")));
			goods.setStock(cursor.getInt(cursor.getColumnIndex("_stock")));
			goods.setUnit(cursor.getString(cursor.getColumnIndex("_unit")));
			byte[] imgByteArray = cursor.getBlob(cursor.getColumnIndex("_image"));
			if(imgByteArray!=null && imgByteArray.length > 0){
				goods.setImage(BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length));
			}
			goods.setBarCode(cursor.getString(cursor.getColumnIndex("_bar_code")));
			goods.setPurchaseDate(cursor.getLong(cursor.getColumnIndex("_purchase_date")));
			goods.setOverdueDate(cursor.getLong(cursor.getColumnIndex("_overdue_date")));
			goodsList.add(goods);
		}
		//关闭数据库  
		db.close();
		Log.i(TAG, "find goods page------------->");
		return goodsList;
	}
	
	/**
	 * 返回是否最后一页
	 * @param context
	 * @param pageNo
	 * @return
	 */
	public boolean isLastPage(Context context,String selectArg, int pageNo){
		if(selectArg==null || "".equals(selectArg)){
			return isLastPage(context, pageNo);
		}
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		int pageSize = mPerferences.getInt(Constant.PREF_KEY_PAGE_SIZE,Constant.DEFAULT_PAGE_SIZE);
		SQLiteDatabase db = this.getWritableDatabase();
		int goodsCount = 0;
		Cursor countCursor = db.rawQuery("select count(*) from "+ getTableName() + 
				" where _bar_code='" + selectArg + "' or _name like '%" + selectArg + "%'", null);
		countCursor.moveToNext();
		goodsCount = countCursor.getInt(0);
		db.close();
		int remainder = goodsCount % pageSize;
		return remainder==0?(pageNo == (goodsCount / pageSize)):(pageNo == (goodsCount / pageSize + 1));
	}
	
	public String getTableName(){
		return "_shop_goods";
	}
}
