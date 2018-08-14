package model;

import java.util.Date;

public class Account {

	private Integer id;
	private String account;
	private double amount;
	private Date createDatetime;
	
	public Account(String account, Integer amount) {
		super();
		this.account = account;
		this.amount = amount;
	}
	
	public Account(Integer id, String account, double amount) {
		super();
		this.id = id;
		this.account = account;
		this.amount = amount;
	}



	public Account() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", account=" + account + ", amount=" + amount + ", createDatetime="
				+ createDatetime + "]";
	}

}
