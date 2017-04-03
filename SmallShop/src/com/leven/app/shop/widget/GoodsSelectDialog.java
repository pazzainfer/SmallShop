package com.leven.app.shop.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leven.app.shop.R;
import com.leven.app.shop.entity.Goods;
import com.leven.app.shop.util.DrawableUtil;
import com.leven.app.shop.util.GoodsDBHelper;
import com.leven.app.shop.util.UIUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsSelectDialog extends Dialog {
	private int pageNo = 1;
	private List<HashMap<String, Object>> mListData;
	private ListView mListView;
    private View mLoadMoreView;
    private Button mLoadMoreButton;
    private EditText mSearchText;
    private Button mSearchBtn;
	private SimpleAdapter mListAdapter;
	
	private Context mContext;
	
	private Goods mGoods;
	private OnGoodsDialogListener mOnGoodsDialogListener;
    
	private static final String[] ADAPTER_KEYS = {"goods_img", "goods_name", "goods_price", "goods_unit", "goods_id"};
	
	private static final float WIDTH_SCALE = 0.8f, HEIGHT_SCALE = 0.8f;

	public GoodsSelectDialog(Context context, OnGoodsDialogListener onGoodsDialogListener) {
		super(context, R.style.CustomSelectDialog);
		this.mContext = context;
		this.mOnGoodsDialogListener = onGoodsDialogListener;
	}
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	public void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_goods_select, null);
		mListView = (ListView) view.findViewById(R.id.dialog_goodssearch_list);
		mLoadMoreView = inflater.inflate(R.layout.list_item_load, null);
		mSearchText = (EditText)view.findViewById(R.id.dialog_goodssearch_text);
		mLoadMoreButton = (Button)mLoadMoreView.findViewById(R.id.list_loadmore);
		mSearchBtn = (Button)view.findViewById(R.id.dialog_goodssearch_btn);
        mLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.reloading), Toast.LENGTH_SHORT).show();
                loadMoreData();
            }  
        });
        mListView.addFooterView(mLoadMoreView);
        setAdapter();
		mListView.setEmptyView(view.findViewById(R.id.dialog_goodssearch_list_empty));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> mItemData = (Map<String, Object>)parent.getItemAtPosition(position);
				Integer _id = (Integer)mItemData.get("goods_id");
				String _name = (String)mItemData.get("goods_name");
				Bitmap _img = (Bitmap)mItemData.get("goods_img");
				Double _price = (Double)mItemData.get("goods_price");
				String _unit = (String)mItemData.get("goods_unit");
				setGoods(new Goods(_id, _name, _price, _unit, _img));
				closeDialog();
				mOnGoodsDialogListener.back();
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
				GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getContext(), null);
				List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), mSearchText.getText().toString(), ++pageNo);
				Goods mGoods;
				for (int i = 0; i < list.size(); i++) {
					mGoods = list.get(i);
					HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
					mListData.add(hashMap);
				}
				if(goodsDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
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

		GoodsDBHelper goodsDBHelper = new GoodsDBHelper(getContext(), null);
		List<Goods> list = goodsDBHelper.findGoodsByPage(getContext(), mSearchText.getText().toString(), pageNo);
		Goods mGoods;
		for (int i = 0; i < list.size(); i++) {
			mGoods = list.get(i);
			HashMap<String, Object> hashMap = putGoodsToMap(mGoods);
			mListData.add(hashMap);
		}
		if(goodsDBHelper.isLastPage(getContext(), mSearchText.getText().toString(), pageNo)){
            mListView.removeFooterView(mLoadMoreView);
		}
		
		mListAdapter = new SimpleAdapter(getContext(), mListData, R.layout.dialog_goods_select_item, ADAPTER_KEYS,
				new int[] {R.id.dialog_goods_select_item_image, R.id.dialog_goods_select_item_name, R.id.dialog_goods_select_item_price,
						R.id.dialog_goods_select_item_unit, R.id.dialog_goods_select_item_id});
		mListAdapter.setViewBinder(new CustomListViewBinder());
		mListView.setAdapter(mListAdapter);
	}

	private HashMap<String, Object> putGoodsToMap(Goods mGoods){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(ADAPTER_KEYS[0], mGoods.getImage());
		hashMap.put(ADAPTER_KEYS[1], mGoods.getName());
		hashMap.put(ADAPTER_KEYS[2], mGoods.getSellPrice());
		hashMap.put(ADAPTER_KEYS[3], mGoods.getUnit());
		hashMap.put(ADAPTER_KEYS[4], mGoods.getId());
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

	public Goods getGoods() {
		return mGoods;
	}

	public void setGoods(Goods goods) {
		this.mGoods = goods;
	}

	public interface OnGoodsDialogListener{
        public void back();
	}
	
	private class CustomListViewBinder implements ViewBinder{
		@Override
	    public boolean setViewValue(View view, Object data,
	            String textRepresentation) {
	        if(view instanceof ImageView) {
	            ImageView imageView = (ImageView) view;
	            int size = DrawableUtil.dp2px(getContext(), 72);
	        	if(data instanceof Bitmap){
		            Bitmap bmp = (Bitmap) data;
		            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, size, size));
		            return true;
	        	} else if(data == null){
	        		Bitmap bmp = DrawableUtil.getBitmapFromDrawable(getContext(), R.drawable.picture_add);
		            imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bmp, size, size));
	        		return true;
	        	}
	        }
	        return false;
		}
	}
}