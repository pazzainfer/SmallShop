package com.leven.app.shop.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomerParcelable implements Parcelable {
	private Customer customer;
	public static final Parcelable.Creator<CustomerParcelable> CREATOR = new Parcelable.Creator<CustomerParcelable>() {
		@Override
		public CustomerParcelable createFromParcel(Parcel source) {
			return new CustomerParcelable(source);
		}
		@Override
		public CustomerParcelable[] newArray(int size) {
			return new CustomerParcelable[size];
		}
	};

	public CustomerParcelable(Customer customer) {
		super();
		this.customer = customer;
	}
	/**
	 * Parcel以流的形式读取，所以，写入跟读取都要同样的顺序，若遇到数组属性时，先读取readInt来得到数组大小，再依次读取数组内数据
	 * @param in
	 */
	private CustomerParcelable(Parcel in) {
		customer = new Customer();
		customer.setId(in.readInt());
		customer.setName(in.readString());
		customer.setAddress(in.readString());
		customer.setSex(in.readString());
		customer.setRemarks(in.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(customer.getId());
		dest.writeString(customer.getName());
		dest.writeString(customer.getAddress());
		dest.writeString(customer.getSex());
		dest.writeString(customer.getRemarks());
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
