package com.leven.app.shop.activity;

import com.leven.app.shop.R;
import com.leven.app.shop.widget.CustomPerfView;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CustomSettingsActivity extends Activity {
	private CustomPerfView mPageSizePref;
	private Button mSendBtn;
	private Button mReceiveBtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);

        mSendBtn = (Button)findViewById(R.id.pref_sync_out_start_send);
        mReceiveBtn = (Button)findViewById(R.id.pref_sync_in_start_receive);
        mSendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
        mReceiveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
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
