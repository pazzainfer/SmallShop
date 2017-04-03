package com.leven.app.shop.entity;

public class Customer {
	/**
	 * 编号
	 */
	private Integer id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 备注
	 */
	private String remarks;
	public Customer() {
		
	}
	public Customer(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
