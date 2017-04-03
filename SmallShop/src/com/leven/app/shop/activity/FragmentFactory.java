package com.leven.app.shop.activity;

import com.leven.app.shop.R;

import android.support.v4.app.Fragment;
/**
 * Fragment对象生成器
 * @author Felix Lee
 * @2015年12月18日 @下午8:54:31
 */
public class FragmentFactory {
	public static final int TAB_LIST = R.id.fragment_tab_list;
	public static final int TAB_SEARCH = R.id.fragment_tab_search;
	public static final int TAB_OVERDUE = R.id.fragment_tab_overdue;
	
	public static final int TAB_CUSTOMER_LIST = R.id.fragment_customer_tab_list;
	public static final int TAB_CUSTOMER_SEARCH = R.id.fragment_customer_tab_search;
	public static final int TAB_CUSTOMER_OVERDUE = R.id.fragment_customer_tab_overdue;
	
	public static final int TAB_BILLRECORD_LIST = R.id.fragment_billrecord_tab_list;
	public static final int TAB_BILLRECORD_SEARCH = R.id.fragment_billrecord_tab_search;
	public static final int TAB_BILLRECORD_OVERDUE = R.id.fragment_billrecord_tab_overdue;
	/**
	 * 获取指定ID所对应的Fragment对象
	 * @param id
	 * @return
	 */
    public static Fragment getInstanceById(int id) {
        Fragment fragment = null;
        switch (id) {
            case TAB_LIST:
                fragment = new GoodsListFragment();
                break;
            case TAB_SEARCH:
                fragment = new GoodsSearchFragment();
                break;
            case TAB_OVERDUE:
                fragment = new GoodsOverdueListFragment();
                break;
            default:
            	fragment = new GoodsListFragment();
        }
        return fragment;
    }
	/**
	 * 获取指定ID所对应的Fragment对象
	 * @param id
	 * @return
	 */
    public static Fragment getCustomerInstanceById(int id) {
        Fragment fragment = null;
        switch (id) {
            case TAB_CUSTOMER_LIST:
                fragment = new CustomerListFragment();
                break;
            case TAB_CUSTOMER_SEARCH:
                fragment = new GoodsSearchFragment();
                break;
            case TAB_CUSTOMER_OVERDUE:
                fragment = new GoodsOverdueListFragment();
                break;
            default:
            	fragment = new GoodsListFragment();
        }
        return fragment;
    }

	/**
	 * 获取指定ID所对应的Fragment对象
	 * @param id
	 * @return
	 */
    public static Fragment getBillRecordInstanceById(int id) {
        Fragment fragment = null;
        switch (id) {
            case TAB_BILLRECORD_LIST:
                fragment = new BillRecordListFragment();
                break;
            case TAB_BILLRECORD_SEARCH:
                fragment = new GoodsSearchFragment();
                break;
            case TAB_BILLRECORD_OVERDUE:
                fragment = new GoodsOverdueListFragment();
                break;
            default:
            	fragment = new GoodsListFragment();
        }
        return fragment;
    }
}
