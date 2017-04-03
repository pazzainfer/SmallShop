package com.leven.app.shop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.zxing.client.android.CaptureActivity;
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
import com.leven.app.shop.util.UIUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

public class GoodsSearchFragment extends Fragment {
	private View view;
	
	private ImageView mCameraImage;
	private EditText mSearchText;
	private Button mSearchBtn;
	private SwipeMenuListView mListView;
	private List<HashMap<String, Object>> mListData;
	private CheckListAdapter mListAdapter;
    private View mLoadMoreView;
    private Button mLoadMoreButton;
	
	private int pageNo = 1;
	private static final String[] ADAPTER_KEYS = {"goods_image", "goods_name", "goods_price", "goods_unit", "goods_date", "goods_id"};

	private static final int REQUEST_CODE_DETAIL = 0;
	private static final int REQUEST_CODE_ADD = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	private boolean isEditState = false;
	
	private Menu mMenu;
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_goods_search, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mCameraImage = (ImageView)view.findViewById(R.id.search_btn_camera);
		mSearchText = (EditText)view.findViewById(R.id.search_text);
		mSearchBtn = (Button)view.findViewById(R.id.search_btn_search);
		mListView = (SwipeMenuListView)view.findViewById(R.id.search_goods_list);
		mListData = new ArrayList<HashMap<String, Object>>();
		mListView.setEmptyView(view.findViewById(R.id.search_goods_list_empty));

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
		
		mCameraImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doCameraScan();
			}
		});
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					searchRefreshList();
					UIUtil.hideInputImm(getActivity());
				}
				return false;
			}
		});
        mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchRefreshList();
			}
		});
        mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!isEditState){
					Map<String, Object> mItemData = (Map<String, Object>)parent.getItemAtPosition(position);
					Integer _id = (Integer)mItemData.get("goods_id");
					String mTitle = getResources().getString(R.string.action_detail);
					Bundle args = new Bundle();
					args.putString("key", mTitle);
					args.putInt("_id", _id);
					Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
					intent.putExtras(args);
					startActivityForResult(intent, REQUEST_CODE_DETAIL);
				}
			}
		});
        SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getActivity().getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.menu_bg_red)));
				// set item width
				deleteItem.setWidth(DrawableUtil.dp2px(getContext(), 90));
				// set a icon
				deleteItem.setIcon(R.drawable.swipemenu_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				deleteGoods(position);
			}
		});
	}
	/**
	 * 打开相机扫描条码功能
	 */
	protected void doCameraScan() {
		try{
			Intent openCameraIntent = new Intent(getActivity(), CaptureActivity.class);
			startActivityForResult(openCameraIntent, REQUEST_CODE_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从数据库加载下一页数据，并刷新到列表上显示
	 */
	protected void loadMoreData() {
		String _searchText = mSearchText.getText().toString();
		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
		List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), _searchText,++pageNo);
		Goods mGoods;
		for (int i = 0; i < list.size(); i++) {
			mGoods = list.get(i);
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put(ADAPTER_KEYS[0], mGoods.getImage());
			hashMap.put(ADAPTER_KEYS[1], mGoods.getName());
			hashMap.put(ADAPTER_KEYS[2], mGoods.getSellPrice());
			hashMap.put(ADAPTER_KEYS[3], mGoods.getUnit());
			hashMap.put(ADAPTER_KEYS[4], DateUtil.transLongToDate(mGoods.getPurchaseDate()));
			hashMap.put(ADAPTER_KEYS[5], mGoods.getId());
			mListData.add(hashMap);
		}
        Toast.makeText(this.getActivity(), getResources().getString(R.string.reloading), Toast.LENGTH_SHORT).show();
		mListAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化时为列表设置适配器
	 */
	private void setAdapter(){
		mListData = new ArrayList<HashMap<String, Object>>();

		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
		List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), mSearchText.getText().toString(), pageNo);
		Goods mGoods;
		for (int i = 0; i < list.size(); i++) {
			mGoods = list.get(i);
			HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
			mListData.add(hashMap);
		}
		if(goodsDBHelper.isLastPage(getContext(), pageNo)){
            mListView.removeFooterView(mLoadMoreView);
		}
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.goods_list_item, ADAPTER_KEYS,
				new int[] {R.id.main_goods_list_item_image, R.id.main_goods_list_item_name, R.id.main_goods_list_item_price,
						R.id.main_goods_list_item_unit, R.id.goods_list_item_date, R.id.goods_list_item_id},
				new int[]{R.id.goods_list_arrow, R.id.goods_list_checkbox}, false);
		mListAdapter.setViewBinder(new GoodsListViewBinder());
		mListView.setAdapter(mListAdapter);
	}

	/**
	 * 搜索功能刷新结果列表
	 */
	private void searchRefreshList(){
		mListData = new ArrayList<HashMap<String, Object>>();
		String _searchText = mSearchText.getText().toString();
		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getActivity(), null);
		List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), _searchText,pageNo);
		Goods mGoods;
		for (int i = 0; i < list.size(); i++) {
			mGoods = list.get(i);
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put(ADAPTER_KEYS[0], mGoods.getImage());
			hashMap.put(ADAPTER_KEYS[1], mGoods.getName());
			hashMap.put(ADAPTER_KEYS[2], mGoods.getSellPrice());
			hashMap.put(ADAPTER_KEYS[3], mGoods.getUnit());
			hashMap.put(ADAPTER_KEYS[4], DateUtil.transLongToDate(mGoods.getPurchaseDate()));
			hashMap.put(ADAPTER_KEYS[5], mGoods.getId());
			mListData.add(hashMap);
		}
		pageNo = 1;
        Toast.makeText(this.getActivity(), getResources().getString(R.string.reloading), Toast.LENGTH_SHORT).show();
        setAdapter();
		UIUtil.hideInputImm(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			if (requestCode == REQUEST_CODE_CAMERA) {
	            Bundle bundle = data.getExtras();
	            String scanResult = bundle.getString("result");
	            mSearchText.setText(scanResult);
	        }else if(requestCode == REQUEST_CODE_DETAIL){
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
				searchRefreshList();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		inflater.inflate(R.menu.search, menu);
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
					searchRefreshList();
	            }
	        })
	        .setNegativeButton(getResources().getString(R.string.options_cancle), null).show();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void editStateChange() {
		mMenu.findItem(R.id.action_edit).setEnabled(isEditState);
		mMenu.findItem(R.id.action_cancel).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_delete).setEnabled(!isEditState);
		mMenu.findItem(R.id.action_edit).setVisible(isEditState);
		mMenu.findItem(R.id.action_cancel).setVisible(!isEditState);
		mMenu.findItem(R.id.action_delete).setVisible(!isEditState);
		mListAdapter.setCheckState(!isEditState);
		mListAdapter = new CheckListAdapter(getActivity(), mListData, R.layout.goods_list_item, ADAPTER_KEYS,
				new int[] {R.id.main_goods_list_item_image, R.id.main_goods_list_item_name, R.id.main_goods_list_item_price,
						R.id.main_goods_list_item_unit, R.id.goods_list_item_date, R.id.goods_list_item_id},
				new int[]{R.id.goods_list_arrow, R.id.goods_list_checkbox}, !isEditState);
		mListAdapter.setViewBinder(new GoodsListViewBinder());
		mListView.setAdapter(mListAdapter);
		this.setEditState(!isEditState);
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
