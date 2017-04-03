package com.leven.app.shop.activity;

import java.util.Calendar;

import com.google.zxing.client.android.CaptureActivity;
import com.leven.app.shop.R;
import com.leven.app.shop.crop.BitmapUtil;
import com.leven.app.shop.crop.CropHandler;
import com.leven.app.shop.crop.CropHelper;
import com.leven.app.shop.crop.CropParams;
import com.leven.app.shop.entity.Goods;
import com.leven.app.shop.entity.GoodsParcelable;
import com.leven.app.shop.util.DateUtil;
import com.leven.app.shop.util.GoodsDBHelper;
import com.leven.app.shop.util.UIUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsDetailActivity extends Activity implements CropHandler {
	public static final String TAG = "GoodsDetailActivity";
	
	private ImageView mImage;
	private TextView mId;
	private EditText mName;
	private EditText mPrice;
	private EditText mCostPrice;
	private Spinner mUnit;
	private EditText mStock;
	private EditText mBarCode;
	private ImageView mBarCodeScan;
	private EditText mDate;
	private ImageView mDateImage;
	private EditText mOverdue;
	private ImageView mOverdueImage;

	private Goods mGoods = new Goods();
	
	private Calendar c;
	private Calendar overdueCalendar;
	private int mYear,mMonth,mDay;
	private int mOverdueYear,mOverdueMonth,mOverdueDay;

    private CropParams mCropParams;
    
    private Bundle bundle;  
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_detail);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		/* 取得Intent中的Bundle对象 */ 
	    intent=this.getIntent();  
	    bundle = intent.getExtras(); 
	    
		mCropParams = new CropParams(this);
		mImage = (ImageView)findViewById(R.id.goods_detail_pic_btn);
		mId = (TextView)findViewById(R.id.goods_detail_id);
		mName = (EditText)findViewById(R.id.goods_detail_name_input);
		mPrice = (EditText)findViewById(R.id.goods_detail_price_input);
		mCostPrice = (EditText)findViewById(R.id.goods_detail_costprice_input);
		mUnit = (Spinner)findViewById(R.id.goods_detail_unit_input);
		mStock = (EditText)findViewById(R.id.goods_detail_stock_input);
		mBarCode = (EditText)findViewById(R.id.goods_detail_barcode_input);
		mBarCodeScan = (ImageView)findViewById(R.id.goods_detail_barcode_camera);
		mDate = (EditText)findViewById(R.id.goods_detail_date_input);
		mDateImage = (ImageView)findViewById(R.id.goods_detail_date_calendar);
		mOverdue = (EditText)findViewById(R.id.goods_detail_overdue_input);
		mOverdueImage = (ImageView)findViewById(R.id.goods_detail_overdue_calendar);
		mDate.setKeyListener(null);
		mOverdue.setKeyListener(null);
		
		int _id = bundle.getInt("_id");
		mId.setText(String.valueOf(_id));
		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(this, null);
		mGoods = goodsDBHelper.getGoodsById(_id);
        mImage.setImageBitmap(mGoods.getImage());
        mName.setText(mGoods.getName());
        mBarCode.setText(mGoods.getBarCode());
        mPrice.setText(String.valueOf(mGoods.getSellPrice()));
        mCostPrice.setText(String.valueOf(mGoods.getCostPrice()));
        
        if(mGoods.getUnit()!=null && !"".equals(mGoods.getUnit())){
        String[] mUnitArray = getResources().getStringArray(R.array.array_unit);
	        for(int i=0;i<mUnitArray.length;i++){
	        	if(mGoods.getUnit().equals(mUnitArray[i])){
	        		mUnit.setSelection(i , true);
	        		break;
	        	}
	        }
        }
        
        mStock.setText(String.valueOf(mGoods.getStock()));
        mDate.setText(DateUtil.transLongToDate(mGoods.getPurchaseDate()));
        mOverdue.setText(DateUtil.transLongToDate(mGoods.getOverdueDate()));
		
		mImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera();
			}
		});
		mBarCodeScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doBarCodeScan();
			}
		});
		c = Calendar.getInstance();  
        if(mGoods.getPurchaseDate()!=null && mGoods.getPurchaseDate()!=0){
        	c.setTimeInMillis(mGoods.getPurchaseDate());
            mYear = c.get(Calendar.YEAR); // 获取当前年份  
            mMonth = c.get(Calendar.MONTH);// 获取当前月份   
            mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        }
        
        overdueCalendar = Calendar.getInstance();
        if(mGoods.getOverdueDate()!=null && mGoods.getOverdueDate()!=0){
        	overdueCalendar.setTimeInMillis(mGoods.getOverdueDate());
            mOverdueYear = overdueCalendar.get(Calendar.YEAR); // 获取当前年份  
            mOverdueMonth = overdueCalendar.get(Calendar.MONTH);// 获取当前月份   
            mOverdueDay = overdueCalendar.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        }

        mDateImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(GoodsDetailActivity.this, new DatePickerDialog.OnDateSetListener() {  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    	mYear = year;
                    	mMonth = monthOfYear;
                    	mDay = dayOfMonth;
                    	c.set(mYear, mMonth, mDay);
                        mDate.setText(new StringBuilder().append(year).append("/").append(mMonth+1).append("/").append(dayOfMonth).append(""));  
                    }  

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
        mOverdueImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(GoodsDetailActivity.this, new DatePickerDialog.OnDateSetListener() {  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    	mOverdueYear = year;
                    	mOverdueMonth = monthOfYear;
                    	mOverdueDay = dayOfMonth;
                    	overdueCalendar.set(mOverdueYear, mOverdueMonth, mOverdueDay);
                        mOverdue.setText(new StringBuilder().append(mOverdueYear).append("/").append(mOverdueMonth).append("/").append(mOverdueDay).append(""));  
                    }  

                }, overdueCalendar.get(Calendar.YEAR), overdueCalendar.get(Calendar.MONTH), overdueCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
    }

    /**
	 * 打开相机扫描条码功能
	 */
	protected void doBarCodeScan() {
		try{
			Intent openCameraIntent = new Intent(this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Activity.RESULT_FIRST_USER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.goods_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_finish) {
			doFinish();
		}else if(id == android.R.id.home){
			this.setResult(RESULT_CANCELED, intent);  
	        this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 完成时保存数据至数据库
	 */
	private void doFinish(){
		if(mName.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.goods_toast_name_must), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mPrice.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.goods_toast_price_must), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mUnit.getSelectedItem().toString().length()==0){
			Toast.makeText(this, getResources().getString(R.string.goods_toast_unit_must), Toast.LENGTH_SHORT).show();
			return;
		}
		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(this, null);
		mGoods.setName(mName.getText().toString());
		if(mImage.getDrawable()!=null){
			mGoods.setImage(((BitmapDrawable)mImage.getDrawable()).getBitmap());
		}
		if(!"".equals(mPrice.getText().toString())){
			mGoods.setSellPrice(Double.parseDouble(mPrice.getText().toString()));
		}
		if(!"".equals(mCostPrice.getText().toString())){
			mGoods.setCostPrice(Double.parseDouble(mCostPrice.getText().toString()));
		}
		mGoods.setUnit(mUnit.getSelectedItem().toString());
		if(!"".equals(mStock.getText().toString())){
			mGoods.setStock(Integer.parseInt(mStock.getText().toString()));
		}
		mGoods.setBarCode(mBarCode.getText().toString());
		if(mDate.getText().length()>0){
			mGoods.setPurchaseDate(c.getTimeInMillis());
		}else{
			mGoods.setPurchaseDate(0l);
		}
		if(mOverdue.getText().length()>0){
			mGoods.setOverdueDate(overdueCalendar.getTimeInMillis());
		}else{
			mGoods.setOverdueDate(0l);
		}
		
		goodsDBHelper.updateGoods(mGoods);
		Toast.makeText(this, getResources().getString(R.string.goods_update_success), Toast.LENGTH_SHORT).show();
		
		intent.putExtra("_goods", new GoodsParcelable(mGoods));
		intent.putExtra("_position", bundle.getInt("_position"));
		this.setResult(RESULT_OK, intent);  
        this.finish();
	}
	/**
	 * 拍照获取图片
	 */
	private void camera(){
		new AlertDialog.Builder(this)
	        .setTitle(getResources().getString(R.string.goods_pic_dia_title))
	        .setPositiveButton(getResources().getString(R.string.goods_pic_dia_camera), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	mCropParams.enable = true;
	                mCropParams.compress = false;
	                Intent intent = CropHelper.buildCameraIntent(mCropParams);
	                startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
	            }
	        })
	        .setNegativeButton(getResources().getString(R.string.goods_pic_dia_pick), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	mCropParams.enable = true;
	                mCropParams.compress = false;
	                Intent intent = CropHelper.buildGalleryIntent(mCropParams);
	                startActivityForResult(intent, CropHelper.REQUEST_CROP);
	            }
	        }).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try{
        if(resultCode != Activity.RESULT_OK)
            return;
        switch(requestCode){
	        //条形码扫描结果
	        case Activity.RESULT_FIRST_USER:{
	        	Bundle bundle = data.getExtras();
	            String scanResult = bundle.getString("result");
	            mBarCode.setText(scanResult);
	            UIUtil.hideInputImm(this);
	            break;
	        }
            default:{
            	CropHelper.handleResult(this, requestCode, resultCode, data);
            }
        }

		}catch (Exception e){
			e.printStackTrace();
		}
    }

	@Override
	public void onPhotoCropped(Uri uri) {
		Log.d(TAG, "Crop Uri in path: " + uri.getPath());
		if (!mCropParams.compress){
            mImage.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));
		}
	}

	@Override
	public void onCompressed(Uri uri) {
		mImage.setImageBitmap(BitmapUtil.decodeUriAsBitmap(this, uri));
	}

	@Override
	public void onCancel() {
		Toast.makeText(this, "Crop canceled!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onFailed(String message) {
		Toast.makeText(this, "Crop failed: " + message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void handleIntent(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	@Override
	public CropParams getCropParams() {
		return mCropParams;
	}
}
