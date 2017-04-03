package com.leven.app.shop.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.BillRecord;
import com.leven.app.shop.entity.BillRecordParcelable;
import com.leven.app.shop.swipemenu.SwipeMenu;
import com.leven.app.shop.swipemenu.SwipeMenuCreator;
import com.leven.app.shop.swipemenu.SwipeMenuItem;
import com.leven.app.shop.swipemenu.SwipeMenuListView;
import com.leven.app.shop.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.leven.app.shop.util.BillRecordDBHelper;
import com.leven.app.shop.util.Constant;
import com.leven.app.shop.util.DateUtil;
import com.leven.app.shop.util.DrawableUtil;
import com.leven.app.shop.util.UIUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BillRecordListFragment extends Fragment {
	private int pageNo = 1;
	private View view;
	private SwipeMenuListView mListView;
	private List<HashMap<String, Object>> mListData;
	private CheckListAdapter mListAdapter;
    private View mLoadMoreView;
    private Button mLoadMoreButton;
    private EditText mSearchText;
    private Button mSearchBtn;
	
	private static final String[] ADAPTER_KEYS = {"bill_state", "cust_name", "goods_name", "goods_num",
			"goods_unit", "bill_date", "bill_id", "cust_id", "clear_date", "billd_total"};
	private static final int[] TO_RESOURCES = {R.id.billrecord_list_item_image, R.id.billrecord_list_item_custname, R.id.billrecord_list_item_goodsname,
			R.id.billrecord_list_item_goodsnum,R.id.billrecord_list_item_goodsunit,R.id.billrecord_list_item_date,
			R.id.billrecord_list_item_id,R.id.billrecord_list_item_custid,R.id.billrecord_list_item_cleardate,R.id.billrecord_list_item_total};
	private static final int[] IMG_RESOURCES = {R.id.billrecord_list_arrow, R.id.billrecord_list_checkbox};
	
	private static final int REQUEST_CODE_DETAIL = 30;	
	private static final int REQUEST_CODE_ADD = 31;
	private boolean isEditState = false;
	
	private Menu mMenu;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_billrecord_list, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mListView = (SwipeMenuListView)view.findViewById(R.id.billrecord_list);
		mLoadMoreView = getActivity().getLayoutInflater().inflate(R.layout.list_item_load, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.list_loadmore);
        mSearchText = (EditText)view.findViewById(R.id.billrecord_list_search_text);
		mSearchBtn = (Button)view.findViewById(R.id.billrecord_list_search_btn);
        mLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Toast.makeText(getActivity(), getResources().getString(R.string.reloading), Toast.LENGTH_LONG).show();
                loadMoreData();
            }  
        });
        mListView.addFooterView(mLoadMoreView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setAdapter();
		mListView.setEmptyView(view.findViewById(R.id.billrecord_list_empty));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!isEditState){
					Map<String, Object> mItemData = (Map<String, Object>)parent.getItemAtPosition(position);
					Integer _id = (Integer)mItemData.get("bill_id");
					Bundle args = new Bundle();
					args.putInt("_id", _id);
					args.putInt("_position", position);
					Intent intent = new Intent(getContext(), BillRecordDetailActivity.class);
					intent.putExtras(args);
					startActivityForResult(intent, REQUEST_CODE_DETAIL);
				}
			}
		});
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				int _position = menu.getPosition();
				int _state = (Integer) mListData.get(_position).get("bill_state");
				if(_state==Constant.BILLRECORD_STATE_DEBT){
					SwipeMenuItem billClearItem = new SwipeMenuItem(
							getActivity().getApplicationContext());
					billClearItem.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_bg_green)));
					billClearItem.setWidth(DrawableUtil.dp2px(getContext(), 70));
					billClearItem.setIcon(R.drawable.swipemenu_bill_clear);
					billClearItem.setTitle(R.string.action_billrecord_clear);
					billClearItem.setTitleColor(getResources().getColor(R.color.white));
					billClearItem.setTitleSize(14);
					menu.addMenuItem(billClearItem);
				}else {
					SwipeMenuItem cancelStateItem = new SwipeMenuItem(
							getActivity().getApplicationContext());
					cancelStateItem.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_bg_blue)));
					cancelStateItem.setWidth(DrawableUtil.dp2px(getContext(), 70));
					cancelStateItem.setIcon(R.drawable.swipemenu_bill_cancel);
					cancelStateItem.setTitle(R.string.action_billrecord_cancel);
					cancelStateItem.setTitleColor(getResources().getColor(R.color.white));
					cancelStateItem.setTitleSize(14);
					menu.addMenuItem(cancelStateItem);
				}
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getActivity().getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_bg_red)));
				deleteItem.setWidth(DrawableUtil.dp2px(getContext(), 70));
				deleteItem.setIcon(R.drawable.swipemenu_delete);
				deleteItem.setTitle(R.string.action_delete);
				deleteItem.setTitleColor(getResources().getColor(R.color.white));
				deleteItem.setTitleSize(14);
				menu.addMenuItem(deleteItem);
			}
		};
		
		mListView.setMenuCreator(creator);

		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				int _state = (Integer) mListData.get(position).get("bill_state");
				switch(index){
				case 0:
					if(_state==Constant.BILLRECORD_STATE_DEBT){
						doBillClear(position);
					} else {
						doCancelBillState(position);
					}
					break;
				case 1:
					deleteBillRecord(position);
					break;
				}
			}
		});
		mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					setAdapter();
					mListAdapter.notifyDataSetChanged();
					UIUtil.hideInputImm(getContext());
				}
				return false;
			}
		});
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setAdapter();
				mListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 加载下一页客户信息数据
	 */
	private void loadMoreData(){
		new AsyncTask<Void, Void, String[]>(){
			@Override
			protected String[] doInBackground(Void... params) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				return null;
			}
			@Override
			protected void onPostExecute(String[] result) {
				BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
				List<BillRecord> list = billRecordDBHelper.findBillRecordByPage(getContext(), mSearchText.getText().toString(), ++pageNo);
				BillRecord mBillRecord;
				for (int i = 0; i < list.size(); i++) {
					mBillRecord = list.get(i);
					HashMap<String, Object> hashMap = putBillRecordToMap(mBillRecord);
					mListData.add(hashMap);
				}
				if(billRecordDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
		            mListView.removeFooterView(mLoadMoreView);
				}
				mListAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}
	/**
	 * 刷新客户信息数据
	 */
	private void refreshData(){
		new AsyncTask<Void, Void, String[]>(){
			@Override
			protected String[] doInBackground(Void... params) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				return null;
			}
			@Override
			protected void onPostExecute(String[] result) {
				BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
				pageNo = 1;
				List<BillRecord> list = billRecordDBHelper.findBillRecordByPage(getContext(), mSearchText.getText().toString(), pageNo);
				BillRecord mBillRecord;
				mListData = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					mBillRecord = list.get(i);
					HashMap<String, Object> hashMap = putBillRecordToMap(mBillRecord);
					mListData.add(hashMap);
				}
				if(billRecordDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
		            mListView.removeFooterView(mLoadMoreView);
				}else{
					mListView.addFooterView(mLoadMoreView);
				}
				mListAdapter.setCheckState(false);
				mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.billrecord_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, false);
				mListAdapter.setViewBinder(new BillRecordListViewBinder());
				mListView.setAdapter(mListAdapter);
				super.onPostExecute(result);
			}
		}.execute();
	}
	/**
	 * 初始化时为列表设置适配器
	 */
	private void setAdapter(){
		mListData = new ArrayList<HashMap<String, Object>>();

		BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
		List<BillRecord> list = billRecordDBHelper.findBillRecordByPage(getContext(), mSearchText.getText().toString(), pageNo);
		BillRecord mBillRecord;
		for (int i = 0; i < list.size(); i++) {
			mBillRecord = list.get(i);
			HashMap<String, Object> hashMap = putBillRecordToMap(mBillRecord);
			mListData.add(hashMap);
		}
		if(billRecordDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
            mListView.removeFooterView(mLoadMoreView);
		}
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.billrecord_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, false);
		mListAdapter.setViewBinder(new BillRecordListViewBinder());
		mListView.setAdapter(mListAdapter);
	}
	
	private void doCancelBillState(final int position){
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		int _settingDay = mPerferences.getInt(Constant.PREF_KEY_CANCELBILLSTATE, 5);
		long _clearDate = (Long)mListData.get(position).get(ADAPTER_KEYS[8]);
		Long _today = new Date().getTime();
		Long _areaDay = _today - _clearDate;
		Long _settingDayTime = _settingDay * 1000l * 60l * 60l * 24l;
		if(_settingDayTime < _areaDay){
			Toast.makeText(this.getContext(), getResources().getString(R.string.billrecord_cancelstate_cannot), Toast.LENGTH_LONG).show();
			return;
		}
		new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.billrecord_clear_dia_title))
        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
				final Integer _id = (Integer)mListData.get(position).get(ADAPTER_KEYS[6]);
				final BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
				billRecordDBHelper.changeBillState(_id, Constant.BILLRECORD_STATE_DEBT);
				new AsyncTask<Void, Void, String[]>(){
					@Override
					protected String[] doInBackground(Void... params) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						return null;
					}
					@Override
					protected void onPostExecute(String[] result) {
						BillRecord _billRecord = billRecordDBHelper.getBillRecordById(_id);
						HashMap<String, Object> _newData = putBillRecordToMap(_billRecord);
						mListData.remove(position);
						mListData.add(position, _newData);
						mListAdapter.notifyDataSetChanged();
						super.onPostExecute(result);
					}
				}.execute();
				Toast.makeText(getActivity(), getResources().getString(R.string.billrecord_clear_success), Toast.LENGTH_SHORT).show();
            }
        })
        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
	}
	/**
	 * 处理账单结清
	 * @param position
	 */
	private void doBillClear(final int position){
		new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.billrecord_clear_dia_title))
        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
				final Integer _id = (Integer)mListData.get(position).get("bill_id");
				final BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
				billRecordDBHelper.changeBillState(_id, Constant.BILLRECORD_STATE_CLEAR);
				new AsyncTask<Void, Void, String[]>(){
					@Override
					protected String[] doInBackground(Void... params) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						return null;
					}
					@Override
					protected void onPostExecute(String[] result) {
						BillRecord _billRecord = billRecordDBHelper.getBillRecordById(_id);
						HashMap<String, Object> _newData = putBillRecordToMap(_billRecord);
						mListData.remove(position);
						mListData.add(position, _newData);
						mListAdapter.notifyDataSetChanged();
						super.onPostExecute(result);
					}
				}.execute();
				Toast.makeText(getActivity(), getResources().getString(R.string.billrecord_clear_success), Toast.LENGTH_SHORT).show();
            }
        })
        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
	}
	private void deleteBillRecord(final int position){
		new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.billrecord_delete_dia_title))
        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	HashMap<String, Object> _deleteData = mListData.get(position);
				Integer _id = (Integer)_deleteData.get("bill_id");
				BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
				billRecordDBHelper.delete(_id);
				mListData.remove(position);
				mListAdapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
	}
	
	/**
	 * 如果mHasMenu为false，那么是不会执行onCreateOptionsMenu(menu, inflater)方法的，也就是不会添加fragment的menu菜单
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.billrecord_list, menu);
		mMenu = menu;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.action_billrecord_add){
			Intent intent = new Intent(getContext(), BillRecordAddActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD);
		} else if(id==R.id.action_billrecord_edit){
			if(mListData.isEmpty()){
				Toast.makeText(getActivity(), getResources().getString(R.string.note_list_empty), Toast.LENGTH_LONG).show();
			}else {
				editStateChange();
			}
		} else if(id==R.id.action_billrecord_cancel){
			editStateChange();
		} else if(id==R.id.action_billrecord_delete){
			new AlertDialog.Builder(getActivity())
	        .setTitle(getResources().getString(R.string.billrecord_delete_dia_title))
	        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Map<Integer, Boolean> checkMap = mListAdapter.getCheckMap();
	            	Iterator<Integer> checkIterator = checkMap.keySet().iterator();
	            	List<Integer> selectedIdList = new ArrayList<Integer>();
	            	while(checkIterator.hasNext()){
	            		selectedIdList.add((Integer)mListData.get(checkIterator.next()).get("bill_id"));
	            	}
	            	BillRecordDBHelper billRecordDBHelper = new BillRecordDBHelper(getActivity(), null);
	            	billRecordDBHelper.deleteBatch(selectedIdList);
					Toast.makeText(getActivity(), getResources().getString(R.string.billrecord_delete_success), Toast.LENGTH_LONG).show();
					editStateChange();
					refreshData();
	            }
	        })
	        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void editStateChange() {
		mMenu.findItem(R.id.action_billrecord_edit).setEnabled(isEditState);
		mMenu.findItem(R.id.action_billrecord_add).setEnabled(isEditState);
		mMenu.findItem(R.id.action_billrecord_cancel).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_billrecord_delete).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_billrecord_edit).setVisible(isEditState);
		mMenu.findItem(R.id.action_billrecord_add).setVisible(isEditState);
		mMenu.findItem(R.id.action_billrecord_cancel).setVisible(!isEditState);
		mMenu.findItem(R.id.action_billrecord_delete).setVisible(!isEditState);
		mListAdapter.setCheckState(!isEditState);
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.billrecord_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, !isEditState);
		mListAdapter.setViewBinder(new BillRecordListViewBinder());
		mListView.setAdapter(mListAdapter);
		this.setEditState(!isEditState);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			if(requestCode == REQUEST_CODE_DETAIL){
				final int position = data.getIntExtra("_position", -1);
				BillRecordParcelable billRecordParcelable = data.getParcelableExtra("_billRecord");
				final BillRecord mBillRecord = billRecordParcelable.getBillRecord();
				if(position > -1){
					new AsyncTask<Void, Void, String[]>(){
						@Override
						protected String[] doInBackground(Void... params) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
							}
							return null;
						}
						@Override
						protected void onPostExecute(String[] result) {
							HashMap<String, Object> hashMap = putBillRecordToMap(mBillRecord);
							mListData.remove(position);
							mListData.add(position, hashMap);
							mListAdapter.notifyDataSetChanged();
							super.onPostExecute(result);
						}
					}.execute();
				}
			} else if(requestCode == REQUEST_CODE_ADD){
				refreshData();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private HashMap<String, Object> putBillRecordToMap(BillRecord mBillRecord){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(ADAPTER_KEYS[0], mBillRecord.getState());
		hashMap.put(ADAPTER_KEYS[1], mBillRecord.getCustomerName());
		hashMap.put(ADAPTER_KEYS[2], mBillRecord.getGoodsName());
		hashMap.put(ADAPTER_KEYS[3], mBillRecord.getGoodsNum());
		hashMap.put(ADAPTER_KEYS[4], mBillRecord.getGoodsUnit());
		hashMap.put(ADAPTER_KEYS[5], DateUtil.transLongToDate(mBillRecord.getDate()));
		hashMap.put(ADAPTER_KEYS[6], mBillRecord.getId());
		hashMap.put(ADAPTER_KEYS[7], mBillRecord.getCustomerId());
		hashMap.put(ADAPTER_KEYS[8], mBillRecord.getClearDate());
		hashMap.put(ADAPTER_KEYS[9], mBillRecord.getTotal());
		return hashMap;
	}
	
	public boolean isEditState() {
		return isEditState;
	}
	public void setEditState(boolean isEditState) {
		this.isEditState = isEditState;
	}
	
	class BillRecordListViewBinder implements com.leven.app.shop.activity.CheckListAdapter.ViewBinder {  
	    @Override  
	    public boolean setViewValue(View view, Object data,  
	            String textRepresentation) {
	        if(view instanceof ImageView) {
	        	ImageView imageView = (ImageView) view;
	        	if(data!=null && data instanceof Integer && ((Integer)data)==1){
	        		imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.billrecord_state_clear));
	        	} else {
	        		imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.billrecord_state_debt));
	        	}
	        	return true;
	        }
	        return false;
	    }
	      
	}
}
