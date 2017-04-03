package com.leven.app.shop.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BillRecordParcelable implements Parcelable {
	private BillRecord billRecord;
	public static final Parcelable.Creator<BillRecordParcelable> CREATOR = new Parcelable.Creator<BillRecordParcelable>() {
		@Override
		public BillRecordParcelable createFromParcel(Parcel source) {
			return new BillRecordParcelable(source);
		}
		@Override
		public BillRecordParcelable[] newArray(int size) {
			return new BillRecordParcelable[size];
		}
	};

	public BillRecordParcelable(BillRecord billRecord) {
		super();
		this.billRecord = billRecord;
	}
	/**
	 * Parcel以流的形式读取，所以，写入跟读取都要同样的顺序，若遇到数组属性时，先读取readInt来得到数组大小，再依次读取数组内数据
	 * @param in
	 */
	private BillRecordParcelable(Parcel in) {
		billRecord = new BillRecord();
		billRecord.setId(in.readInt());
		billRecord.setCustomerId(in.readInt());
		billRecord.setGoodsId(in.readInt());
		billRecord.setGoodsNum(in.readInt());
		billRecord.setState(in.readInt());
		billRecord.setGoodsName(in.readString());
		billRecord.setGoodsUnit(in.readString());
		billRecord.setRemarks(in.readString());
		billRecord.setCustomerName(in.readString());
		billRecord.setGoodsPrice(in.readDouble());
		billRecord.setTotal(in.readDouble());
		billRecord.setDate(in.readLong());
		billRecord.setClearDate(in.readLong());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(billRecord.getId());
		dest.writeInt(billRecord.getCustomerId());
		dest.writeInt(billRecord.getGoodsId());
		dest.writeInt(billRecord.getGoodsNum());
		dest.writeInt(billRecord.getState());
		dest.writeString(billRecord.getGoodsName());
		dest.writeString(billRecord.getGoodsUnit());
		dest.writeString(billRecord.getRemarks());
		dest.writeString(billRecord.getCustomerName());
		dest.writeDouble(billRecord.getGoodsPrice());
		dest.writeDouble(billRecord.getTotal());
		dest.writeLong(billRecord.getDate());
		dest.writeLong(billRecord.getClearDate());
	}

	public BillRecord getBillRecord() {
		return billRecord;
	}

	public void setBillRecord(BillRecord billRecord) {
		this.billRecord = billRecord;
	}
}
