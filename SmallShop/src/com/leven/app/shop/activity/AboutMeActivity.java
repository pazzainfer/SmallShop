package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AboutMeActivity extends Activity implements OnClickListener {
	
	private View mPhoneView;
	private TextView mPhoneText;
	private View mMailView;
	private TextView mMailText;
	private View mMsgView;
	private TextView mMsgText;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_me);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        
        mPhoneView = findViewById(R.id.about_me_phone_item);
        mPhoneText = (TextView)findViewById(R.id.about_me_phone_text);
        mMailView = findViewById(R.id.about_me_mail_item);
        mMailText = (TextView)findViewById(R.id.about_me_mail_text);
        mMsgView = findViewById(R.id.about_me_msg_item);
        mMsgText = (TextView)findViewById(R.id.about_me_msg_text);
        
        mPhoneView.setOnClickListener(this);
        mMailView.setOnClickListener(this);
        mMsgView.setOnClickListener(this);
        mPhoneView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ClipboardManager cmb = (ClipboardManager) AboutMeActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText("data", mPhoneText.getText().toString()));
				Toast.makeText(AboutMeActivity.this, R.string.copy_to_clipboard, Toast.LENGTH_LONG).show();
				return true;
			}
		});
        mMailView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ClipboardManager cmb = (ClipboardManager) AboutMeActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText("data", mMailText.getText().toString()));
				Toast.makeText(AboutMeActivity.this, R.string.copy_to_clipboard, Toast.LENGTH_LONG).show();
				return true;
			}
		});
        mMsgView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ClipboardManager cmb = (ClipboardManager) AboutMeActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText("data", mMsgText.getText().toString()));
				Toast.makeText(AboutMeActivity.this, R.string.copy_to_clipboard, Toast.LENGTH_LONG).show();
				return true;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.about_me_phone_item:{
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + mPhoneText.getText()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			}
			case R.id.about_me_mail_item:{
				String[] reciver = new String[] {mMailText.getText().toString()};  
		        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);  
		        myIntent.setType("plain/text");  
		        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);  
		        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");  
		        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");  
		        startActivity(Intent.createChooser(myIntent, getResources().getString(R.string.about_me_mail_selecct))); 
				break;
			}
			case R.id.about_me_msg_item:{
				Intent intent = new Intent();
				ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(cmp);
				startActivity(intent);
				break;
			}
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {  
        case android.R.id.home:  
            this.finish();
            break;  
        }  
        return super.onOptionsItemSelected(item);
    }
}
