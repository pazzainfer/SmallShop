package com.leven.app.shop.entity;

import android.graphics.Bitmap;

/**
 * 商品
 * @author Felix Lee
 * @2015年12月6日 @下午3:53:53
 */
public class Goods {
	/**
	 * 编号
	 */
	private Integer id;
	/**
	 * 商品描述
	 */
	private String name;
	/**
	 * 售价
	 */
	private Double sellPrice;
	/**
	 * 进货价
	 */
	private Double costPrice;
	/**
	 * 库存
	 */
	private Integer stock;
	/**
	 * 商品计价单位
	 */
	private String unit;
	/**
	 * 图片数据流
	 */
	private Bitmap image;
	/**
	 * 条形码
	 */
	private String barCode;
	/**
	 * 进货日期
	 */
	private Long purchaseDate;
	/**
	 * 到期日期
	 */
	private Long overdueDate;
	public Goods(){
		
	}
	public Goods(Integer id, String name, Double sellPrice, String unit, Bitmap image) {
		super();
		this.id = id;
		this.name = name;
		this.sellPrice = sellPrice;
		this.unit = unit;
		this.image = image;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public Long getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Long purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Long getOverdueDate() {
		return overdueDate;
	}
	public void setOverdueDate(Long overdueDate) {
		this.overdueDate = overdueDate;
	}
}
