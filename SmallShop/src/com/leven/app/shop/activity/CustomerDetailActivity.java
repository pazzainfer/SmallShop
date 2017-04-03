package com.leven.app.shop.activity;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.Customer;
import com.leven.app.shop.entity.CustomerParcelable;
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
import android.widget.TextView;
import android.widget.Toast;

public class CustomerDetailActivity extends Activity {
	public static final String TAG = "CustomerDetailActivity";
	
	private EditText mName;
	private Spinner mSex;
	private EditText mAddress;
	private EditText mRemarks;
	private TextView mId;

	private Customer mCustomer = new Customer();
	
    private Bundle bundle;  
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_detail);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
		
		/* 取得Intent中的Bundle对象 */ 
	    intent=this.getIntent();  
	    bundle = intent.getExtras(); 
	    
	    mName = (EditText)findViewById(R.id.customer_detail_name_input);
		mSex = (Spinner)findViewById(R.id.customer_detail_sex_input);
		mAddress = (EditText)findViewById(R.id.customer_detail_address_input);
		mRemarks = (EditText)findViewById(R.id.customer_detail_remarks_input);
		mId = (TextView)findViewById(R.id.customer_detail_id);
		
		int _id = bundle.getInt("_id");
		mId.setText(String.valueOf(_id));
		CustomerDBHelper customerDBHelper = new CustomerDBHelper(this, null);
		mCustomer = customerDBHelper.getCustomerById(_id);
        mName.setText(mCustomer.getName());
        mAddress.setText(mCustomer.getAddress());
        mRemarks.setText(mCustomer.getRemarks());
        
        if(mCustomer.getSex()!=null && !"".equals(mCustomer.getSex())){
        String[] mUnitArray = getResources().getStringArray(R.array.array_sex);
	        for(int i=0;i<mUnitArray.length;i++){
	        	if(mCustomer.getSex().equals(mUnitArray[i])){
	        		mSex.setSelection(i , true);
	        		break;
	        	}
	        }
        }
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.customer_detail, menu);
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
		mCustomer.setName(mName.getText().toString());
		mCustomer.setSex(mSex.getSelectedItem().toString());
		mCustomer.setAddress(mAddress.getText().toString());
		mCustomer.setRemarks(mRemarks.getText().toString());
		
		customerDBHelper.updateCustomer(mCustomer);
		Toast.makeText(this, getResources().getString(R.string.customer_update_success), Toast.LENGTH_SHORT).show();
		
		intent.putExtra("_customer", new CustomerParcelable(mCustomer));
		intent.putExtra("_position", bundle.getInt("_position"));
		this.setResult(RESULT_OK, intent);  
        this.finish();
	}
}
