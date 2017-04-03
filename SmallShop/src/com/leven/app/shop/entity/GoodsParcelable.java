package com.leven.app.shop.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class GoodsParcelable implements Parcelable {
	private Goods goods;
	public static final Parcelable.Creator<GoodsParcelable> CREATOR = new Parcelable.Creator<GoodsParcelable>() {
		@Override
		public GoodsParcelable createFromParcel(Parcel source) {
			return new GoodsParcelable(source);
		}
		@Override
		public GoodsParcelable[] newArray(int size) {
			return new GoodsParcelable[size];
		}
	};

	public GoodsParcelable(Goods goods) {
		super();
		this.goods = goods;
	}
	/**
	 * Parcel以流的形式读取，所以，写入跟读取都要同样的顺序，若遇到数组属性时，先读取readInt来得到数组大小，再依次读取数组内数据
	 * @param in
	 */
	private GoodsParcelable(Parcel in) {
		goods = new Goods();
		goods.setId(in.readInt());
		goods.setStock(in.readInt());
		goods.setName(in.readString());
		goods.setUnit(in.readString());
		goods.setBarCode(in.readString());
		goods.setSellPrice(in.readDouble());
		goods.setCostPrice(in.readDouble());
		goods.setPurchaseDate(in.readLong());
		goods.setOverdueDate(in.readLong());
		goods.setImage((Bitmap) in.readParcelable(Bitmap.class.getClassLoader()));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(goods.getId());
		dest.writeInt(goods.getStock());
		dest.writeString(goods.getName());
		dest.writeString(goods.getUnit());
		dest.writeString(goods.getBarCode());
		dest.writeDouble(goods.getSellPrice());
		dest.writeDouble(goods.getCostPrice());
		dest.writeLong(goods.getPurchaseDate());
		dest.writeLong(goods.getOverdueDate());
		dest.writeParcelable(goods.getImage(), PARCELABLE_WRITE_RETURN_VALUE);
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}
}
