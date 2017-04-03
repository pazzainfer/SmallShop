package com.leven.app.shop.activity;

import java.util.Calendar;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.BillRecord;
import com.leven.app.shop.entity.Customer;
import com.leven.app.shop.entity.Goods;
import com.leven.app.shop.util.BillRecordDBHelper;
import com.leven.app.shop.util.DateUtil;
import com.leven.app.shop.widget.CustomerSelectDialog;
import com.leven.app.shop.widget.CustomerSelectDialog.OnCustomerDialogListener;
import com.leven.app.shop.widget.GoodsSelectDialog;
import com.leven.app.shop.widget.GoodsSelectDialog.OnGoodsDialogListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BillRecordAddActivity extends Activity {
	public static final String TAG = "BillRecordAddActivity";
	public static final int FROM_CUSTOMER = 1;
	public static final int FROM_GOODS = 2;
	
	private LinearLayout mCustLayout;
	private LinearLayout mGoodsLayout;
	private TextView mCustName;
	private TextView mCustId;
	private TextView mGoodsName;
	private TextView mGoodsPrice;
	private TextView mGoodsId;
	private EditText mGoodsNum;
	private TextView mGoodsUnit;
	private EditText mTotal;
	private EditText mRemarks;
	private EditText mDate;
	private ImageView mCalendar;

	private Calendar c = Calendar.getInstance();
	
    private Intent intent;
    private CustomerSelectDialog mCustomerSelectDialog;
    private GoodsSelectDialog mGoodsSelectDialog;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_billrecord_add);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		/* 取得Intent中的Bundle对象 */ 
	    intent = this.getIntent();
	    Bundle bundle = intent.getExtras();
	    int _from = bundle==null?0:bundle.getInt("_from");
	    
		mCustLayout = (LinearLayout)findViewById(R.id.billrecord_add_cust_layout);
		mGoodsLayout = (LinearLayout)findViewById(R.id.billrecord_add_goods_layout);
		mCustName = (TextView)findViewById(R.id.billrecord_add_custname_input);
		mGoodsName = (TextView)findViewById(R.id.billrecord_add_goodsname_input);
		mGoodsNum = (EditText)findViewById(R.id.billrecord_add_goodsnum_input);
		mTotal = (EditText)findViewById(R.id.billrecord_add_total_input);
		mDate = (EditText)findViewById(R.id.billrecord_add_date_input);
		mRemarks = (EditText)findViewById(R.id.billrecord_add_remarks_input);
		mCustId = (TextView)findViewById(R.id.billrecord_add_custid);
		mGoodsPrice = (TextView)findViewById(R.id.billrecord_add_goodsprice);
		mGoodsId = (TextView)findViewById(R.id.billrecord_add_goodsid);
		mGoodsUnit = (TextView)findViewById(R.id.billrecord_add_goodsunit);
		mCalendar = (ImageView)findViewById(R.id.billrecord_add_date_calendar);
		mDate.setText(DateUtil.getCurrentDateStr());
		
		if(bundle==null || _from != FROM_CUSTOMER){
			mCustomerSelectDialog = new CustomerSelectDialog(this, new OnCustomerDialogListener() {
				@Override
				public void back() {
					Customer _customer = mCustomerSelectDialog.getCustomer();
					mCustName.setText(_customer.getName());
					mCustId.setText(String.valueOf(_customer.getId()));
				}
			});
			mCustLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCustomerSelectDialog.show();
				}
			});
		} else {
			mCustName.setText(bundle.getString("cust_name"));
			mCustId.setText(String.valueOf(bundle.getInt("cust_id")));
		}
		if(bundle==null || _from != FROM_GOODS){
			mGoodsSelectDialog = new GoodsSelectDialog(this, new OnGoodsDialogListener() {
				@Override
				public void back() {
					Goods _goods = mGoodsSelectDialog.getGoods();
					mGoodsName.setText(_goods.getName());
					mGoodsUnit.setText(_goods.getUnit());
					mGoodsPrice.setText(String.valueOf(_goods.getSellPrice()));
					mGoodsId.setText(String.valueOf(_goods.getId()));
					resetTotal();
				}
			});
			mGoodsLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mGoodsSelectDialog.show();
				}
			});
		} else {
			mGoodsName.setText(bundle.getString("goods_name"));
			mGoodsUnit.setText(bundle.getString("goods_unit"));
			mGoodsPrice.setText(String.valueOf(bundle.getDouble("goods_price")));
			mGoodsId.setText(String.valueOf(bundle.getInt("goods_id")));
		}
		mGoodsNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				resetTotal();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		mCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(BillRecordAddActivity.this, new DatePickerDialog.OnDateSetListener() {  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    	c.set(year, monthOfYear, dayOfMonth);
                        mDate.setText(new StringBuilder().append(year).append("/").append(monthOfYear+1)
                        		.append("/").append(dayOfMonth).append(""));  
                    }  

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.billrecord_add, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_billrecord_finish) {
			doFinish();
		}else if(id == android.R.id.home){
			this.setResult(RESULT_CANCELED, intent);  
	        this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 重新计算总价款
	 */
	private void resetTotal(){
		try{
			double _price = Double.parseDouble(mGoodsPrice.getText().toString());
			int _num = Integer.parseInt(mGoodsNum.getText().toString());
			mTotal.setText(String.format("%.1f",_price * _num));
		}catch (Exception e){
			return;
		}
	}
	/**
	 * 完成时保存数据至数据库
	 */
	private void doFinish(){
		if(mCustName.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.billrecord_cust_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mGoodsName.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.billrecord_goods_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mGoodsNum.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.billrecord_goodsnum_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mTotal.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.billrecord_total_empty), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mDate.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.billrecord_date_empty), Toast.LENGTH_SHORT).show();
			return;
		} else {
			Calendar _nowDate = Calendar.getInstance();
			if(c.after(_nowDate)){
				Toast.makeText(this, getResources().getString(R.string.billrecord_date_diable), Toast.LENGTH_LONG).show();
				return;
			}
		}
		BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(this, null);
		BillRecord billRecord = new BillRecord();
		billRecord.setCustomerName(mCustName.getText().toString());
		billRecord.setCustomerId(Integer.parseInt(mCustId.getText().toString()));
		billRecord.setGoodsId(Integer.parseInt(mGoodsId.getText().toString()));
		billRecord.setGoodsName(mGoodsName.getText().toString());
		billRecord.setGoodsUnit(mGoodsUnit.getText().toString());
		billRecord.setGoodsPrice(Double.parseDouble(mGoodsPrice.getText().toString()));
		billRecord.setGoodsNum(Integer.parseInt(mGoodsNum.getText().toString()));
		billRecord.setTotal(Double.parseDouble(mTotal.getText().toString()));
		billRecord.setDate(c.getTimeInMillis());
		billRecord.setRemarks(mRemarks.getText().toString());
		
		billRecordDBHelper.insertBillRecord(billRecord);
		Toast.makeText(this, getResources().getString(R.string.billrecord_add_success), Toast.LENGTH_SHORT).show();
		
		this.setResult(RESULT_OK, intent);  
        this.finish();
	}
}
