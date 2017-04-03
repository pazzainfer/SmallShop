package com.leven.app.shop.activity;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.Customer;
import com.leven.app.shop.util.CustomerDBHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CustomerAddActivity extends Activity {
	public static final String TAG = "CustomerAddActivity";
	
	private EditText mName;
	private Spinner mSex;
	private EditText mAddress;
	private EditText mRemarks;
	
    private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_add);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		/* 取得Intent中的Bundle对象 */ 
	    intent=this.getIntent();
		
		mName = (EditText)findViewById(R.id.customer_add_name_input);
		mSex = (Spinner)findViewById(R.id.customer_add_sex_input);
		mAddress = (EditText)findViewById(R.id.customer_add_address_input);
		mRemarks = (EditText)findViewById(R.id.customer_add_remarks_input);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.customer_add, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_customer_finish) {
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
			Toast.makeText(this, getResources().getString(R.string.customer_toast_name_must), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mAddress.getText().length()==0){
			Toast.makeText(this, getResources().getString(R.string.customer_toast_address_must), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mSex.getSelectedItem().toString().length()==0){
			Toast.makeText(this, getResources().getString(R.string.customer_toast_sex_must), Toast.LENGTH_SHORT).show();
			return;
		}
		CustomerDBHelper customerDBHelper = new CustomerDBHelper(this, null);
		Customer customer = new Customer();
		customer.setName(mName.getText().toString());
		customer.setSex(mSex.getSelectedItem().toString());
		customer.setAddress(mAddress.getText().toString());
		customer.setRemarks(mRemarks.getText().toString());
		
		customerDBHelper.insertCustomer(customer);
		Toast.makeText(this, getResources().getString(R.string.customer_add_success), Toast.LENGTH_SHORT).show();
		
		this.setResult(RESULT_OK, intent);  
        this.finish();
	}
}
