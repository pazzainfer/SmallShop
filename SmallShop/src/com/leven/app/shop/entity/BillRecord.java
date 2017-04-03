package com.leven.app.shop.entity;

public class BillRecord {
	/**
	 * 流水号
	 */
	private Integer id;
	/**
	 * 客户ID
	 */
	private Integer customerId;
	/**
	 * 客户姓名
	 */
	private String customerName;
	/**
	 * 商品数目
	 */
	private Integer goodsNum;
	/**
	 * 结清标识
	 */
	private Integer state = 0;
	/**
	 * 商品编号
	 */
	private Integer goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品单位
	 */
	private String goodsUnit;
	/**
	 * 商品价格
	 */
	private Double goodsPrice;
	/**
	 * 总价
	 */
	private Double total;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 日期
	 */
	private Long date;
	/**
	 * 结清日期
	 */
	private Long clearDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public Double getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(Double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public String getGoodsUnit() {
		return goodsUnit;
	}
	public void setGoodsUnit(String goodsUnit) {
		this.goodsUnit = goodsUnit;
	}
	public Integer getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public Long getClearDate() {
		return clearDate;
	}
	public void setClearDate(Long clearDate) {
		this.clearDate = clearDate;
	}
}
