package com.leven.app.shop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.Goods;
import com.leven.app.shop.entity.GoodsParcelable;
import com.leven.app.shop.swipemenu.SwipeMenu;
import com.leven.app.shop.swipemenu.SwipeMenuCreator;
import com.leven.app.shop.swipemenu.SwipeMenuItem;
import com.leven.app.shop.swipemenu.SwipeMenuListView;
import com.leven.app.shop.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.leven.app.shop.util.DateUtil;
import com.leven.app.shop.util.DrawableUtil;
import com.leven.app.shop.util.GoodsDBHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class GoodsListFragment extends Fragment {
	private int pageNo = 1;
	private View view;
	private SwipeMenuListView mListView;
	private List<HashMap<String, Object>> mListData;
	private CheckListAdapter mListAdapter;
    private View mLoadMoreView;
    private Button mLoadMoreButton;
	
	private static final String[] ADAPTER_KEYS = {"goods_image", "goods_name", "goods_price", "goods_unit", "goods_date", "goods_id"};

	private static final int[] TO_RESOURCES = {R.id.main_goods_list_item_image, R.id.main_goods_list_item_name, R.id.main_goods_list_item_price,
			R.id.main_goods_list_item_unit, R.id.goods_list_item_date, R.id.goods_list_item_id};
	private static final int[] IMG_RESOURCES = {R.id.goods_list_arrow, R.id.goods_list_checkbox};
	
	private static final int REQUEST_CODE_DETAIL = 0;
	private static final int REQUEST_CODE_ADD = 1;
	private static final int REQUEST_CODE_ADD_BILL = 2;
	private boolean isEditState = false;
	
	private Menu mMenu;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_goods_list, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mListView = (SwipeMenuListView)view.findViewById(R.id.main_goods_list);
		mLoadMoreView = getActivity().getLayoutInflater().inflate(R.layout.list_item_load, null);
        mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.list_loadmore);
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
		mListView.setEmptyView(view.findViewById(R.id.main_goods_list_empty));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!isEditState){
					Map<String, Object> mItemData = (Map<String, Object>)parent.getItemAtPosition(position);
					Integer _id = (Integer)mItemData.get("goods_id");
					Bundle args = new Bundle();
					args.putInt("_id", _id);
					args.putInt("_position", position);
					Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
					intent.putExtras(args);
					startActivityForResult(intent, REQUEST_CODE_DETAIL);
				}
			}
		});
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem billAddItem = new SwipeMenuItem(
						getActivity().getApplicationContext());
				billAddItem.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_bg_blue)));
				billAddItem.setWidth(DrawableUtil.dp2px(getContext(), 70));
				billAddItem.setIcon(R.drawable.swipemenu_bill_add);
				billAddItem.setTitle(R.string.title_activity_billrecord_add);
				billAddItem.setTitleColor(getResources().getColor(R.color.white));
				billAddItem.setTitleSize(14);
				menu.addMenuItem(billAddItem);
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
				switch(index){
				case 0:
					startBillRecordAdd(position);
					break;
				case 1:
					deleteGoods(position);
					break;
				}
			}
		});
	}
	
	/**
	 * 加载下一页商品信息数据
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
				GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
				List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), ++pageNo);
				Goods mGoods;
				for (int i = 0; i < list.size(); i++) {
					mGoods = list.get(i);
					HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
					mListData.add(hashMap);
				}
				if(goodsDBHelper.isLastPage(getContext(), pageNo)){
		            mListView.removeFooterView(mLoadMoreView);
				}
				mListAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}
	/**
	 * 刷新商品信息数据
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
				GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
				pageNo = 1;
				List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), pageNo);
				Goods mGoods;
				mListData = new ArrayList<HashMap<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					mGoods = list.get(i);
					HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
					mListData.add(hashMap);
				}
				if(goodsDBHelper.isLastPage(getContext(), pageNo)){
		            mListView.removeFooterView(mLoadMoreView);
				}else{
					mListView.addFooterView(mLoadMoreView);
				}
				mListAdapter.setCheckState(false);
				mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.goods_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, false);
				mListAdapter.setViewBinder(new GoodsListViewBinder());
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

		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
		List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), pageNo);
		Goods mGoods;
		for (int i = 0; i < list.size(); i++) {
			mGoods = list.get(i);
			HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
			mListData.add(hashMap);
		}
		if(goodsDBHelper.isLastPage(getContext(), pageNo)){
            mListView.removeFooterView(mLoadMoreView);
		}
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.goods_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, false);
		mListAdapter.setViewBinder(new GoodsListViewBinder());
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 根据商品信息添加账单
	 * @param position
	 */
	private void startBillRecordAdd(int position) {
		HashMap<String, Object> mItemData = mListData.get(position);
		Bundle args = new Bundle();
		args.putInt("_from", BillRecordAddActivity.FROM_GOODS);
		args.putInt("_position", position);
		args.putInt("goods_id", (Integer)mItemData.get("goods_id"));
		args.putString("goods_name", (String)mItemData.get("goods_name"));
		args.putString("goods_unit", (String)mItemData.get("goods_unit"));
		args.putDouble("goods_price", (Double)mItemData.get("goods_price"));
		Intent intent = new Intent(getContext(), BillRecordAddActivity.class);
		intent.putExtras(args);
		startActivityForResult(intent, REQUEST_CODE_ADD_BILL);
	}
	
	private void deleteGoods(final int position){
		new AlertDialog.Builder(getActivity())
        .setTitle(getResources().getString(R.string.goods_delete_dia_title))
        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	HashMap<String, Object> _deleteData = mListData.get(position);
				Integer _id = (Integer)_deleteData.get("goods_id");
				GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
				goodsDBHelper.delete(_id);
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
		inflater.inflate(R.menu.goods_list, menu);
		mMenu = menu;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.action_add){
			Intent intent = new Intent(getContext(), GoodsAddActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD);
		} else if(id==R.id.action_edit){
			if(mListData.isEmpty()){
				Toast.makeText(getActivity(), getResources().getString(R.string.note_list_empty), Toast.LENGTH_LONG).show();
			}else {
				editStateChange();
			}
		} else if(id==R.id.action_cancel){
			editStateChange();
		} else if(id==R.id.action_delete){
			new AlertDialog.Builder(getActivity())
	        .setTitle(getResources().getString(R.string.goods_delete_dia_title))
	        .setPositiveButton(getResources().getString(R.string.options_sure), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Map<Integer, Boolean> checkMap = mListAdapter.getCheckMap();
	            	Iterator<Integer> checkIterator = checkMap.keySet().iterator();
	            	List<Integer> selectedIdList = new ArrayList<Integer>();
	            	while(checkIterator.hasNext()){
	            		selectedIdList.add((Integer)mListData.get(checkIterator.next()).get("goods_id"));
	            	}
	            	GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
	            	goodsDBHelper.deleteBatch(selectedIdList);
					Toast.makeText(getActivity(), getResources().getString(R.string.goods_delete_success), Toast.LENGTH_LONG).show();
					editStateChange();
					refreshData();
	            }
	        })
	        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void editStateChange() {
		mMenu.findItem(R.id.action_edit).setEnabled(isEditState);
		mMenu.findItem(R.id.action_add).setEnabled(isEditState);
		mMenu.findItem(R.id.action_cancel).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_delete).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_edit).setVisible(isEditState);
		mMenu.findItem(R.id.action_add).setVisible(isEditState);
		mMenu.findItem(R.id.action_cancel).setVisible(!isEditState);
		mMenu.findItem(R.id.action_delete).setVisible(!isEditState);
		mListAdapter.setCheckState(!isEditState);
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.goods_list_item, ADAPTER_KEYS,TO_RESOURCES,IMG_RESOURCES, !isEditState);
		mListAdapter.setViewBinder(new GoodsListViewBinder());
		mListView.setAdapter(mListAdapter);
		this.setEditState(!isEditState);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			if(requestCode == REQUEST_CODE_DETAIL){
				final int position = data.getIntExtra("_position", -1);
				GoodsParcelable goodsParcelable = data.getParcelableExtra("_goods");
				final Goods mGoods = goodsParcelable.getGoods();
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
							HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
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
	
	private HashMap<String, Object> putGoodsToMap(Goods mGoods){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(ADAPTER_KEYS[0], mGoods.getImage());
		hashMap.put(ADAPTER_KEYS[1], mGoods.getName());
		hashMap.put(ADAPTER_KEYS[2], mGoods.getSellPrice());
		hashMap.put(ADAPTER_KEYS[3], mGoods.getUnit());
		hashMap.put(ADAPTER_KEYS[4], DateUtil.transLongToDate(mGoods.getPurchaseDate()));
		hashMap.put(ADAPTER_KEYS[5], mGoods.getId());
		return hashMap;
	}
	
	public boolean isEditState() {
		return isEditState;
	}
	public void setEditState(boolean isEditState) {
		this.isEditState = isEditState;
	}
}
