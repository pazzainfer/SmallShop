package com.leven.app.shop.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;
import com.leven.app.shop.activity.CustomerListViewBinder;
import com.leven.app.shop.entity.Customer;
import com.leven.app.shop.util.CustomerDBHelper;
import com.leven.app.shop.util.UIUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerSelectDialog extends Dialog {
	private int pageNo = 1;
	private List<HashMap<String, Object>> mListData;
	private ListView mListView;
    private View mLoadMoreView;
    private Button mLoadMoreButton;
    private EditText mSearchText;
    private Button mSearchBtn;
	private SimpleAdapter mListAdapter;
	
	private Context mContext;
	
	private Customer mCustomer;
	private OnCustomerDialogListener mOnCustomerDialogListener;
    
	private static final String[] ADAPTER_KEYS = {"customer_sex", "customer_name", "customer_address", "customer_id"};
	
	private static final float WIDTH_SCALE = 0.8f, HEIGHT_SCALE = 0.8f;

	public CustomerSelectDialog(Context context, OnCustomerDialogListener onCustomerDialogListener) {
		super(context, R.style.CustomSelectDialog);
		this.mContext = context;
		this.mOnCustomerDialogListener = onCustomerDialogListener;
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	public void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_customer_select, null);
		mListView = (ListView) view.findViewById(R.id.dialog_custsearch_list);
		mLoadMoreView = inflater.inflate(R.layout.list_item_load, null);
		mSearchText = (EditText)view.findViewById(R.id.dialog_custsearch_text);
		mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.list_loadmore);
		mSearchBtn = (Button)view.findViewById(R.id.dialog_custsearch_btn);
        mLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.reloading), Toast.LENGTH_SHORT).show();
                loadMoreData();
            }  
        });
        mListView.addFooterView(mLoadMoreView);
        setAdapter();
		mListView.setEmptyView(view.findViewById(R.id.dialog_custsearch_list_empty));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> mItemData = (Map<String, Object>)parent.getItemAtPosition(position);
				Integer _id = (Integer)mItemData.get("customer_id");
				String _name = (String)mItemData.get("customer_name");
				setCustomer(new Customer(_id, _name));
				closeDialog();
				mOnCustomerDialogListener.back();
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
		this.setContentView(view);
	}

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
				CustomerDBHelper customerDBHelper = new CustomerDBHelper(getContext(), null);
				List<Customer> list = customerDBHelper.findCustomerByPage(getContext(), mSearchText.getText().toString(), ++pageNo);
				Customer mCustomer;
				for (int i = 0; i < list.size(); i++) {
					mCustomer = list.get(i);
					HashMap<String, Object> hashMap = putCustomerToMap(mCustomer);
					mListData.add(hashMap);
				}
				if(customerDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
		            mListView.removeFooterView(mLoadMoreView);
				}
				mListAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}.execute();
	}
	
	/**
	 * 初始化时为列表设置适配器
	 */
	private void setAdapter(){
		mListData = new ArrayList<HashMap<String, Object>>();

		CustomerDBHelper customerDBHelper = new CustomerDBHelper(getContext(), null);
		List<Customer> list = customerDBHelper.findCustomerByPage(getContext(), mSearchText.getText().toString(), pageNo);
		Customer mCustomer;
		for (int i = 0; i < list.size(); i++) {
			mCustomer = list.get(i);
			HashMap<String, Object> hashMap = putCustomerToMap(mCustomer);
			mListData.add(hashMap);
		}
		if(customerDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
            mListView.removeFooterView(mLoadMoreView);
		}
		mListAdapter = new SimpleAdapter(getContext(), mListData, R.layout.dialog_customer_select_item, ADAPTER_KEYS,
				new int[] {R.id.dialog_customer_select_item_image, R.id.dialog_customer_select_item_name, R.id.dialog_customer_select_item_address,
						R.id.dialog_customer_select_item_id});
		mListAdapter.setViewBinder(new CustomerListViewBinder());
		mListView.setAdapter(mListAdapter);
	}

	private HashMap<String, Object> putCustomerToMap(Customer mCustomer){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(ADAPTER_KEYS[0], mCustomer.getSex());
		hashMap.put(ADAPTER_KEYS[1], mCustomer.getName());
		hashMap.put(ADAPTER_KEYS[2], mCustomer.getAddress());
		hashMap.put(ADAPTER_KEYS[3], mCustomer.getId());
		return hashMap;
	}
	
	//关闭dialog的方法
    private void closeDialog() {
        try {
            java.lang.reflect.Field field = this.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(this, true);
            this.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void show() {
    	super.show();
    	Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
    	WindowManager.LayoutParams params = dialogWindow.getAttributes();
    	WindowManager wm = dialogWindow.getWindowManager();
    	int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();
    	params.x = (int) (windowWidth * (1 - WIDTH_SCALE) / 2); // 新位置X坐标
    	params.y = (int) (windowHeight * (1 - HEIGHT_SCALE) / 2); // 新位置Y坐标
    	params.width = (int) (windowWidth * WIDTH_SCALE); // 宽度
    	params.height = (int) (windowHeight * HEIGHT_SCALE); // 高度
    	dialogWindow.setAttributes(params);
    }

	public Customer getCustomer() {
		return mCustomer;
	}

	public void setCustomer(Customer customer) {
		this.mCustomer = customer;
	}
	
	public interface OnCustomerDialogListener{
        public void back();
	}
}