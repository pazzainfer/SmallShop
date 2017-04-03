package com.leven.app.shop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private static final int REQ_SETTING = 0;
	private static final int REQ_ABOUT = 1;
	/** DrawerLayout */
	private DrawerLayout mDrawerLayout;
	/** 左边栏菜单 */
	private ListView mMenuListView;
	/** 菜单列表 */
	private String[] mMenuTitles;
	private int[] mMenuIcons;
	/** 菜单打开/关闭状态 */
	private boolean isDirection_left = false;
	private View showView;
	private List<Map<String, Object>> mData;
	private SimpleAdapter mAdapter;
	
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mMenuListView = (ListView) findViewById(R.id.left_drawer);
		this.showView = mMenuListView;

		// 初始化菜单列表
		mMenuTitles = getResources().getStringArray(R.array.menu_text_array);
		mMenuIcons = new int[]{R.drawable.drawer_item_goods, R.drawable.drawer_item_customer,
				R.drawable.drawer_item_billrecord, R.drawable.drawer_item_setting, R.drawable.drawer_item_about};
		
		mData = new ArrayList<Map<String, Object>>();
		Map<String, Object> mMap;
		for(int i=0;i<mMenuTitles.length;i++){
			mMap = new HashMap<String, Object>();
			mMap.put("item_text", mMenuTitles[i]);
			mMap.put("item_icon", mMenuIcons[i]);
			mData.add(mMap);
		}
		mAdapter = new SimpleAdapter(this, mData, R.layout.drawer_list_item, 
				new String[]{"item_text", "item_icon"}, new int[]{R.id.drawer_list_item_text, R.id.drawer_list_item_icon});
		mAdapter.setViewBinder(new CustomViewBinder());
		mMenuListView.setAdapter(mAdapter);
		mMenuListView.setOnItemClickListener(new DrawerItemClickListener());
		// 设置抽屉打开时，主要内容区被自定义阴影覆盖
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// 设置ActionBar可见，并且切换菜单和内容视图
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerLayout.setDrawerListener(new DrawerLayoutStateListener());

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exit_more_touch)
	            		, Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

	/**
	 * ListView上的Item点击事件
	 * 
	 */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/**
	 * DrawerLayout状态变化监听
	 */
	private class DrawerLayoutStateListener extends
			DrawerLayout.SimpleDrawerListener {
		/**
		 * 当导航菜单打开时执行
		 */
		@Override
		public void onDrawerOpened(android.view.View drawerView) {
			if (drawerView == mMenuListView) {
				isDirection_left = true;
			}
		}

		/**
		 * 当导航菜单关闭时执行
		 */
		@Override
		public void onDrawerClosed(android.view.View drawerView) {
			if (drawerView == mMenuListView) {
				isDirection_left = false;
			}
		}
	}

	/**
	 * 切换主视图区域的Fragment
	 * 
	 * @param position
	 */
	private void selectItem(int position) {
		switch (position) {
			case 0:
				changeFragment(GoodsTabFragment.class, position);
				break;
			case 1:
				changeFragment(CustomerListFragment.class, position);
				break;
			case 2:
				changeFragment(BillRecordListFragment.class, position);
				break;
			case 3:
				startActivityForResult(new Intent(this, CustomSettingsActivity.class), REQ_SETTING);
				return;
			case 4:
				startActivityForResult(new Intent(this, AboutActivity.class), REQ_ABOUT);
				return;
			default:
				break;
		}
	}
	
	private void changeFragment(Class<? extends Fragment> cla, int position){
		try {
			Bundle args = new Bundle();
			String title = mMenuTitles[position];
			args.putString("key", title);
			Fragment fragment = cla.newInstance();
			fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// 更新选择后的item和title，然后关闭菜单
			mMenuListView.setItemChecked(position, true);
			setTitle(mMenuTitles[position]);
			mDrawerLayout.closeDrawer(mMenuListView);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode == REQ_SETTING || requestCode == REQ_ABOUT){
			// 更新选择后的item和title，然后关闭菜单
			mMenuListView.setItemChecked(0, true);
			setTitle(mMenuTitles[0]);
		}
	}

	/**
	 * 点击ActionBar上菜单
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			if (showView == mMenuListView) {
				if (!isDirection_left) { // 左边栏菜单关闭时，打开
					mDrawerLayout.openDrawer(mMenuListView);
				} else {// 左边栏菜单打开时，关闭
					mDrawerLayout.closeDrawer(mMenuListView);
				}
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void closeDrawer(){
		mDrawerLayout.closeDrawer(mMenuListView);
	}

	class CustomViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data, String textRepresentation) {
			if(view instanceof ImageView) {
	            ImageView imageView = (ImageView) view;
	        	if(data instanceof Integer){
		            imageView.setImageDrawable(getResources().getDrawable((Integer)data));  
		            return true;  
	        	}
	        }
			return false;
		}
	}
}
