package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AboutFeedbackActivity extends Activity {
	private Button mSubmitBtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_feedback);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        mSubmitBtn = (Button) findViewById(R.id.about_feedback_submit);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(AboutFeedbackActivity.this, 
						R.string.about_feedback_submit_success, Toast.LENGTH_LONG).show();
				finish();
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
