package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GoodsTabFragment extends Fragment {
    private FragmentManager fragmentManager;
	private View view;
    private RadioGroup mRadioGroup;
    private RadioButton mListButton;
    private int mCheckedId = FragmentFactory.TAB_LIST;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_goods_tab, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
        fragmentManager = getFragmentManager();
        mRadioGroup = (RadioGroup) view.findViewById(R.id.main_content_tab);
        mListButton = (RadioButton) view.findViewById(R.id.fragment_tab_list);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            	((RadioButton)view.findViewById(mCheckedId)).setChecked(false);
            	changeTab(checkedId);
                ((RadioButton)view.findViewById(checkedId)).setChecked(true);
                mCheckedId = checkedId;
            }
        });
        mListButton.setChecked(true);
	}
	/**
	 * 切换标签页
	 * @param tabId
	 */
	private void changeTab(int tabId){
		FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceById(tabId);
        transaction.replace(R.id.main_content_tab_layout, fragment);
        transaction.commit();
	}
}
