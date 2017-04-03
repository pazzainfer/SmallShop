package com.leven.app.shop.widget;

import com.leven.app.shop.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 自定义设置组件
 * @author Felix Lee
 * @2015年12月24日 @上午1:34:53
 */
public class CustomPerfView extends RelativeLayout {
	// 命名空间，在引用这个自定义组件的时候，需要用到
    private String namespace = "http://schemas.android.com/apk/res/com.leven.app.shop";
    public static final int PREF_TYPE_TEXT = 0;
    public static final int PREF_TYPE_LIST = 1;
    
    public static final int PREF_INPUTTYPE_STRING = 0;
    public static final int PREF_INPUTTYPE_DOUBLE = 1;
    public static final int PREF_INPUTTYPE_INT = 2;
    public static final int PREF_INPUTTYPE_DATE = 3;
    public static final int PREF_INPUTTYPE_DATETIME = 4;
    
    private int defaulBgId = R.drawable.item_corner_bg_selector;
    
    private View mView;
    private TextView mtitleText;
    private TextView mSummaryText;
    
    private Object mValue;
    
    private String key;	//偏好Key值
    private String title;	//偏好标题
    private String summary;	//偏好简介
    private String defaultValue;	//默认值
    private int type;	//偏好类型（0:text，1:list）
    private int inputType;	//偏好文本数据类型(stringType:0;doubleType:1;intType:2;dateType:3;datetimeType:4)
    private CharSequence[] listEntries;	//偏好List选项值

	public CustomPerfView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 将自定义组合控件的布局渲染成View
		mView = View.inflate(context, R.layout.custom_perference_layout, this);
        mtitleText = (TextView) mView.findViewById(R.id.custom_pref_title);
        mSummaryText = (TextView) mView.findViewById(R.id.custom_pref_summary);
        
        TypedArray a = context.obtainStyledAttributes(attrs,     
                R.styleable.CustomPrefView);
        key = attrs.getAttributeValue(namespace, "key");
        title = attrs.getAttributeValue(namespace, "title");
        if(title!=null && title.startsWith("@")){	//Strings.xml文件资源
        	title = getResources().getString(Integer.parseInt(title.substring(1)));
        }
        summary = attrs.getAttributeValue(namespace, "summary");
        defaultValue = attrs.getAttributeValue(namespace, "defaultValue");
        if(summary!=null && summary.startsWith("@")){	//Strings.xml文件资源
        	summary = getResources().getString(Integer.parseInt(summary.substring(1)));
        }
        type = attrs.getAttributeIntValue(namespace, "type", PREF_TYPE_TEXT);
        inputType = attrs.getAttributeIntValue(namespace, "inputType", PREF_INPUTTYPE_STRING);
        listEntries = a.getTextArray(R.styleable.CustomPrefView_listEntries);

        Drawable mDrawable = a.getDrawable(R.styleable.CustomPrefView_background);
        if(mDrawable!=null){
        	mView.setBackground(mDrawable);
        }else{
        	mView.setBackground(getResources().getDrawable(defaulBgId));
        }
        
        mtitleText.setText(title);
        mValue = getPreferenceValue();
        if(mValue!=null){
        	mSummaryText.setText(mValue.toString());
        } else {
        	if(defaultValue==null){
        		mSummaryText.setText(summary);
        	} else {
        		mSummaryText.setText(defaultValue);
        	}
        }
        if(type==PREF_TYPE_LIST){
        	initListAction();
        } else {
        	initTextInputAction();
        }
	}
	/**
	 * 初始化文本输入处理
	 */
	private void initTextInputAction() {
		mView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText inputServer = new EditText(getContext());
				mValue = getPreferenceValue();
				if(mValue!=null){
					inputServer.setText(getPreferenceValue().toString());
				}else if(defaultValue!=null){
					inputServer.setText(defaultValue);
				}
				if(inputType==PREF_INPUTTYPE_DOUBLE){
					inputServer.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
				} else if(inputType==PREF_INPUTTYPE_INT){
					inputServer.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_NORMAL);
				} else if(inputType==PREF_INPUTTYPE_DATE){
					inputServer.setInputType(EditorInfo.TYPE_CLASS_DATETIME | EditorInfo.TYPE_DATETIME_VARIATION_DATE);
				} else if(inputType==PREF_INPUTTYPE_DATETIME){
					inputServer.setInputType(EditorInfo.TYPE_CLASS_DATETIME | EditorInfo.TYPE_DATETIME_VARIATION_TIME);
				} else {
					inputServer.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle(title).setView(inputServer)
						.setNegativeButton(R.string.options_cancle, null);
				builder.setPositiveButton(R.string.options_sure, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mSummaryText.setText(inputServer.getText().toString());
						persistPreference(inputServer.getText().toString());
					}
				});
				builder.show();
			}
		});
	}
	/**
	 * 初始化列表选择处理
	 */
	private void initListAction() {
		mView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setTitle(title).setItems(listEntries, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSummaryText.setText(listEntries[which]);
						persistPreference(listEntries[which].toString());
					}
				});
				builder.show();
			}
		});
	}
	/**
	 * 返回指定SharedPreferences所保存的值
	 * @return
	 */
	private Object getPreferenceValue(){
        SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!mPerferences.contains(key)){
        	return null;
        }
        if(inputType==PREF_INPUTTYPE_DOUBLE){
        	float _defaultValue = 0;
			try{
				_defaultValue = Float.parseFloat(defaultValue);
			}catch(Exception e){}
        	return mPerferences.getFloat(key, _defaultValue);
		} else if(inputType==PREF_INPUTTYPE_INT){
			int _defaultValue = 0;
			try{
				_defaultValue = Integer.parseInt(defaultValue);
			}catch(Exception e){}
			return mPerferences.getInt(key, _defaultValue);
		} else if(inputType==PREF_INPUTTYPE_DATE || inputType==PREF_INPUTTYPE_DATETIME){
        	long _defaultValue = 0;
			try{
				_defaultValue = Long.parseLong(defaultValue);
			}catch(Exception e){}
			return mPerferences.getLong(key, _defaultValue);
		} else {
			return mPerferences.getString(key, defaultValue);
		}
	}
	/**
	 * 持久化数据
	 * @param value
	 */
	private void persistPreference(String value){
        SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Editor editor = mPerferences.edit();
        if(inputType==PREF_INPUTTYPE_DOUBLE){
        	editor.putFloat(key, Float.parseFloat(value));
		} else if(inputType==PREF_INPUTTYPE_INT){
			editor.putInt(key, Integer.parseInt(value));
		} else if(inputType==PREF_INPUTTYPE_DATE || inputType==PREF_INPUTTYPE_DATETIME){
			editor.putLong(key, Long.parseLong(value));
		} else {
			editor.putString(key, value);
		}
        editor.commit();
	}
}
