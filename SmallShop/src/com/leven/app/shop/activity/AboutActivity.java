package com.leven.app.shop.activity;

import java.io.IOException;

import com.leven.app.shop.R;
import com.leven.app.shop.util.FileUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AboutActivity extends Activity implements OnClickListener {
	private static final int REQ_ABOUT_ME = 0;
	private static final int REQ_ABOUT_FEEDBACK = 1;
	private View mAboutShare;
	private View mAboutFeedback;
	private View mAboutVersion;
	private View mAboutMe;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		ActionBar actionBar = this.getActionBar();    
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        mAboutShare = findViewById(R.id.about_share_item);
        mAboutFeedback = findViewById(R.id.about_feedback_item);
        mAboutVersion = findViewById(R.id.about_version_item);
        mAboutMe = findViewById(R.id.about_me_item);
        
        mAboutShare.setOnClickListener(this);
        mAboutFeedback.setOnClickListener(this);
        mAboutVersion.setOnClickListener(this);
        mAboutMe.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.about_share_item:{
				Intent intent=new Intent(Intent.ACTION_SEND); 
				intent.setType("image/*"); 
				intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.about_item_share_title));
				intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
				
				try {
					FileUtils.savaDrawable(this, "ic_share_app.png", R.drawable.ic_share_app);
					Uri uri = Uri.fromFile(FileUtils.getSavedFile(this, "ic_share_app.png"));
					intent.putExtra(Intent.EXTRA_STREAM, uri);
				} catch (IOException e) {
					e.printStackTrace();
				}
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(Intent.createChooser(intent, getResources().getString(R.string.about_item_share_title)));
				break;
			}
			case R.id.about_feedback_item:{
				Intent intent = new Intent(AboutActivity.this, AboutFeedbackActivity.class);
				startActivityForResult(intent, REQ_ABOUT_FEEDBACK);
				break;
			}
			case R.id.about_version_item:{
				/*Intent intent = new Intent(AboutActivity.this, CustomSettingsActivity.class);
				startActivityForResult(intent, REQ_ABOUT_ME);*/
				Toast.makeText(AboutActivity.this, "当前已经是最新版本", Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.about_me_item:{
				Intent intent = new Intent(AboutActivity.this, AboutMeActivity.class);
				startActivityForResult(intent, REQ_ABOUT_ME);
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
